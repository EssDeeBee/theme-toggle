package com.essdeebee;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class EditorSchemeSynchronizer {
    private final EditorColorsManager colorsManager;

    EditorSchemeSynchronizer(EditorColorsManager colorsManager) {
        this.colorsManager = colorsManager;
    }

    void apply(boolean targetIsDark) {
        Optional<EditorColorsScheme> scheme = findMatchingScheme(targetIsDark);

        if (scheme.isEmpty() || scheme.get().getName().equals(colorsManager.getGlobalScheme().getName())) {
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> setGlobalScheme(scheme.get()));
    }

    private Optional<EditorColorsScheme> findMatchingScheme(boolean targetIsDark) {
        EditorColorsScheme[] allSchemes = getAllSchemes();
        EditorColorsScheme currentScheme = colorsManager.getGlobalScheme();
        List<String> preferredNames = preferredNames(targetIsDark);

        Optional<EditorColorsScheme> preferredMatch = Arrays.stream(allSchemes)
                .filter(scheme -> matchesDarkness(scheme, targetIsDark))
                .filter(scheme -> preferredNames.stream()
                        .anyMatch(name -> containsIgnoreCase(scheme.getName(), name)))
                .findFirst();

        if (preferredMatch.isPresent()) {
            return preferredMatch;
        }

        return Arrays.stream(allSchemes)
                .filter(scheme -> matchesDarkness(scheme, targetIsDark))
                .filter(scheme -> !scheme.getName().equals(currentScheme.getName()))
                .findFirst();
    }

    private EditorColorsScheme[] getAllSchemes() {
        return colorsManager.getAllSchemes();
    }

    private List<String> preferredNames(boolean isDark) {
        if (isDark) {
            return List.of("Darcula", "One Dark", "Dark", "High contrast");
        }

        return List.of("IntelliJ Light", "Default", "Light");
    }

    private boolean matchesDarkness(EditorColorsScheme scheme, boolean isDark) {
        return SchemeDarknessDetector.isDark(scheme) == isDark;
    }

    private boolean containsIgnoreCase(String value, String searchTerm) {
        return value.toLowerCase(Locale.ROOT).contains(searchTerm.toLowerCase(Locale.ROOT));
    }

    private void setGlobalScheme(EditorColorsScheme scheme) {
        try {
            Method method = colorsManager.getClass().getMethod("setGlobalScheme", EditorColorsScheme.class);
            method.invoke(colorsManager, scheme);
        } catch (ReflectiveOperationException ignored) {
            // Ignore API differences between IDE versions.
        }
    }
}
