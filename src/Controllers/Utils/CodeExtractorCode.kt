package Controllers.Utils

import Models.RequestCode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.*

/**
 * Created by pamelaiupipeixinho on 16/09/17.
 */
class CodeExtractorCode: AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val requestCode = RequestCode()
        extractAST(event, requestCode)

    }

    private fun extractAST(event: AnActionEvent, requestCode: RequestCode) {
        val project = event.project ?: return

        val projectName = project.name
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return

        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        val language = psiFile.language
        println(language)

        requestCode.comments = listOf("os", "test")

        val fileType = psiFile.fileType
        println(fileType)

        psiFile.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement?) {
                println(element)
                    if (element is PsiComment) {
                        println(element.text)
//                        requestCode.comments.add()
                    } else if (element is PsiImportList) {
                        println(element.allImportStatements)
                        element.allImportStatements.forEach { importElement ->
                            println(importElement)
                        }
                    }
                super.visitElement(element)
            }
        })
    }
}