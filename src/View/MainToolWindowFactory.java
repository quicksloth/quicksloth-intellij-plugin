package View;

import Controllers.Utils.CodeExtractor;
import Models.Codes;
import Models.RecommendedCodes;
import Models.RequestCode;
import Network.NetworkService;
import View.Components.StripedProgressBarUI;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentFactory.SERVICE;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * Created by PamelaPeixinho on 26/03/17.
 */
public class MainToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {

    static public String ID = "QuickSloth";
    private NetworkService networkService;

    private JPanel root;
    private JTextField queryField;
    private JButton searchButton;
    private JPanel mainContent;
    private JPanel header;
    private JLabel title;
    private JProgressBar loading;
    private JPanel resultsArea;
    private JTextPane explain;
    private JButton insertCode;
    private JButton copyClipboard;
    private JScrollPane scroll;
    private JPanel resultButtons;
    private JPanel codesArea;
    private ToolWindow myToolWindow;

    public MainToolWindowFactory() {
        Color primaryColor = new JBColor(new Color(232, 111, 86), new Color(247, 76, 34));
        this.loading.setForeground(primaryColor);
        networkService = new NetworkService();
        System.out.println("MainToolWindowFactory");
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        setupToolWindow(toolWindow);
        setupCTAs(project);
        setupUIDetails();
    }

