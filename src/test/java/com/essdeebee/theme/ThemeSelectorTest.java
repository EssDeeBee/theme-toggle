package com.essdeebee.theme;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.ide.ui.LafReference;
import com.intellij.ide.ui.laf.UIThemeExportableBean;
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.ui.CollectionComboBoxModel;
import kotlin.sequences.Sequence;
import org.junit.jupiter.api.Test;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThemeSelectorTest {

    @Test
    void returnsFirstThemeWithOppositeDarkness() {
        TestTheme current = new TestTheme("current", false);
        TestTheme dark = new TestTheme("dark", true);
        TestTheme anotherDark = new TestTheme("another-dark", true);
        ThemeSelector selector = new ThemeSelector(new TestLafManager(current, List.of(current, dark, anotherDark)));

        Optional<UIThemeLookAndFeelInfo> result = selector.findOppositeTheme();

        assertEquals(Optional.of(dark), result);
    }

    @Test
    void returnsEmptyWhenNoOppositeThemeExists() {
        TestTheme current = new TestTheme("current", false);
        TestTheme light = new TestTheme("light", false);
        ThemeSelector selector = new ThemeSelector(new TestLafManager(current, List.of(current, light)));

        assertTrue(selector.findOppositeTheme().isEmpty());
    }

    static final class TestTheme implements UIThemeLookAndFeelInfo {
        private final String name;
        private final boolean dark;

        TestTheme(String name, boolean dark) {
            this.name = name;
            this.dark = dark;
        }

        @Override
        public String getId() {
            return name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getAuthor() {
            return "test";
        }

        @Override
        public boolean isDark() {
            return dark;
        }

        @Override
        public String getEditorSchemeId() {
            return null;
        }

        @Override
        public boolean isInitialized() {
            return true;
        }

        @Override
        public ClassLoader getProviderClassLoader() {
            return getClass().getClassLoader();
        }

        @Override
        public void installTheme(UIDefaults defaults) {
        }

        @Override
        public void installEditorScheme(EditorColorsScheme scheme) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public UIThemeExportableBean describe() {
            return null;
        }
    }

    @SuppressWarnings("removal")
    static class TestLafManager extends LafManager {
        private final UIThemeLookAndFeelInfo currentTheme;
        private final List<UIThemeLookAndFeelInfo> themes;

        TestLafManager(UIThemeLookAndFeelInfo currentTheme, List<UIThemeLookAndFeelInfo> themes) {
            this.currentTheme = currentTheme;
            this.themes = themes;
        }

        @Override
        public UIManager.LookAndFeelInfo[] getInstalledLookAndFeels() {
            return new UIManager.LookAndFeelInfo[0];
        }

        @Override
        public Sequence<UIThemeLookAndFeelInfo> getInstalledThemes() {
            return () -> themes.iterator();
        }

        @Override
        public CollectionComboBoxModel<LafReference> getLafComboBoxModel() {
            return null;
        }

        @Override
        public UIThemeLookAndFeelInfo findLaf(String s) {
            return null;
        }

        @Override
        public UIManager.LookAndFeelInfo getCurrentLookAndFeel() {
            return null;
        }

        @Override
        public UIThemeLookAndFeelInfo getCurrentUIThemeLookAndFeel() {
            return currentTheme;
        }

        @Override
        public LafReference getLookAndFeelReference() {
            return null;
        }

        @Override
        public ListCellRenderer<LafReference> getLookAndFeelCellRenderer(JComponent component) {
            return null;
        }

        @Override
        public JComponent createSettingsToolbar() {
            return null;
        }

        @Override
        public void setCurrentLookAndFeel(UIThemeLookAndFeelInfo lookAndFeelInfo, boolean update) {
        }

        @Override
        public void updateUI() {
        }

        @Override
        public void repaintUI() {
        }

        @Override
        public boolean getAutodetect() {
            return false;
        }

        @Override
        public void setAutodetect(boolean value) {
        }

        @Override
        public boolean getAutodetectSupported() {
            return false;
        }

        @Override
        public void setPreferredDarkLaf(UIThemeLookAndFeelInfo lookAndFeelInfo) {
        }

        @Override
        public void setPreferredLightLaf(UIThemeLookAndFeelInfo lookAndFeelInfo) {
        }

        @Override
        public void resetPreferredEditorColorScheme() {
        }

        @Override
        public void setRememberSchemeForLaf(boolean value) {
        }

        @Override
        public void rememberSchemeForLaf(EditorColorsScheme scheme) {
        }

        @Override
        public void addLafManagerListener(LafManagerListener listener) {
        }

        @Override
        public void removeLafManagerListener(LafManagerListener listener) {
        }

        @Override
        public UIThemeLookAndFeelInfo getDefaultLightLaf() {
            return null;
        }

        @Override
        public UIThemeLookAndFeelInfo getDefaultDarkLaf() {
            return null;
        }
    }
}
