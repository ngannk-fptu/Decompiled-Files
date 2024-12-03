/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.eclipse.gemini.blueprint.context.support.internal.classloader;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.BundleClassLoaderFactory;
import org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader;
import org.osgi.framework.Bundle;

class CachingBundleClassLoaderFactory
implements BundleClassLoaderFactory {
    private static final String DELIMITER = "|";
    private final Map<Bundle, Map<Object, WeakReference<ClassLoader>>> cache = new WeakHashMap<Bundle, Map<Object, WeakReference<ClassLoader>>>();

    CachingBundleClassLoaderFactory() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ClassLoader createClassLoader(Bundle bundle) {
        ClassLoader loader = null;
        Object key = this.createKeyFor(bundle);
        Map<Object, WeakReference<ClassLoader>> loaders = null;
        Map<Object, Object> map = this.cache;
        synchronized (map) {
            loaders = this.cache.get(bundle);
            if (loaders == null) {
                loaders = new HashMap<Object, WeakReference<ClassLoader>>(4);
                loader = this.createBundleClassLoader(bundle);
                loaders.put(key, new WeakReference<ClassLoader>(loader));
                return loader;
            }
        }
        map = loaders;
        synchronized (map) {
            WeakReference<ClassLoader> reference = loaders.get(key);
            if (reference != null) {
                loader = (ClassLoader)reference.get();
            }
            if (loader == null) {
                loader = this.createBundleClassLoader(bundle);
                loaders.put(key, new WeakReference<ClassLoader>(loader));
            }
            return loader;
        }
    }

    private Object createKeyFor(Bundle bundle) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(bundle.getBundleId());
        buffer.append(DELIMITER);
        buffer.append(Long.toHexString(bundle.getLastModified()));
        buffer.append(DELIMITER);
        buffer.append(bundle.getClass().getName());
        return buffer.toString();
    }

    private ClassLoader createBundleClassLoader(Bundle bundle) {
        return BundleDelegatingClassLoader.createBundleClassLoaderFor(bundle);
    }
}

