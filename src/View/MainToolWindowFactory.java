package View;

import Controllers.Utils.CodeExtractor;
import Models.RequestCode;
import Network.NetworkService;
import View.Components.ProgressCircleUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentFactory.SERVICE;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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
        this.loading.setIndeterminate(true);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        System.out.println("createToolWindowContent");
        System.out.println(toolWindow.isActive());

//      TOOLWINDOW SETUP
        this.myToolWindow = toolWindow;
        ContentFactory contentFactory = SERVICE.getInstance();
        Content content = contentFactory.createContent(root, "", false);
        toolWindow.getContentManager().addContent(content);

//      LOADING SETUP
        this.loading.setUI(new ProgressCircleUI());
        this.loading.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.loading.setStringPainted(true);
        this.loading.setFont(this.loading.getFont().deriveFont(24f));
        this.loading.setForeground(Color.ORANGE);


//      CTAs SETUP
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
}
