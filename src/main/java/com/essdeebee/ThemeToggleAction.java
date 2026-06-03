package com.essdeebee;

import com.intellij.ide.ui.LafManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class ThemeToggleAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LafManager lafManager = LafManager.getInstance();
        ThemeSelector themeSelector = new ThemeSelector(lafManager);
        ThemeSwitcher themeSwitcher = new ThemeSwitcher(lafManager);
        EditorSchemeSynchronizer schemeSynchronizer =
                new EditorSchemeSynchronizer(EditorColorsManager.getInstance());

        themeSelector.findOppositeTheme().ifPresent(targetTheme -> {
            themeSwitcher.apply(targetTheme);
            schemeSynchronizer.apply(targetTheme.isDark());
        });
    }
}
