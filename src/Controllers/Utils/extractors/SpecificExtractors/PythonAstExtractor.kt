package Controllers.Utils.extractors.SpecificExtractors

import Controllers.Utils.extractors.AbstractExtractor.AstExtractor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement

/**
 * Created by pamelaiupipeixinho on 01/10/17.
 */
class PythonAstExtractor: AstExtractor() {
    val invalidChars = setOf('#', '"')

    override fun getCodeProperty(element: PsiElement, comments: MutableList<String>, libs: MutableSet<String>) {
        val elementType = element.node?.elementType.toString()

        if (element is PsiComment || elementType.contains("DOCSTRING")) {
            comments.add(element.text.filter { !invalidChars.contains(it) }.trimMargin())
        }

        if (elementType.contains("IMPORT_STATEMENT")) {
            element.children.forEach { importElement ->
                if (importElement.node?.elementType.toString().contains("IMPORT_ELEMENT")) {
                    libs.add(importElement.text)
                }
            }
        }
    }

}