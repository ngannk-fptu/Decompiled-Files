/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.lucene.analysis.util.ResourceLoader;

public final class ClasspathResourceLoader
implements ResourceLoader {
    private final Class<?> clazz;
    private final ClassLoader loader;

    public ClasspathResourceLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClasspathResourceLoader(ClassLoader loader) {
        this(null, loader);
    }

    public ClasspathResourceLoader(Class<?> clazz) {
        this(clazz, clazz.getClassLoader());
    }

    private ClasspathResourceLoader(Class<?> clazz, ClassLoader loader) {
        this.clazz = clazz;
        this.loader = loader;
    }

    @Override
    public InputStream openResource(String resource) throws IOException {
        InputStream stream;
        InputStream inputStream = stream = this.clazz != null ? this.clazz.getResourceAsStream(resource) : this.loader.getResourceAsStream(resource);
        if (stream == null) {
            throw new IOException("Resource not found: " + resource);
        }
        return stream;
    }

    @Override
    public <T> Class<? extends T> findClass(String cname, Class<T> expectedType) {
        try {
            return Class.forName(cname, true, this.loader).asSubclass(expectedType);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot load class: " + cname, e);
        }
    }

    @Override
    public <T> T newInstance(String cname, Class<T> expectedType) {
        Class<T> clazz = this.findClass(cname, expectedType);
        try {
            return clazz.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot create instance: " + cname, e);
        }
    }
}

