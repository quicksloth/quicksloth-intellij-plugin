package View;

import Controllers.Utils.CodeExtractor;
import Models.RecommendedCodes;
import Models.RequestCode;
import Models.Codes;
import Network.NetworkService;

import View.Components.StripedProgressBarUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentFactory.SERVICE;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by PamelaPeixinho on 26/03/17.
 */
public class MainToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {

    private JPanel root;
    private JTextField queryField;
    private JButton searchButton;
    private JPanel mainContent;
    private JPanel header;
    private JLabel title;
    private JProgressBar loading;
    private JPanel resultsArea;
    private JTextPane explain;
    private JButton InsertCode;
    private JButton CopyClipboard;
    private ToolWindow myToolWindow;

    public MainToolWindowFactory() {
        Color primaryColor = new JBColor(new Color(232, 111, 86), new Color(247, 76, 34));
        this.loading.setForeground(primaryColor);
        System.out.println("MainToolWindowFactory");
    }

    private void searchPerformed(Project project) {
        System.out.println("queryField " + queryField.getText());
        this.startLoading();

        RequestCode requestCode = new RequestCode(queryField.getText());

        if (project != null) {
            requestCode = new CodeExtractor().extractAST(project, requestCode);
        }

        if (requestCode != null ) {
            System.out.println(requestCode.getComments());
            System.out.println(requestCode.getLanguage());
            System.out.println(requestCode.getLibs());
            System.out.println(requestCode.getQuery());
            NetworkService.getCodeRecommendation(requestCode, this);
        }

//        class MyWorker extends SwingWorker {
//            protected String doInBackground() {
//                try {
//                    Thread.sleep(600);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                showResults();
//                return "done";
//            }
//        }
//        new MyWorker().execute();
    }

    public void showResults(RecommendedCodes resultCodes) {
        resultsArea.setLayout(new BoxLayout(resultsArea, BoxLayout.Y_AXIS));

        for (Codes code: resultCodes.getCodes()) {
            JPanel newPanel = getPanel(code);
            resultsArea.add(newPanel);
        }

//        JPanel codeResults = new JPanel();
//        codeResults.setBounds(resultsArea.getBounds());
//        codeResults.add(newPanel);
//        codeResults.add(newPanel2);
//        codeResults.add(newPanel3);
//        codeResults.add(newPanel4);
//        resultsArea.add(codeResults);

        resultsArea.setVisible(true);
        resultsArea.revalidate();
        resultsArea.repaint();

        stopLoading();
    }

    private void stopLoading() {
        loading.setVisible(false);
        searchButton.enable();
        queryField.enable();
    }

    private void startLoading() {
        this.resultsArea.setVisible(false);
        this.loading.setVisible(true);
        this.searchButton.disable();
        this.queryField.disable();
    }

    private JPanel getPanel(Codes code) {
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        newPanel.setBounds(resultsArea.getBounds());
        newPanel.setBorder(new TitledBorder("Code Score: " + (code.getScore()*100) + "%"));
        newPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        newPanel = addCodeLines(newPanel, code);
        return newPanel;
    }

    private JPanel addCodeLines(JPanel panel, Codes code) {
        String[] codeLines = code.getCodeText().split("\n");
        for (String line: codeLines) {
            JCheckBox newCB = new JCheckBox();
            newCB.setLabel(line);
            newCB.setActionCommand(line);
            panel.add(newCB);
        }
        return panel;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        System.out.println("createToolWindowContent");
        System.out.println(toolWindow.isActive());

        this.loading.setUI(new StripedProgressBarUI(false, true));
        toolWindowSetup(toolWindow);

        setupCTAs(project);

        this.explain.setBackground(new Color(255, 255, 255, 0));
    }

    private void setupCTAs(@NotNull final Project project) {
        searchButton.addActionListener(e -> searchPerformed(project));

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

    private void toolWindowSetup(@NotNull ToolWindow toolWindow) {
        this.myToolWindow = toolWindow;
        ContentFactory contentFactory = SERVICE.getInstance();
        Content content = contentFactory.createContent(root, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
