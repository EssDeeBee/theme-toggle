package com.essdeebee

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.project.DumbAware
import javax.swing.UIManager

class ThemeToggleAction : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val lafManager = LafManager.getInstance()

        val current = lafManager.currentUIThemeLookAndFeel
        val currentIsDark = (current as? UIThemeLookAndFeelInfo)?.isDark ?: false

        val installedThemes = lafManager.installedLookAndFeels
            .mapNotNull { it as? UIThemeLookAndFeelInfo }

        // Pick the opposite darkness
        val target = installedThemes.firstOrNull { it.isDark != currentIsDark } ?: return

        // --- Switch Look & Feel (theme), tolerate API diffs ---
        val lmClass = lafManager.javaClass
        val uiThemeClass = UIThemeLookAndFeelInfo::class.java

        val themeInvoked = runCatching {
            lmClass.getMethod("setCurrentLookAndFeel", uiThemeClass, java.lang.Boolean.TYPE)
                .invoke(lafManager, target, true)
        }.recoverCatching {
            lmClass.getMethod("setCurrentLookAndFeel", uiThemeClass)
                .invoke(lafManager, target)
        }.isSuccess

        runCatching { lmClass.getMethod("updateUI").invoke(lafManager) }

        if (!themeInvoked) {
            (target as? UIManager.LookAndFeelInfo)?.let { plain ->
                runCatching {
                    lmClass.getMethod("setCurrentLookAndFeel", UIManager.LookAndFeelInfo::class.java)
                        .invoke(lafManager, plain)
                    lmClass.getMethod("updateUI").invoke(lafManager)
                }
            }
        }

        // --- Match the editor color scheme darkness to the chosen theme ---
        val colorsManager = EditorColorsManager.getInstance()
        val allSchemes: Array<EditorColorsScheme> =
            runCatching { colorsManager.allSchemes }.getOrElse { colorsManager.getAllSchemes() }

        val currentScheme = colorsManager.globalScheme

        val preferredNames = if (target.isDark)
            listOf("Darcula", "One Dark", "Dark", "High contrast")
        else
            listOf("IntelliJ Light", "Default", "Light")

        val preferredMatch = allSchemes.firstOrNull { s ->
            isDarkSchemeCompat(s) == target.isDark &&
                    preferredNames.any { pn -> s.name.contains(pn, ignoreCase = true) }
        }

        val anyMatchingDarkness = preferredMatch
            ?: allSchemes.firstOrNull { s ->
                isDarkSchemeCompat(s) == target.isDark && s.name != currentScheme.name
            }

        val schemeToApply = anyMatchingDarkness
        if (schemeToApply != null && schemeToApply.name != currentScheme.name) {
            ApplicationManager.getApplication().invokeLater {
                // Use reflection to support SDKs where globalScheme isn't a 'var'
                runCatching {
                    colorsManager.javaClass.getMethod(
                        "setGlobalScheme",
                        EditorColorsScheme::class.java
                    ).invoke(colorsManager, schemeToApply)
                }
            }
        }
    }

    private fun isDarkSchemeCompat(s: EditorColorsScheme): Boolean {
        // Try EditorColorsScheme.isDark() if present; otherwise fall back to name heuristics
        return try {
            val m = s.javaClass.methods.firstOrNull { it.name == "isDark" && it.parameterCount == 0 }
            val viaApi = (m?.invoke(s) as? Boolean)
            viaApi ?: nameLooksDark(s.name)
        } catch (_: Throwable) {
            nameLooksDark(s.name)
        }
    }

    private fun nameLooksDark(name: String): Boolean {
        val n = name.lowercase()
        return n.contains("darcula") || n.contains("dark") || n.contains("high contrast")
    }
}
