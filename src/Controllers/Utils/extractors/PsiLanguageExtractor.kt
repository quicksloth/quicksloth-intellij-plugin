package Controllers.Utils.extractors

import Controllers.Utils.extractors.AbstractExtractor.AstExtractor
import Controllers.Utils.extractors.SpecificExtractors.PythonAstExtractor

/**
 * Created by pamelaiupipeixinho on 01/10/17.
 */

enum class PsiLanguageExtractor {
    PYTHON {
        override val languageName = "Python"
        override val extractor = PythonAstExtractor()
    },
    ;

    abstract val languageName: String
    abstract val extractor: AstExtractor

    companion object {
        fun getLanguageExtractor(languageName: String): PsiLanguageExtractor? {
            PsiLanguageExtractor
                    .values()
                    .forEach { languageExtractor ->
                        if (languageName.contains(languageExtractor.languageName)) {
                            return languageExtractor
                        }
                    }
            return null
        }
    }
}