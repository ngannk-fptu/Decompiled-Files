/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.support.internal.classloader;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.springframework.util.Assert;

public class ChainedClassLoader
extends ClassLoader {
    private final List<ClassLoader> loaders = new ArrayList<ClassLoader>();
    private final List<ClassLoader> nonOsgiLoaders = new ArrayList<ClassLoader>();
    private final ClassLoader parent;

    public ChainedClassLoader(ClassLoader[] loaders) {
        this(loaders, ClassUtils.getFwkClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChainedClassLoader(ClassLoader[] loaders, ClassLoader parent) {
        super(parent);
        this.parent = parent;
        Assert.notEmpty((Object[])loaders);
        List<ClassLoader> list = this.loaders;
        synchronized (list) {
            for (int i = 0; i < loaders.length; ++i) {
                ClassLoader classLoader = loaders[i];
                Assert.notNull((Object)classLoader, (String)"null classloaders not allowed");
                this.addClassLoader(classLoader);
            }
        }
    }

    @Override
    public URL getResource(final String name) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(new PrivilegedAction<URL>(){

                @Override
                public URL run() {
                    return ChainedClassLoader.this.doGetResource(name);
                }
            });
        }
        return this.doGetResource(name);
    }

    private URL doGetResource(String name) {
        URL url = this.doGetResource(name, this.loaders);
        if (url != null) {
            return url;
        }
        url = this.doGetResource(name, this.nonOsgiLoaders);
        if (url != null) {
            return url;
        }
        return this.parent != null ? this.parent.getResource(name) : url;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private URL doGetResource(String name, List<ClassLoader> classLoaders) {
        URL url = null;
        List<ClassLoader> list = classLoaders;
        synchronized (list) {
            for (int i = 0; i < classLoaders.size(); ++i) {
                ClassLoader loader = classLoaders.get(i);
                url = loader.getResource(name);
                if (url == null) continue;
                return url;
            }
        }
        return url;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (System.getSecurityManager() != null) {
            try {
                return (Class)AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>(){

                    @Override
                    public Class<?> run() throws Exception {
                        return ChainedClassLoader.this.doLoadClass(name);
                    }
                });
            }
            catch (PrivilegedActionException pae) {
                throw (ClassNotFoundException)pae.getException();
            }
        }
        return this.doLoadClass(name);
    }

    private Class<?> doLoadClass(String name) throws ClassNotFoundException {
        Class<?> clazz = this.doLoadClass(name, this.loaders);
        if (clazz != null) {
            return clazz;
        }
        clazz = this.doLoadClass(name, this.nonOsgiLoaders);
        if (clazz != null) {
            return clazz;
        }
        if (this.parent != null) {
            return this.parent.loadClass(name);
        }
        throw new ClassNotFoundException(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Class<?> doLoadClass(String name, List<ClassLoader> classLoaders) throws ClassNotFoundException {
        Class<?> clazz = null;
        List<ClassLoader> list = classLoaders;
        synchronized (list) {
            for (int i = 0; i < classLoaders.size(); ++i) {
                ClassLoader loader = classLoaders.get(i);
                try {
                    clazz = loader.loadClass(name);
                    return clazz;
                }
                catch (ClassNotFoundException classNotFoundException) {
                    continue;
                }
            }
        }
        return clazz;
    }

    public void addClassLoader(final Class<?> clazz) {
        Assert.notNull(clazz, (String)"a non-null class required");
        if (System.getSecurityManager() != null) {
            this.addClassLoader(AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

                @Override
                public ClassLoader run() {
                    return ClassUtils.getClassLoader(clazz);
                }
            }));
        } else {
            this.addClassLoader(ClassUtils.getClassLoader(clazz));
        }
    }

    public void addClassLoader(ClassLoader classLoader) {
        Assert.notNull((Object)classLoader, (String)"a non-null classLoader required");
        if (!this.addNonOsgiLoader(classLoader)) {
            this.addOsgiLoader(classLoader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean addNonOsgiLoader(ClassLoader classLoader) {
        if (ClassUtils.knownNonOsgiLoadersSet.contains(classLoader)) {
            List<ClassLoader> list = this.nonOsgiLoaders;
            synchronized (list) {
                int index;
                if (!this.nonOsgiLoaders.contains(classLoader) && (index = ClassUtils.knownNonOsgiLoaders.indexOf(classLoader)) >= 0) {
                    int insertIndex = 0;
                    for (int i = 0; i < this.nonOsgiLoaders.size(); ++i) {
                        int presentLoaderIndex = ClassUtils.knownNonOsgiLoaders.indexOf(this.nonOsgiLoaders.get(i));
                        if (presentLoaderIndex < 0 || presentLoaderIndex >= index) continue;
                        insertIndex = i + 1;
                    }
                    this.nonOsgiLoaders.add(insertIndex, classLoader);
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addOsgiLoader(ClassLoader classLoader) {
        List<ClassLoader> list = this.loaders;
        synchronized (list) {
            if (!this.loaders.contains(classLoader)) {
                this.loaders.add(classLoader);
            }
        }
    }
}

