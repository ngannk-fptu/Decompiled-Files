/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.tools;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.discovery.jdk.JDKHooks;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnvironmentCache {
    private static final Map<ClassLoader, Map<String, Object>> root_cache = new HashMap<ClassLoader, Map<String, Object>>();
    public static final int smallHashSize = 13;

    public static synchronized Map<String, Object> get(ClassLoader classLoader) {
        return root_cache.get(classLoader);
    }

    public static synchronized void put(ClassLoader classLoader, Map<String, Object> spis) {
        if (spis != null) {
            root_cache.put(classLoader, spis);
        }
    }

    public static synchronized void release() {
        root_cache.remove(JDKHooks.getJDKHooks().getThreadContextClassLoader());
    }

    public static synchronized void release(ClassLoader classLoader) {
        root_cache.remove(classLoader);
    }
}

