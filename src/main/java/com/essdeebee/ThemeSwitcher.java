package com.essdeebee;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo;

import javax.swing.UIManager;
import java.lang.reflect.Method;

final class ThemeSwitcher {
    private final LafManager lafManager;

    ThemeSwitcher(LafManager lafManager) {
        this.lafManager = lafManager;
    }

    void apply(UIThemeLookAndFeelInfo targetTheme) {
        boolean themeInvoked = invokeSetCurrentLookAndFeel(targetTheme);
        updateUi();

        if (!themeInvoked && targetTheme instanceof UIManager.LookAndFeelInfo plainTheme) {
            applyPlainLookAndFeel(plainTheme);
        }
    }

    private boolean invokeSetCurrentLookAndFeel(UIThemeLookAndFeelInfo targetTheme) {
        Class<?> lafManagerClass = lafManager.getClass();

        if (invokeSetCurrentLookAndFeel(lafManagerClass, targetTheme, true)) {
            return true;
        }

        return invokeSetCurrentLookAndFeel(lafManagerClass, targetTheme);
    }

    private boolean invokeSetCurrentLookAndFeel(
            Class<?> lafManagerClass,
            UIThemeLookAndFeelInfo targetTheme,
            boolean update
    ) {
        try {
            Method method = lafManagerClass.getMethod(
                    "setCurrentLookAndFeel",
                    UIThemeLookAndFeelInfo.class,
                    Boolean.TYPE
            );
            method.invoke(lafManager, targetTheme, update);
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private boolean invokeSetCurrentLookAndFeel(
            Class<?> lafManagerClass,
            UIThemeLookAndFeelInfo targetTheme
    ) {
        try {
            Method method = lafManagerClass.getMethod("setCurrentLookAndFeel", UIThemeLookAndFeelInfo.class);
            method.invoke(lafManager, targetTheme);
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private void applyPlainLookAndFeel(UIManager.LookAndFeelInfo targetTheme) {
        try {
            Method method = lafManager.getClass()
                    .getMethod("setCurrentLookAndFeel", UIManager.LookAndFeelInfo.class);
            method.invoke(lafManager, targetTheme);
        } catch (ReflectiveOperationException ignored) {
            return;
        }

        updateUi();
    }

    private void updateUi() {
        try {
            Method method = lafManager.getClass().getMethod("updateUI");
            method.invoke(lafManager);
        } catch (ReflectiveOperationException ignored) {
            // Ignore API differences between IDE versions.
        }
    }
}
