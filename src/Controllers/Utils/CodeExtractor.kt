@file:JvmName("CodeExtractor")

package Controllers.Utils

import Controllers.Utils.extractors.PsiLanguageExtractor
import Models.RequestCode
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementWalkingVisitor

/**
 * Created by pamelaiupipeixinho on 16/09/17.
 */
class CodeExtractor {
    fun extractAST(project: Project, requestCode: RequestCode): RequestCode? {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return null
        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return null

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

        return requestCode
    }
}