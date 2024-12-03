/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class EhcacheDefaultClassLoader
extends ClassLoader {
    private static final ClassLoader INSTANCE = new EhcacheDefaultClassLoader();
    private final ClassLoader ehcacheLoader = EhcacheDefaultClassLoader.class.getClassLoader();

    public static ClassLoader getInstance() {
        return INSTANCE;
    }

    private EhcacheDefaultClassLoader() {
        super(null);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != null) {
            try {
                return tccl.loadClass(name);
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return this.ehcacheLoader.loadClass(name);
    }

    @Override
    public URL getResource(String name) {
        URL url;
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != null && (url = tccl.getResource(name)) != null) {
            return url;
        }
        return this.ehcacheLoader.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL resource = this.getResource(name);
        try {
            return resource == null ? null : resource.openStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> urls;
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != null && (urls = tccl.getResources(name)) != null && urls.hasMoreElements()) {
            return urls;
        }
        return this.ehcacheLoader.getResources(name);
    }
}

