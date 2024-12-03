/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.finder;

import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class ClassLoaderInterfaceDelegate
implements ClassLoaderInterface {
    private ClassLoader classLoader;

    public ClassLoaderInterfaceDelegate(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.classLoader.loadClass(name);
    }

    @Override
    public URL getResource(String className) {
        return this.classLoader.getResource(className);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.classLoader.getResources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.classLoader.getResourceAsStream(name);
    }

    @Override
    public ClassLoaderInterface getParent() {
        return this.classLoader.getParent() != null ? new ClassLoaderInterfaceDelegate(this.classLoader.getParent()) : null;
    }
}

