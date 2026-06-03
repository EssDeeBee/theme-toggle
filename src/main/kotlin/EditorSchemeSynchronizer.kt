package com.essdeebee

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme

internal class EditorSchemeSynchronizer(
    private val colorsManager: EditorColorsManager,
) {
    fun apply(targetIsDark: Boolean) {
        val scheme = findMatchingScheme(targetIsDark) ?: return

        if (scheme.name == colorsManager.globalScheme.name) return

        ApplicationManager.getApplication().invokeLater {
            runCatching {
                colorsManager.javaClass
                    .getMethod("setGlobalScheme", EditorColorsScheme::class.java)
                    .invoke(colorsManager, scheme)
            }
        }
    }

    private fun findMatchingScheme(targetIsDark: Boolean): EditorColorsScheme? {
        val allSchemes = getAllSchemes()
        val currentScheme = colorsManager.globalScheme
        val preferredNames = preferredNames(targetIsDark)

        return allSchemes.firstOrNull { scheme ->
            scheme.matchesDarkness(targetIsDark) && preferredNames.any { preferredName ->
                scheme.name.contains(preferredName, ignoreCase = true)
            }
        } ?: allSchemes.firstOrNull { scheme ->
            scheme.matchesDarkness(targetIsDark) && scheme.name != currentScheme.name
        }
    }

    private fun getAllSchemes(): Array<EditorColorsScheme> =
        runCatching { colorsManager.allSchemes }.getOrElse { colorsManager.getAllSchemes() }

    private fun preferredNames(isDark: Boolean): List<String> =
        if (isDark) {
            listOf("Darcula", "One Dark", "Dark", "High contrast")
        } else {
            listOf("IntelliJ Light", "Default", "Light")
        }

    private fun EditorColorsScheme.matchesDarkness(isDark: Boolean): Boolean =
        SchemeDarknessDetector.isDark(this) == isDark
}
