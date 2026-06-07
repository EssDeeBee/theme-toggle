package com.essdeebee.action;

import com.essdeebee.editor.EditorSchemeSynchronizer;
import com.essdeebee.theme.ThemeSelector;
import com.essdeebee.theme.ThemeSwitcher;
import com.intellij.ide.ui.LafManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ThemeToggleAction extends AnAction implements DumbAware {
    private final Consumer<AnActionEvent> actionHandler;

    public ThemeToggleAction() {
        this(ThemeToggleAction::toggleTheme);
    }

    ThemeToggleAction(Consumer<AnActionEvent> actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        handleAction(event);
    }

    void handleAction(AnActionEvent event) {
        actionHandler.accept(event);
    }

    private static void toggleTheme(AnActionEvent event) {
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
