package View;

import Controllers.Utils.CodeExtractor;
import Models.RequestCode;
import Network.NetworkService;
import View.Components.StripedProgressBarUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
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
    private ToolWindow myToolWindow;

    public MainToolWindowFactory() {
        Color primaryColor = new JBColor(new Color(232, 111, 86), new Color(247, 76, 34));
        this.loading.setForeground(primaryColor);
        JScrollPane scrollPane = new JBScrollPane(mainContent,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scrollPane);
        root.revalidate();
        root.repaint();
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
            NetworkService.getCodeRecommendation(requestCode);
        }
    }

    private void startLoading() {
        this.loading.setVisible(true);
        this.searchButton.disable();
        this.queryField.disable();

        JPanel newPanel = new JPanel();
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        newPanel.setBounds(resultsArea.getBounds());
        newPanel.setToolTipText("Result 1");
        newPanel.setBorder(new TitledBorder("Result 1"));
        newPanel = addCodeLines(newPanel);
        resultsArea.add(newPanel);
        resultsArea.setVisible(true);
        resultsArea.revalidate();
        resultsArea.repaint();
        this.loading.setVisible(false);

//        this.loading.setVisible(false);
//        class MyWorker extends SwingWorker {
//            protected String doInBackground() {
//                // Do my downloading code
//                return "Done.";
//            }
//            protected void done() {
//                loading.setVisible(false);
//            }
//        }
//        new MyWorker().execute();
    }

    private JPanel addCodeLines(JPanel panel) {
        for (Integer i = 0 ; i < 20; i++) {
            JCheckBox newCB = new JCheckBox();
            newCB.setLabel("Check check " + i.toString());
            newCB.setActionCommand(i.toString());
            panel.add(newCB);
        }
        return panel;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        System.out.println("createToolWindowContent");
        System.out.println(toolWindow.isActive());
//      TODO: extract to functions


        this.loading.setUI(new StripedProgressBarUI(false, true));
        toolWindowSetup(toolWindow);

//      CTAs SETUP
        setupCTAs(project);
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
