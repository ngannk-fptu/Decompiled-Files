/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.component;

import java.util.Collection;
import java.util.Optional;

public abstract class ComponentLocator {
    private static ComponentLocator componentLocator;

    public static void setComponentLocator(ComponentLocator loc) {
        componentLocator = loc;
    }

    public static boolean isInitialized() {
        return componentLocator != null;
    }

    public static <T> T getComponent(Class<T> iface) {
        return componentLocator.getComponentInternal(iface);
    }

    public static <T> Optional<T> getComponentSafely(Class<T> iface) {
        if (componentLocator == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(componentLocator.getComponentInternal(iface));
        }
        catch (RuntimeException ignore) {
            return Optional.empty();
        }
    }

    public static <T> T getComponent(Class<T> iface, String componentKey) {
        return componentLocator.getComponentInternal(iface, componentKey);
    }

    protected abstract <T> T getComponentInternal(Class<T> var1);

    protected abstract <T> T getComponentInternal(Class<T> var1, String var2);

    public static <T> Collection<T> getComponents(Class<T> iface) {
        return componentLocator.getComponentsInternal(iface);
    }

    protected abstract <T> Collection<T> getComponentsInternal(Class<T> var1);

    protected String convertClassToName(Class iface) {
        return Character.toLowerCase(iface.getSimpleName().charAt(0)) + iface.getSimpleName().substring(1);
    }
}

