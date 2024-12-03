/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tomcat.InstanceManager;

public final class InstanceManagerBindings {
    private static final Map<ClassLoader, InstanceManager> bindings = new ConcurrentHashMap<ClassLoader, InstanceManager>();

    public static void bind(ClassLoader classLoader, InstanceManager instanceManager) {
        bindings.put(classLoader, instanceManager);
    }

    public static void unbind(ClassLoader classLoader) {
        bindings.remove(classLoader);
    }

    public static InstanceManager get(ClassLoader classLoader) {
        return bindings.get(classLoader);
    }
}

