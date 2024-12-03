/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

abstract class AbstractClassLoader
extends ClassLoader {
    protected AbstractClassLoader(ClassLoader parent) {
        super(parent);
    }

    protected AbstractClassLoader() {
    }

    @Override
    protected abstract URL findResource(String var1);

    protected abstract Class findClass(String var1) throws ClassNotFoundException;

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        URL url = this.findResource(name);
        return url != null ? Collections.enumeration(Collections.singleton(url)) : null;
    }
}

