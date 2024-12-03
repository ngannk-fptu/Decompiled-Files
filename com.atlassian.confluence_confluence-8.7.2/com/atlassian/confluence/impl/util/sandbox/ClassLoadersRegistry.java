/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.sandbox;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

class ClassLoadersRegistry {
    private final Map<ClassLoader, UUID> idByClassLoader = new WeakHashMap<ClassLoader, UUID>();

    ClassLoadersRegistry() {
    }

    synchronized UUID classLoaderUid(ClassLoader classLoader) {
        return this.idByClassLoader.computeIfAbsent(classLoader, c -> UUID.randomUUID());
    }
}

