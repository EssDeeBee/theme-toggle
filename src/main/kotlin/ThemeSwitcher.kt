package com.essdeebee

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo
import javax.swing.UIManager

internal class ThemeSwitcher(
    private val lafManager: LafManager,
) {
    fun apply(targetTheme: UIThemeLookAndFeelInfo) {
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
}
