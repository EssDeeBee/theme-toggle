package com.essdeebee.editor;

import com.intellij.openapi.editor.colors.EditorColorsScheme;

import java.lang.reflect.Proxy;

final class SchemeTestFixtures {
    private SchemeTestFixtures() {
    }

    static EditorColorsScheme scheme(String name) {
        return proxy(name, null);
    }

    static EditorColorsScheme scheme(String name, boolean dark) {
        return proxy(name, dark);
    }

    private static EditorColorsScheme proxy(String name, Boolean dark) {
        return (EditorColorsScheme) Proxy.newProxyInstance(
                SchemeTestFixtures.class.getClassLoader(),
                new Class<?>[]{EditorColorsScheme.class, DarkAwareScheme.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getName" -> name;
                    case "isDark" -> dark;
                    case "toString" -> name;
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> defaultValue(method.getReturnType());
                }
        );
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }

        if (returnType == boolean.class) {
            return false;
        }

        if (returnType == int.class) {
            return 0;
        }

        if (returnType == float.class) {
            return 0.0f;
        }

        if (returnType == long.class) {
            return 0L;
        }

        return null;
    }

    interface DarkAwareScheme {
        boolean isDark();
    }
}
