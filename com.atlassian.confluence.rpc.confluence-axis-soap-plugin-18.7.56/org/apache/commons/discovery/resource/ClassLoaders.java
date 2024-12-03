/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.resource;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.discovery.jdk.JDKHooks;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ClassLoaders {
    protected List<ClassLoader> classLoaders = new LinkedList<ClassLoader>();

    public int size() {
        return this.classLoaders.size();
    }

    public ClassLoader get(int idx) {
        return this.classLoaders.get(idx);
    }

    public void put(ClassLoader classLoader) {
        if (classLoader != null) {
            this.classLoaders.add(classLoader);
        }
    }

    public void put(ClassLoader classLoader, boolean prune) {
        if (!(classLoader == null || prune && this.isAncestor(classLoader))) {
            this.classLoaders.add(classLoader);
        }
    }

    public boolean isAncestor(ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        for (int idx = 0; idx < this.size(); ++idx) {
            for (ClassLoader walker = this.get(idx); walker != null; walker = walker.getParent()) {
                if (walker != classLoader) continue;
                return true;
            }
        }
        return false;
    }

    public static ClassLoaders getLibLoaders(Class<?> spi, Class<?> factory, boolean prune) {
        ClassLoaders loaders = new ClassLoaders();
        if (spi != null) {
            loaders.put(spi.getClassLoader());
        }
        if (factory != null) {
            loaders.put(factory.getClassLoader(), prune);
        }
        loaders.put(JDKHooks.getJDKHooks().getSystemClassLoader(), prune);
        return loaders;
    }

    public static ClassLoaders getAppLoaders(Class<?> spi, Class<?> factory, boolean prune) {
        ClassLoaders loaders = new ClassLoaders();
        loaders.put(JDKHooks.getJDKHooks().getThreadContextClassLoader());
        if (spi != null) {
            loaders.put(spi.getClassLoader(), prune);
        }
        if (factory != null) {
            loaders.put(factory.getClassLoader(), prune);
        }
        loaders.put(JDKHooks.getJDKHooks().getSystemClassLoader(), prune);
        return loaders;
    }
}

