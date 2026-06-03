package com.essdeebee.theme;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo;

import java.util.Iterator;
import java.util.Optional;

public final class ThemeSelector {
    private final LafManager lafManager;

    public ThemeSelector(LafManager lafManager) {
        this.lafManager = lafManager;
    }

    public Optional<UIThemeLookAndFeelInfo> findOppositeTheme() {
        boolean currentIsDark = Optional.ofNullable(lafManager.getCurrentUIThemeLookAndFeel())
                .map(UIThemeLookAndFeelInfo::isDark)
                .orElse(false);

        Iterator<UIThemeLookAndFeelInfo> themes = lafManager.getInstalledThemes().iterator();
        while (themes.hasNext()) {
            UIThemeLookAndFeelInfo theme = themes.next();
            if (theme.isDark() != currentIsDark) {
                return Optional.of(theme);
            }
        }

        return Optional.empty();
    }
}
