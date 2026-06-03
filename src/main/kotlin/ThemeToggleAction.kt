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
        val targetTheme = findOppositeTheme(lafManager) ?: return

        applyTheme(lafManager, targetTheme)
        applyMatchingEditorScheme(targetTheme.isDark)
    }

    private fun findOppositeTheme(lafManager: LafManager): UIThemeLookAndFeelInfo? {
        val currentIsDark = lafManager.currentUIThemeLookAndFeel?.isDark ?: false

        return lafManager.installedLookAndFeels
            .filterIsInstance<UIThemeLookAndFeelInfo>()
            .firstOrNull { it.isDark != currentIsDark }
    }

    private fun applyTheme(lafManager: LafManager, targetTheme: UIThemeLookAndFeelInfo) {
        val lmClass = lafManager.javaClass
        val uiThemeClass = UIThemeLookAndFeelInfo::class.java

        val themeInvoked = runCatching {
            lmClass.getMethod("setCurrentLookAndFeel", uiThemeClass, java.lang.Boolean.TYPE)
                .invoke(lafManager, targetTheme, true)
        }.recoverCatching {
            lmClass.getMethod("setCurrentLookAndFeel", uiThemeClass)
                .invoke(lafManager, targetTheme)
        }.isSuccess

        updateUi(lafManager)

        if (!themeInvoked) {
            (targetTheme as? UIManager.LookAndFeelInfo)?.let { plainTheme ->
                applyPlainLookAndFeel(lafManager, plainTheme)
            }
        }
    }

    private fun applyPlainLookAndFeel(
        lafManager: LafManager,
        targetTheme: UIManager.LookAndFeelInfo,
    ) {
        runCatching {
            lafManager.javaClass
                .getMethod("setCurrentLookAndFeel", UIManager.LookAndFeelInfo::class.java)
                .invoke(lafManager, targetTheme)
        }

        updateUi(lafManager)
    }

    private fun updateUi(lafManager: LafManager) {
        runCatching { lafManager.javaClass.getMethod("updateUI").invoke(lafManager) }
    }

    private fun applyMatchingEditorScheme(targetIsDark: Boolean) {
        val colorsManager = EditorColorsManager.getInstance()
        val scheme = findMatchingEditorScheme(colorsManager, targetIsDark) ?: return

        if (scheme.name == colorsManager.globalScheme.name) return

        ApplicationManager.getApplication().invokeLater {
            runCatching {
                colorsManager.javaClass
                    .getMethod("setGlobalScheme", EditorColorsScheme::class.java)
                    .invoke(colorsManager, scheme)
            }
        }
    }

    private fun findMatchingEditorScheme(
        colorsManager: EditorColorsManager,
        targetIsDark: Boolean,
    ): EditorColorsScheme? {
        val allSchemes = getAllSchemes(colorsManager)
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

    private fun getAllSchemes(colorsManager: EditorColorsManager): Array<EditorColorsScheme> =
        runCatching { colorsManager.allSchemes }.getOrElse { colorsManager.getAllSchemes() }

    private fun preferredNames(isDark: Boolean): List<String> =
        if (isDark) {
            listOf("Darcula", "One Dark", "Dark", "High contrast")
        } else {
            listOf("IntelliJ Light", "Default", "Light")
        }

    private fun EditorColorsScheme.matchesDarkness(isDark: Boolean): Boolean =
        isDarkSchemeCompat() == isDark

    private fun EditorColorsScheme.isDarkSchemeCompat(): Boolean {
        return try {
            val isDarkMethod = javaClass.methods.firstOrNull { it.name == "isDark" && it.parameterCount == 0 }
            val isDark = isDarkMethod?.invoke(this) as? Boolean
            isDark ?: nameLooksDark(name)
        } catch (_: Throwable) {
            nameLooksDark(name)
        }
    }

    private fun nameLooksDark(name: String): Boolean {
        val n = name.lowercase()
        return n.contains("darcula") || n.contains("dark") || n.contains("high contrast")
    }
}
