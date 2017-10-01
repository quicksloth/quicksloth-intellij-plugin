package Controllers.Utils

import Models.RequestCode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiComment
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

        val projectName = project.name
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return

        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        val language = psiFile.language
        println(language)
        requestCode.language = language.toString()

        val fileType = psiFile.fileType
        println(fileType)

        val comments: MutableList<String> = mutableListOf()
        val libs: MutableList<String> = mutableListOf()

        psiFile.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {

                println(element.node?.elementType.toString() + " - " + element.text)

                if (element is PsiComment || element.node?.elementType.toString().contains("DOCSTRING")) {
                    println("*** COMMENT" + element.text)
                    comments.add(element.text)
                }

//                if (element.node?.elementType.toString().contains( "IMPORT_ELEMENT")) {
//                    println("*** IMPORT" + element.text)
//                    libs.add(element.text)
////                    requestCode.libs.add(element?.text)
//                }
//
//
                if (element.node?.elementType.toString().contains("IMPORT_STATEMENT")) {
                    element.children.forEach { importElement ->
                        if (importElement.node?.elementType.toString().contains("IMPORT_ELEMENT")) {
                            println("*** IMPORT" + element.text)
                            libs.add(element.text)
                        }
                    }
                }

                println(comments)
                println(libs)

//              NOT WORKING
//                if (element is PsiTypeElement) {
//                    println("*** PyImportStatement" + element.text)
//                }
//
//                if (element?.parent is PsiDocComment) {
//                    println("*** COMMENT" + element?.text)
//                    requestCode.comments.add(element?.text)
//                }

//                if (element is PsiImportStatement) {
//                    println("*** IMPORT" + element.text)
//                }

//                if (element?.parent is PsiImportStatement) {
//                    println("*** IMPORT" + element?.text)
//                }


//                IMPORT_ELEMENT

//                println("going")
//                println(element?.originalElement)
//                println(element?.node?.elementType)
//                println(element?.javaClass?.name)
//                println(element?.javaClass?.kotlin?.qualifiedName)

                super.visitElement(element)
                println("---------")
            }
        })

        requestCode.comments = comments
        requestCode.libs = libs
    }
}