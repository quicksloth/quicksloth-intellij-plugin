package Controllers.Utils

import Controllers.Utils.extractors.PsiLanguageExtractor
import Models.RequestCode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementWalkingVisitor

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
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        val languageName = psiFile.language.toString()
        val psiLanguageExtractor = PsiLanguageExtractor.getLanguageExtractor(languageName)

        requestCode.language = psiLanguageExtractor?.languageName
        val extractor = psiLanguageExtractor?.extractor

        val fileType = psiFile.fileType
        println(fileType)

        val comments: MutableList<String> = mutableListOf()
        val libs: MutableSet<String> = mutableSetOf()

        psiFile.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                extractor?.getCodeProperty(element, comments, libs)
                super.visitElement(element)
            }
        })

        requestCode.comments = comments
        requestCode.libs = libs.toList()

        println(requestCode.language)
        println(requestCode.comments)
        println(requestCode.libs)
    }
}