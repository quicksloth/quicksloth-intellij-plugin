package Controllers;

import View.MainToolWindowFactory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Created by pamelaiupipeixinho on 16/09/17.
 */
//ToolWindowManager.getInstance(project).getToolWindow("My Log");
class MainactionController : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        println("Here, I'm")
        val project = event.project?: return
        val queryText = Messages.showInputDialog(project,
                "What is your Doubt?",
                "Input Your Query",
                Messages.getQuestionIcon())

//        println(ToolWindowManager.getInstance(project).getActiveToolWindowId())
        val factory =  MainToolWindowFactory();
        ToolWindowManager.getInstance(project).unregisterToolWindow(MainToolWindowFactory.ID)
        val toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(MainToolWindowFactory.ID,
                true, ToolWindowAnchor.RIGHT, project, true);

        factory.createToolWindowContent(project, toolWindow);
        factory.setQuery(queryText)
        factory.searchPerformed(project)
        factory.showToolWindow()

//        toolWindow.
//        toolWindow.contentManager.factory.
//        val mainToolWindow = MainToolWindowFactory()
//        ContentFactory.SERVICE.getInstance().createContent(mainToolWindow, "a", false)
//        toolWindow.setQuery(queryText)
//        toolWindow.searchPerformed(project)

//        val con = MainToolWindowFactory()
//        con.createToolWindowContent(project, toolWindow)

//        val factory = toolWindow.contentManager.factory.createContent(con, null, false)

//        val factoryMainToolwindow = factory as MainToolWindowFactory
//        factoryMainToolwindow.setQuery(queryText)
//        factoryMainToolwindow.searchPerformed(project)


    }
}