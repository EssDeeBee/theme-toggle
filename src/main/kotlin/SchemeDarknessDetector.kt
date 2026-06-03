package com.essdeebee

import com.intellij.openapi.editor.colors.EditorColorsScheme

internal object SchemeDarknessDetector {
    fun isDark(scheme: EditorColorsScheme): Boolean {
        return readIsDarkFromApi(scheme) ?: nameLooksDark(scheme.name)
    }

    private fun readIsDarkFromApi(scheme: EditorColorsScheme): Boolean? {
        return runCatching {
            val isDarkMethod = scheme.javaClass.methods.firstOrNull {
                it.name == "isDark" && it.parameterCount == 0
            }

            isDarkMethod?.invoke(scheme) as? Boolean
        }.getOrNull()
    }

    private fun nameLooksDark(name: String): Boolean {
        val n = name.lowercase()
        return n.contains("darcula") || n.contains("dark") || n.contains("high contrast")
    }
}
