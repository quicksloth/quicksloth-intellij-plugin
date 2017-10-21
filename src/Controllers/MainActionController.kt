package Controllers;

import View.MainToolWindowFactory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Created by pamelaiupipeixinho on 16/09/17.
 */
class MainActionController : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project?: return
        val queryText = Messages.showInputDialog(project,
                "What do you want to search?",
                "Input Your Query",
                Messages.getQuestionIcon())

        if (queryText != null) {
            showProcessingToolWindow(project, queryText)
        }
    }

    private fun showProcessingToolWindow(project: Project, queryText: String?) {
        /**
         * WORKAROUND (Problem found here!!)
         * The only way was to unregister ToolWindow and register again
         * to have access to factory. After, this access is used to
         * set search query and start the search
         * */
        val factory = MainToolWindowFactory();
        ToolWindowManager.getInstance(project).unregisterToolWindow(MainToolWindowFactory.ID)
        val toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(MainToolWindowFactory.ID,
                true, ToolWindowAnchor.RIGHT, project, true);

        factory.createToolWindowContent(project, toolWindow);
        factory.setQuery(queryText)
        factory.searchPerformed(project)
        factory.showToolWindow()
    }
}