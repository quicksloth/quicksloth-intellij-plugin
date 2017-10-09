package Controllers.Utils

import Models.RequestCode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * Created by pamelaiupipeixinho on 16/09/17.
 */
class CodeExtractorAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val requestCode = RequestCode()
        val project = event.project ?: return
        CodeExtractor().extractAST(project, requestCode)
    }
}