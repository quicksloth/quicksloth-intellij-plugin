package View;

import Controllers.Utils.CodeExtractor;
import Models.RequestCode;
import Network.NetworkService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentFactory.SERVICE;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by PamelaPeixinho on 26/03/17.
 */
public class MainToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {

    private JPanel root;
    private JTextField queryField;
    private JButton searchButton;
    private JPanel maincontent;
    private JPanel header;
    private JLabel title;
    private JProgressBar loading;
    private ToolWindow myToolWindow;

    public MainToolWindowFactory() {
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
        loading.setVisible(true);
        loading.setIndeterminate(true);

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

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        System.out.println("createToolWindowContent");
        System.out.println(toolWindow.isActive());
//      TODO: extract to functions

        toolWindowSetup(toolWindow);

//      LOADING SETUP
//        this.loading.setUI(new ProgressCircleUI());
        this.loading.setVisible(false);


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
