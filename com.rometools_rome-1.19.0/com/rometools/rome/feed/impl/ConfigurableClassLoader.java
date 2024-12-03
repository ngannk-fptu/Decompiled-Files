/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.impl;

public enum ConfigurableClassLoader {
    INSTANCE;

    private ClassLoader classLoader;

    public ClassLoader getClassLoader() {
        if (this.classLoader == null) {
            this.classLoader = ConfigurableClassLoader.class.getClassLoader();
        }
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}

