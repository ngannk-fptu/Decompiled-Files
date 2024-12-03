/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegationClassLoader
extends ClassLoader {
    private static final Logger log = LoggerFactory.getLogger(DelegationClassLoader.class);
    private ClassLoader delegateClassLoader = DelegationClassLoader.class.getClassLoader();

    public void setDelegateClassLoader(ClassLoader delegateClassLoader) {
        this.delegateClassLoader = Objects.requireNonNull(delegateClassLoader, "Can't set the delegation target to null");
        if (log.isDebugEnabled()) {
            log.debug("Update class loader delegation from [{}] to [{}]", (Object)this.delegateClassLoader, (Object)delegateClassLoader);
        }
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return this.delegateClassLoader.loadClass(name);
    }

    @Override
    public URL getResource(String name) {
        return this.delegateClassLoader.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.delegateClassLoader.getResources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.delegateClassLoader.getResourceAsStream(name);
    }

    @Override
    public synchronized void setDefaultAssertionStatus(boolean enabled) {
        this.delegateClassLoader.setDefaultAssertionStatus(enabled);
    }

    @Override
    public synchronized void setPackageAssertionStatus(String packageName, boolean enabled) {
        this.delegateClassLoader.setPackageAssertionStatus(packageName, enabled);
    }

    @Override
    public synchronized void setClassAssertionStatus(String className, boolean enabled) {
        this.delegateClassLoader.setClassAssertionStatus(className, enabled);
    }

    @Override
    public synchronized void clearAssertionStatus() {
        this.delegateClassLoader.clearAssertionStatus();
    }
}

