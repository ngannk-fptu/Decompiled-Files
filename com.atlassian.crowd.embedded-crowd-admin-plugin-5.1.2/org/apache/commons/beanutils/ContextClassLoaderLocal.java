/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.util.Map;
import java.util.WeakHashMap;

public class ContextClassLoaderLocal<T> {
    private final Map<ClassLoader, T> valueByClassLoader = new WeakHashMap<ClassLoader, T>();
    private boolean globalValueInitialized = false;
    private T globalValue;

    protected T initialValue() {
        return null;
    }

    public synchronized T get() {
        this.valueByClassLoader.isEmpty();
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                T value = this.valueByClassLoader.get(contextClassLoader);
                if (value == null && !this.valueByClassLoader.containsKey(contextClassLoader)) {
                    value = this.initialValue();
                    this.valueByClassLoader.put(contextClassLoader, value);
                }
                return value;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (!this.globalValueInitialized) {
            this.globalValue = this.initialValue();
            this.globalValueInitialized = true;
        }
        return this.globalValue;
    }

    public synchronized void set(T value) {
        this.valueByClassLoader.isEmpty();
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                this.valueByClassLoader.put(contextClassLoader, value);
                return;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        this.globalValue = value;
        this.globalValueInitialized = true;
    }

    public synchronized void unset() {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            this.unset(contextClassLoader);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
    }

    public synchronized void unset(ClassLoader classLoader) {
        this.valueByClassLoader.remove(classLoader);
    }
}

