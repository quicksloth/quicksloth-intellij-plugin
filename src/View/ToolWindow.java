package View;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by pamelaiupipeixinho on 01/10/17.
 */
public class ToolWindow implements ToolWindowFactory{
    private JButton searchButton;
    private JTextField searchBox;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull com.intellij.openapi.wm.ToolWindow toolWindow) {

    }
}
