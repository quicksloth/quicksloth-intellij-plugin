package Controllers.Utils

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiDocumentManager

/**
 * Created by pamelaiupipeixinho on 16/09/17.
 */
class CodeExtractorCode: AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        print("TESTE")
        print("TESTE2")
        val project = event.project ?: return

        val projectName = project.name
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return

        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return

        val language = psiFile.language
        print(language)

        val fileType = psiFile.fileType
        print(fileType)

//        psiFile.accept(PsiRecursiveElementWalkingVisitor)
//        psiFile.accept(new PsiRecursiveElementWalkingVisitor(){
//            override fun visitElement(element: PsiElement?) {
//
//                //				----- JAVA ----
//                if (element is PsiMethod) {
//                    println("psi method = " + element.nameIdentifier!!.toString())
//                } else if (element is PsiImportList) {
//                    for (importStatement in element.importStatements) {
//                        println("psi PsiImportList = " + importStatement.qualifiedName!!)
//                    }
//                } else if (element is PsiDocComment) {
//                    println("psi psiDocComment Owner = " + element.owner!!.name!!)
//                    println("psi psiDocComment = " + element.text)
//                } else if (element is PsiTypeElement) {
//                    println("psi psiTypeElement  = " + element.type.toString())
//                } else {
//                    //					System.out.println("psi element = " + element.toString());
//                }
//
//                //				----- Kotlin ----
//
//                //				if (element instanceof )
//
//
//                super.visitElement(element)
//            }
    }
}