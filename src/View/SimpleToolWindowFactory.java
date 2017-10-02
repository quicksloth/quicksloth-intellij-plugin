package View;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
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
public class SimpleToolWindowFactory implements ToolWindowFactory {

	private JPanel myToolWindowContent;
	private JTextField queryField;
	private JButton searchButton;
	private JTree code;
	private JScrollBar scrollBar1;
	private ToolWindow myToolWindow;

	public SimpleToolWindowFactory() {
		System.out.println("SimpleToolWindowFactory");
		searchButton.addActionListener(e -> System.out.println("actionPerformed"));
		queryField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				if (e.getKeyChar() == '\n') {
					System.out.println("Enter Pressed");
					System.out.println("queryField " + queryField.getText());
				}
			}
		});
	}

	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		System.out.println("createToolWindowContent");
		System.out.println(toolWindow.isActive());

		this.myToolWindow = toolWindow;
		ContentFactory contentFactory = SERVICE.getInstance();
		Content content = contentFactory.createContent(myToolWindowContent, "", false);
		toolWindow.getContentManager().addContent(content);
	}
}
