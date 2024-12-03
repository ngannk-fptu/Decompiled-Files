/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.classloader;

import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.classloader.ClassLoaderAwareCache;
import net.sf.ehcache.terracotta.InternalEhcache;

public class InternalClassLoaderAwareCache
extends ClassLoaderAwareCache
implements InternalEhcache {
    public InternalClassLoaderAwareCache(InternalEhcache cache, ClassLoader classLoader) {
        super(cache, classLoader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element removeAndReturnElement(Object arg0) throws IllegalStateException {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            Element element = ((InternalEhcache)this.cache).removeAndReturnElement(arg0);
            return element;
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recalculateSize(Object arg0) {
        Thread t = Thread.currentThread();
        ClassLoader prev = t.getContextClassLoader();
        t.setContextClassLoader(this.classLoader);
        try {
            ((InternalEhcache)this.cache).recalculateSize(arg0);
        }
        finally {
            t.setContextClassLoader(prev);
        }
    }
}

