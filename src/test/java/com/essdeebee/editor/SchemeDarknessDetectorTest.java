package com.essdeebee.editor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemeDarknessDetectorTest {

    @Test
    void prefersIsDarkMethodWhenAvailable() {
        assertTrue(SchemeDarknessDetector.isDark(SchemeTestFixtures.scheme("Light Name", true)));
        assertFalse(SchemeDarknessDetector.isDark(SchemeTestFixtures.scheme("Very Dark Name", false)));
    }

    @Test
    void fallsBackToNameHeuristics() {
        assertTrue(SchemeDarknessDetector.isDark(SchemeTestFixtures.scheme("Darcula")));
        assertTrue(SchemeDarknessDetector.isDark(SchemeTestFixtures.scheme("High Contrast")));
        assertFalse(SchemeDarknessDetector.isDark(SchemeTestFixtures.scheme("IntelliJ Light")));
    }
}
