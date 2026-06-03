package com.essdeebee

import com.intellij.ide.ui.LafManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.DumbAware

class ThemeToggleAction : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val lafManager = LafManager.getInstance()
        val targetTheme = ThemeSelector(lafManager).findOppositeTheme() ?: return

        ThemeSwitcher(lafManager).apply(targetTheme)
        EditorSchemeSynchronizer(EditorColorsManager.getInstance()).apply(targetTheme.isDark)
    }
}
