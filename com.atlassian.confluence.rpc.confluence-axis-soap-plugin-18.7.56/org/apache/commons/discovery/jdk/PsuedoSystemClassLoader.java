/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.jdk;

import java.io.InputStream;
import java.net.URL;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class PsuedoSystemClassLoader
extends ClassLoader {
    PsuedoSystemClassLoader() {
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        return this.findSystemClass(className);
    }

    @Override
    public URL getResource(String resName) {
        return PsuedoSystemClassLoader.getSystemResource(resName);
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        return PsuedoSystemClassLoader.getSystemResourceAsStream(resName);
    }
}

