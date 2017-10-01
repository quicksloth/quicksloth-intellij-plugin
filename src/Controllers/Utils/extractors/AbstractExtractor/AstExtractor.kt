package Controllers.Utils.extractors.AbstractExtractor

import com.intellij.psi.PsiElement

/**
 * Created by pamelaiupipeixinho on 01/10/17.
 */
abstract class AstExtractor {
    abstract fun getCodeProperty(element: PsiElement, comments: MutableList<String>, libs: MutableSet<String>)
}