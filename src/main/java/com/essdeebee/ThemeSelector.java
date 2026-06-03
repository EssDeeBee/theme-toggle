package com.essdeebee;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo;

import java.util.Arrays;
import java.util.Optional;

final class ThemeSelector {
    private final LafManager lafManager;

    ThemeSelector(LafManager lafManager) {
        this.lafManager = lafManager;
    }

    Optional<UIThemeLookAndFeelInfo> findOppositeTheme() {
        boolean currentIsDark = Optional.ofNullable(lafManager.getCurrentUIThemeLookAndFeel())
                .map(UIThemeLookAndFeelInfo::isDark)
                .orElse(false);

        return Arrays.stream(lafManager.getInstalledLookAndFeels())
                .filter(UIThemeLookAndFeelInfo.class::isInstance)
                .map(UIThemeLookAndFeelInfo.class::cast)
                .filter(theme -> theme.isDark() != currentIsDark)
                .findFirst();
    }
}
