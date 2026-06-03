package com.essdeebee;

import com.intellij.openapi.editor.colors.EditorColorsScheme;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

final class SchemeDarknessDetector {
    private SchemeDarknessDetector() {
    }

    static boolean isDark(EditorColorsScheme scheme) {
        return readIsDarkFromApi(scheme).orElseGet(() -> nameLooksDark(scheme.getName()));
    }

    private static Optional<Boolean> readIsDarkFromApi(EditorColorsScheme scheme) {
        try {
            Method method = scheme.getClass().getMethod("isDark");
            Object result = method.invoke(scheme);
            return result instanceof Boolean value ? Optional.of(value) : Optional.empty();
        } catch (ReflectiveOperationException ignored) {
            return Optional.empty();
        }
    }

    private static boolean nameLooksDark(String name) {
        String normalizedName = name.toLowerCase(Locale.ROOT);
        return normalizedName.contains("darcula")
                || normalizedName.contains("dark")
                || normalizedName.contains("high contrast");
    }
}
