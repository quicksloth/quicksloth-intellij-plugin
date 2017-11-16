package View;

import Controllers.Utils.CodeExtractor;
import Models.Codes;
import Models.RecommendedCodes;
import Models.RequestCode;
import Network.NetworkService;
import View.Components.ModernScrollPane;
import View.Components.StripedProgressBarUI;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentFactory.SERVICE;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by PamelaPeixinho on 26/03/17.
 */
public class MainToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {

    static public String ID = "QuickSloth";
    static public String selectAllName = "selectAll";

    private long startTime;
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
    private JPanel searchPanel;
    private JPanel emptyState;
    private JLabel emptyIcon;
    private JTextPane emptyDesc;
    private ToolWindow myToolWindow;

    public MainToolWindowFactory() {
        Color primaryColor = new JBColor(new Color(232, 111, 86), new Color(247, 76, 34));
        this.loading.setForeground(primaryColor);
        networkService = new NetworkService();
        System.out.println("MainToolWindowFactory");
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        System.out.println("createToolWindowContent");
        setupToolWindow(toolWindow);
        setupCTAs(project);
        setupUIDetails();
        resultsArea.setVisible(false);
        scroll.setBorder(null);
    }

    private void setupToolWindow(@NotNull ToolWindow toolWindow) {
        this.myToolWindow = toolWindow;
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
        emptyState.setVisible(false);
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
            startTime = System.currentTimeMillis();
            System.out.println("queryField " + queryField.getText());
            this.startLoading();

            RequestCode requestCode = new RequestCode(queryField.getText());

            if (project != null) {
                requestCode = new CodeExtractor().extractAST(project, requestCode);
            }

            if (requestCode != null) {
                networkService.getCodeRecommendation(requestCode, this::showResults, this::showGenericErrorDialog);
            }
        } catch (Error e) {
            this.showGenericErrorDialog();
        }
    }

    private void cancelPerfomed() {
        networkService.cancelEventDisconnecting(this::cancelSearch);
    }

    public Boolean showResults(RecommendedCodes resultCodes) {
        try {
            if (resultCodes.getCodes().size() > 0) {
                long endTime   = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(totalTime);
                System.out.println("TOTAL TIME = " +  totalSeconds  + "s");
                createResultsUI(resultCodes, totalSeconds);
            } else {
                showEmptyResult();
            }

        } catch (Exception e) {
            this.showGenericErrorDialog();
        }
        
        stopLoading();
        return true;
    }

    private void showEmptyResult () {
        emptyDesc.setBackground(new Color(255, 255, 255, 0));
        emptyState.setLayout(new GridLayout(2, 1));
        emptyState.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyState.setVisible(true);
    }

    private void createResultsUI(RecommendedCodes resultCodes, long totalSeconds) {
        resultsArea.setBounds(mainContent.getBounds());
        resultsArea.setLayout(new BoxLayout(resultsArea, BoxLayout.Y_AXIS));
        resultsArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsArea.setAlignmentY(Component.TOP_ALIGNMENT);

        codesArea.setLayout(new BoxLayout(codesArea, BoxLayout.Y_AXIS));
        codesArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        codesArea.setAlignmentY(Component.TOP_ALIGNMENT);

        explain.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        String explainTextBase = "The following results were found as recommendation for your search and code, " +
                "each one has one score. Select the desired code lines and click on the button to add these " +
                "in your code or copy to your clipboard";
        explain.setText(explainTextBase + " (" + totalSeconds + " segundos)");

        int height = 0;
        int width = 10;

        for (Codes code : resultCodes.getCodes()) {
            JPanel newPanel = getPanel(code);
            height += newPanel.getHeight();
            width = Math.max(width, newPanel.getWidth());

            newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (Component component : newPanel.getComponents()) {
                if (component instanceof JCheckBox) {
                    ((JCheckBox) component).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
            }

            codesArea.add(newPanel);
        }

        codesArea.setPreferredSize(new Dimension(width, height));
        codesArea.setMinimumSize(new Dimension(width, height));
        codesArea.setMaximumSize(new Dimension(width, height));

        resultsArea.setVisible(true);
        resultsArea.revalidate();
        resultsArea.repaint();
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
        JLabel urlDesc = new JLabel();
        urlDesc.setEnabled(true);
        urlDesc.setText("<html>Url: <a href=\"\" color=\"LightGray\">" + code.getSourceLink() + "</a></html>");
        urlDesc.setCursor(new Cursor(Cursor.HAND_CURSOR));

        goToSourceLink(code, urlDesc);

        newPanel.add(urlDesc);
        return newPanel;
    }

    private void goToSourceLink(final Codes code, JLabel urlDesc) {
        urlDesc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    try {
                        Desktop.getDesktop().browse(new URI(code.getSourceLink()));
                    } catch (IOException ex) {
                        showGenericErrorDialog();
                    }
                } catch (URISyntaxException ex) {
                    showGenericErrorDialog();
                }
            }
        });
    }

    private JPanel setupResultPanel(Codes code, JPanel newPanel) {
        float score = code.getScore() * 100;
        double round = Math.floor(score);

        if ((score - round) > 0) {
            newPanel.setBorder(new TitledBorder("Code Score: " +
                    String.format("%.1f", score) + "%"));
        } else {
            DecimalFormat dcf = new DecimalFormat ("##");
            newPanel.setBorder(new TitledBorder("Code Score: " + dcf.format(round) + "%"));
        }

        return newPanel;
    }

    private JPanel addCodeLines(Codes code, JPanel panel) {
        String[] codeLines = code.getCodeText().split("\n");
        int maxLineWidth = 0;

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);

        JCheckBox selectAllCB = new JCheckBox("SELECT ALL");
        selectAllCB.setBorderPainted(true);
        selectAllCB.setName(selectAllName);
        selectAllCB.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel codeCBs = new JPanel();
        codeCBs.setName("checkboxes");
        codeCBs.setLayout(new GridLayout(codeLines.length, 0));
        codeCBs.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        codeCBs.setBorder(new EmptyBorder(0, 8, 0, 0));

        for (String line : codeLines) {
            JCheckBox newCB = new JCheckBox();
            newCB.setLabel(line);
            newCB.setActionCommand(line);
            newCB.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            codeCBs.add(newCB);
            maxLineWidth = Math.max(maxLineWidth, this.getLinesWidth(line));
        }

        selectAllCB.addActionListener((e) -> {
            for (Component component: codeCBs.getComponents()) {
                if (component instanceof JCheckBox) {
                    ((JCheckBox) component).setSelected(selectAllCB.isSelected());
                }
            }
        });

        int width = getCodeWidth(code, maxLineWidth);
        int codeHeight = (codeLines.length * 21);
        int height = 57 + codeHeight;

        codeCBs.setSize(width, codeHeight);

        panel.add(selectAllCB);
        panel.add(codeCBs);

        panel.setSize(width, height);
        return panel;
    }

    private int getLinesWidth(String line) {
        return line.length() * 8;
    }

    private int getCodeWidth(Codes code, int maxLinesWidth) {
        return Math.max(this.getLinesWidth(code.getSourceLink()), maxLinesWidth);
    }

    private void insertSelectedCode(Project project) {
        String code = getSelectedCode();
        insertCodeInFile(project, code);
        unselectCode();
    }

    private void copySelectedCodeToClipboard() {
        try {
            String code = getSelectedCode();
            StringSelection stringSelection = new StringSelection(code);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            unselectCode();
        } catch (Exception e) {
            this.showGenericErrorDialog();
        }
    }

    @NotNull
    private String getSelectedCode() {
        String code = "";
        for (Component component : this.codesArea.getComponents()) {
            if (component instanceof JPanel) {
                for (Component childComponents : ((JPanel) component).getComponents()) {
                    if (childComponents instanceof JPanel) {
                        for (Component cbComponents : ((JPanel) childComponents).getComponents()) {
                            if (cbComponents instanceof JCheckBox) {
                                JCheckBox checkBox = ((JCheckBox) cbComponents);
                                if (checkBox.isSelected()) {
                                    code += (checkBox.getText() + "\n");
                                }
                            }
                        }
                    }

                }
            }
        }

//      \r throws error to add in code file. Solved: replace \r for 4 spaces
        code = code.replaceAll("\r", "    ");

        return code;
    }

    private void unselectCode() {
        for (Component component : this.codesArea.getComponents()) {
            if (component instanceof JPanel) {
                for (Component childComponents : ((JPanel) component).getComponents()) {
                    if (childComponents instanceof JPanel) {
                        for (Component cbComponents : ((JPanel) childComponents).getComponents()) {
                            if (cbComponents instanceof JCheckBox) {
                                JCheckBox checkBox = ((JCheckBox) cbComponents);
                                checkBox.setSelected(false);
                            }
                        }
                    } else if (childComponents instanceof JCheckBox) {
                            JCheckBox checkBox = ((JCheckBox) childComponents);
                            checkBox.setSelected(false);
                    }
                }
            }
        }
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

                    String emptyString = " ";
                    String newCode = "";
                    int caretCount = 0;

                    for (String codeLine: code.split("\n")) {
                        String space = "";

                        if (caretCount > 0)
                            space = String.join("", Collections.nCopies(caretCount, emptyString));

                        newCode += space + codeLine + "\n";
                        caretCount = editor.getCaretModel().getCurrentCaret().getLogicalPosition().column;
                    }

                    document.insertString(cursorOffset, newCode);
                }
            }.execute();
        } catch (Exception e) {
            this.showGenericErrorDialog();
        }
    }

    public void showGenericErrorDialog() {
        try {
            new WriteCommandAction(ProjectManager.getInstance().getDefaultProject()) {
                @Override
                protected void run(@NotNull Result result) throws Throwable {
                    Messages.showErrorDialog("Some unexpected error occurred, press OK and try again later", "Error");
                    cancelPerfomed();
                }
            }.execute();
        } catch (Exception e) {
            System.out.print(e);
            cancelPerfomed();
        }
    }

    private void createUIComponents() {
        scroll = new ModernScrollPane(codesArea);
    }
}
