package com.essdeebee.theme;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo;

import javax.swing.UIManager;
import java.lang.reflect.Method;

public final class ThemeSwitcher {
    private static final String SET_CURRENT_LOOK_AND_FEEL_NAME = "setCurrentLookAndFeel";

    private final LafManager lafManager;

    public ThemeSwitcher(LafManager lafManager) {
        this.lafManager = lafManager;
    }

    public void apply(UIThemeLookAndFeelInfo targetTheme) {
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
                    SET_CURRENT_LOOK_AND_FEEL_NAME,
                    UIThemeLookAndFeelInfo.class,
                    Boolean.TYPE
            );
            method.invoke(lafManager, targetTheme, update);
            return true;
        } catch (ReflectiveOperationException _) {
            return false;
        }
    }

    private boolean invokeSetCurrentLookAndFeel(
            Class<?> lafManagerClass,
            UIThemeLookAndFeelInfo targetTheme
    ) {
        try {
            Method method = lafManagerClass.getMethod(SET_CURRENT_LOOK_AND_FEEL_NAME, UIThemeLookAndFeelInfo.class);
            method.invoke(lafManager, targetTheme);
            return true;
        } catch (ReflectiveOperationException _) {
            return false;
        }
    }

    private void applyPlainLookAndFeel(UIManager.LookAndFeelInfo targetTheme) {
        try {
            Method method = lafManager.getClass()
                    .getMethod(SET_CURRENT_LOOK_AND_FEEL_NAME, UIManager.LookAndFeelInfo.class);
            method.invoke(lafManager, targetTheme);
        } catch (ReflectiveOperationException _) {
            return;
        }

        updateUi();
    }

    private void updateUi() {
        try {
            Method method = lafManager.getClass().getMethod("updateUI");
            method.invoke(lafManager);
        } catch (ReflectiveOperationException _) {
            // Ignore API differences between IDE versions.
        }
    }
}
