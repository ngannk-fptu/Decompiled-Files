/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.ProxyFactory
 */
package org.eclipse.gemini.blueprint.context.support.internal.classloader;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.ChainedClassLoader;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.InternalAopClassLoaderFactory;
import org.springframework.aop.framework.ProxyFactory;

class CachingAopClassLoaderFactory
implements InternalAopClassLoaderFactory {
    private static final String CGLIB_CLASS = "org.springframework.cglib.proxy.Enhancer";
    private final Class<?> cglibClass;
    private final Map<ClassLoader, WeakReference<ChainedClassLoader>> cache = new WeakHashMap<ClassLoader, WeakReference<ChainedClassLoader>>();

    CachingAopClassLoaderFactory() {
        ClassLoader springAopClassLoader = ProxyFactory.class.getClassLoader();
        Class<?> clazz = null;
        try {
            clazz = springAopClassLoader.loadClass(CGLIB_CLASS);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        this.cglibClass = clazz;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChainedClassLoader createClassLoader(final ClassLoader classLoader) {
        Map<ClassLoader, WeakReference<ChainedClassLoader>> map = this.cache;
        synchronized (map) {
            ChainedClassLoader aopClassLoader = null;
            WeakReference<ChainedClassLoader> loaderReference = this.cache.get(classLoader);
            if (loaderReference != null) {
                aopClassLoader = (ChainedClassLoader)loaderReference.get();
            }
            if (aopClassLoader == null) {
                aopClassLoader = System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<ChainedClassLoader>(){

                    @Override
                    public ChainedClassLoader run() {
                        return CachingAopClassLoaderFactory.this.doCreateClassLoader(classLoader);
                    }
                }) : this.doCreateClassLoader(classLoader);
                this.cache.put(classLoader, new WeakReference<ChainedClassLoader>(aopClassLoader));
            }
            return aopClassLoader;
        }
    }

    private ChainedClassLoader doCreateClassLoader(ClassLoader classLoader) {
        if (this.cglibClass != null) {
            return new ChainedClassLoader(new ClassLoader[]{classLoader, ProxyFactory.class.getClassLoader(), this.cglibClass.getClassLoader(), CachingAopClassLoaderFactory.class.getClassLoader()});
        }
        return new ChainedClassLoader(new ClassLoader[]{classLoader, ProxyFactory.class.getClassLoader(), CachingAopClassLoaderFactory.class.getClassLoader()});
    }
}

