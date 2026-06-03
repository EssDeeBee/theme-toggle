package com.essdeebee

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo

internal class ThemeSelector(
    private val lafManager: LafManager,
) {
    fun findOppositeTheme(): UIThemeLookAndFeelInfo? {
        val currentIsDark = lafManager.currentUIThemeLookAndFeel?.isDark ?: false

        return lafManager.installedLookAndFeels
            .filterIsInstance<UIThemeLookAndFeelInfo>()
            .firstOrNull { it.isDark != currentIsDark }
    }
}
