package com.essdeebee.theme;

import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ThemeSwitcherTest {

    @Test
    void appliesThemeThroughLafManagerAndUpdatesUi() {
        ThemeSelectorTest.TestTheme target = new ThemeSelectorTest.TestTheme("target", true);
        TestSwitchingLafManager lafManager = new TestSwitchingLafManager(target, List.of(target));

        new ThemeSwitcher(lafManager).apply(target);

        assertSame(target, lafManager.appliedTheme);
        assertEquals(1, lafManager.updateUiCalls);
    }

    static final class TestSwitchingLafManager extends ThemeSelectorTest.TestLafManager {
        private UIThemeLookAndFeelInfo appliedTheme;
        private int updateUiCalls;

        TestSwitchingLafManager(UIThemeLookAndFeelInfo currentTheme, List<UIThemeLookAndFeelInfo> themes) {
            super(currentTheme, themes);
        }

        @Override
        public void setCurrentLookAndFeel(UIThemeLookAndFeelInfo lookAndFeelInfo, boolean update) {
            appliedTheme = lookAndFeelInfo;
        }

        @Override
        public void updateUI() {
            updateUiCalls++;
        }
    }
}