    private void setupToolWindow(@NotNull ToolWindow toolWindow) {
        this.myToolWindow = toolWindow;
        this.myToolWindow.hide(null);
        ContentFactory contentFactory = SERVICE.getInstance();
        Content content = contentFactory.createContent(root, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void setupCTAs(@NotNull final Project project) {
        searchButton.setText(ButtonType.Search.toString());
        searchButton.addActionListener(e -> searchButtonEventListener(project));

        insertCode.addActionListener(e -> insertSelectedCode(project));
        copyClipboard.addActionListener(e -> copySelectedCodeToClipboard());

        queryField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == '\n') {
                    System.out.println("Enter Pressed");
                    searchPerformed(project);
                }
            }
        });
    }

    private void searchButtonEventListener(@NotNull Project project) {
        if (Objects.equals(searchButton.getText(), ButtonType.Search.toString())) {
            searchPerformed(project);
        } else if (Objects.equals(searchButton.getText(), ButtonType.Cancel.toString())) {
            cancelPerfomed();
        }
    }

    private void setupUIDetails() {
        this.loading.setUI(new StripedProgressBarUI(false, true));
        this.explain.setBackground(new Color(255, 255, 255, 0));
    }

    public void showToolWindow() {
        this.myToolWindow.show(null);
    }

    public void setQuery(String query) {
        this.queryField.setText(query);
    }

    private void startLoading() {
        codesArea.removeAll();
        mainContent.setPreferredSize(new Dimension(300, 95));
        resultsArea.setVisible(false);
        loading.setVisible(true);
        searchButton.disable();
        queryField.disable();
        searchButton.setText(ButtonType.Cancel.toString());
    }

    private void stopLoading() {
        loading.setVisible(false);
        searchButton.enable();
        queryField.enable();
        searchButton.setText(ButtonType.Search.toString());
    }

    public void searchPerformed(Project project) {
        try {

            System.out.println("queryField " + queryField.getText());
            this.startLoading();

            RequestCode requestCode = new RequestCode(queryField.getText());

            if (project != null) {
                requestCode = new CodeExtractor().extractAST(project, requestCode);
            }

            if (requestCode != null) {
                networkService.getCodeRecommendation(requestCode, this::showResults, this::showGenericErrorDialog);
            }
        }
        catch (Exception e) {
            this.showGenericErrorDialog();
        }
    }

    private void cancelPerfomed() {
        networkService.cancelEventDisconnecting(this::cancelSearch);
    }

    public Boolean showResults(RecommendedCodes resultCodes) {
        try {
            createResultsUI(resultCodes);
        } catch (Exception e) {
            this.showGenericErrorDialog();
        }

        stopLoading();
        return true;
    }

    private void createResultsUI(RecommendedCodes resultCodes) {
        resultsArea.setBounds(mainContent.getBounds());
        resultsArea.setLayout(new BoxLayout(resultsArea, BoxLayout.Y_AXIS));
        resultsArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultsArea.setAlignmentY(Component.TOP_ALIGNMENT);

        codesArea.setLayout(new BoxLayout(codesArea, BoxLayout.Y_AXIS));
        codesArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        codesArea.setAlignmentY(Component.TOP_ALIGNMENT);

        explain.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultButtons.setAlignmentX(Component.LEFT_ALIGNMENT);

        int height = 115;
        int width = 10;

        for (Codes code: resultCodes.getCodes()) {
            JPanel newPanel = getPanel(code);

            height += newPanel.getHeight();
            System.out.println(height);

            width = Math.max(width, newPanel.getWidth());

            newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (Component component: newPanel.getComponents()) {
                if (component instanceof JCheckBox) {
                    ((JCheckBox) component).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
            }

            codesArea.add(newPanel);
        }

        mainContent.setPreferredSize(new Dimension(width, height));
        mainContent.setMinimumSize(new Dimension(width, height));
        mainContent.setMaximumSize(new Dimension(width, height));

        resultsArea.setVisible(true);

        mainContent.revalidate();
        mainContent.repaint();
    }

    public void cancelSearch() {
        this.stopLoading();
        queryField.setText("");
        resultsArea.setVisible(false);
    }

    private JPanel getPanel(Codes code) {
        JPanel newPanel = new JPanel();
        newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        newPanel = setupResultPanel(code, newPanel);
        newPanel = addCodeLines(code, newPanel);
        newPanel = addCodeUrl(code, newPanel);

        return newPanel;
    }

    private JPanel addCodeUrl(Codes code, JPanel newPanel) {
        JTextArea urlDesc = new JTextArea();
        urlDesc.setEnabled(true);
        urlDesc.setDragEnabled(false);
        urlDesc.setEditable(false);
        urlDesc.setText("Url: " + code.getSourceLink());
        newPanel.add(urlDesc);
        return newPanel;
    }

    private JPanel setupResultPanel(Codes code, JPanel newPanel) {
//        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        newPanel.setBorder(new TitledBorder("Code Score: " + (code.getScore() * 100) + "%"));
        return newPanel;
    }

    private JPanel addCodeLines(Codes code, JPanel panel) {
        String[] codeLines = code. getCodeText().split("\n");
        int maxLineWidth = 0;

        panel.setLayout(new GridLayout(codeLines.length + 1, 0));
        panel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        for (String line: codeLines) {
            JCheckBox newCB = new JCheckBox();
            newCB.setLabel(line);
            newCB.setActionCommand(line);
            newCB.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            panel.add(newCB);
            maxLineWidth = Math.max(maxLineWidth, this.getLinesWidth(line));
        }

        int width = getCodeWidth(code, maxLineWidth);
        int height = 42 + (codeLines.length * 21);

        panel.setSize(width, height);
        return panel;
    }

    private int getLinesWidth(String line) {
        return line.length() * 9;
    }

    private int getCodeWidth(Codes code, int maxLinesWidth) {
       return Math.max(this.getLinesWidth(code.getSourceLink()), maxLinesWidth);
    }

    private void insertSelectedCode(Project project) {
        String code = getSelectedCode();
        insertCodeInFile(project, code);
    }

    private void copySelectedCodeToClipboard() {
        try {
            String code = getSelectedCode();
            StringSelection stringSelection = new StringSelection(code);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        } catch (Exception e) {
            this.showGenericErrorDialog();
        }
    }

    @NotNull
    private String getSelectedCode() {
        String code = "";
        for (Component component: this.resultsArea.getComponents()) {
            if (component instanceof JPanel) {
                for (Component childComponents: ((JPanel) component).getComponents()) {
                    if (childComponents instanceof JCheckBox) {
                        JCheckBox checkBox = ((JCheckBox) childComponents);
                        if (checkBox.isSelected()) {
                            code += (checkBox.getText() + "\n");
                        }
                    }
                }
            }
        }
        return code;
    }

    private void insertCodeInFile(final Project project, String code) {
        FileEditorManager manager = FileEditorManager.getInstance(project);
        final Editor editor = manager.getSelectedTextEditor();
        assert editor != null;
        final int cursorOffset = editor.getCaretModel().getOffset();
        final Document document = editor.getDocument();

        try {
            new WriteCommandAction(project) {
                @Override
                protected void run(@NotNull Result result) throws Throwable {
                    document.insertString(cursorOffset, code);
                }
            }.execute();
        } catch(Exception e) {
            this.showGenericErrorDialog();
        }
    }

    public void showGenericErrorDialog() {
        Messages.showErrorDialog("Some unexpected error occurred, press OK and try again later", "Error");
    }
}
