/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.dev;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public abstract class ClassLoaderDelegate
extends ClassLoader {
    private ClassLoader delegate;

    public ClassLoaderDelegate(ClassLoader delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.delegate.loadClass(name);
    }

    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public URL getResource(String name) {
        return this.delegate.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.delegate.getResources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.delegate.getResourceAsStream(name);
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        this.delegate.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        this.delegate.setPackageAssertionStatus(packageName, enabled);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        this.delegate.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void clearAssertionStatus() {
        this.delegate.clearAssertionStatus();
    }
}

