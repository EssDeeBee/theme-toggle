package com.essdeebee.editor;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class EditorSchemeSynchronizerTest {

    @Test
    void appliesPreferredSchemeWithMatchingDarkness() {
        EditorColorsScheme current = SchemeTestFixtures.scheme("IntelliJ Light", false);
        EditorColorsScheme fallbackDark = SchemeTestFixtures.scheme("Custom Dark", true);
        EditorColorsScheme preferredDark = SchemeTestFixtures.scheme("Darcula", true);
        TestEditorColorsManager manager = new TestEditorColorsManager(
                current,
                new EditorColorsScheme[]{current, fallbackDark, preferredDark}
        );

        new EditorSchemeSynchronizer(manager, Runnable::run).apply(true);

        assertSame(preferredDark, manager.appliedScheme);
    }

    @Test
    void doesNothingWhenMatchingSchemeIsAlreadyCurrent() {
        EditorColorsScheme current = SchemeTestFixtures.scheme("Darcula", true);
        TestEditorColorsManager manager = new TestEditorColorsManager(
                current,
                new EditorColorsScheme[]{current, SchemeTestFixtures.scheme("Another Dark", true)}
        );

        new EditorSchemeSynchronizer(manager, Runnable::run).apply(true);

        assertNull(manager.appliedScheme);
    }

    static final class TestEditorColorsManager extends EditorColorsManager {
        private final EditorColorsScheme globalScheme;
        private final EditorColorsScheme[] allSchemes;
        private EditorColorsScheme appliedScheme;

        TestEditorColorsManager(EditorColorsScheme globalScheme, EditorColorsScheme[] allSchemes) {
            this.globalScheme = globalScheme;
            this.allSchemes = allSchemes;
        }

        @Override
        public void addColorScheme(EditorColorsScheme scheme) {
        }

        @Override
        public EditorColorsScheme[] getAllSchemes() {
            return allSchemes;
        }

        @Override
        public void setGlobalScheme(EditorColorsScheme scheme) {
            appliedScheme = scheme;
        }

        @Override
        public void setCurrentSchemeOnLafChange(EditorColorsScheme scheme) {
        }

        @Override
        public EditorColorsScheme getGlobalScheme() {
            return globalScheme;
        }

        @Override
        public EditorColorsScheme getActiveVisibleScheme() {
            return globalScheme;
        }

        @Override
        public EditorColorsScheme getScheme(String name) {
            return null;
        }

        @Override
        public boolean isDefaultScheme(EditorColorsScheme scheme) {
            return false;
        }

        @Override
        public boolean isUseOnlyMonospacedFonts() {
            return false;
        }

        @Override
        public void setUseOnlyMonospacedFonts(boolean value) {
        }

        @Override
        public long getSchemeModificationCounter() {
            return 0L;
        }
    }
}
