/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.lang.ref.WeakReference;

public class WeakClassLoaderReference {
    protected final int hashcode;
    private final WeakReference loaderRef;

    public WeakClassLoaderReference(ClassLoader loader) {
        this.loaderRef = new WeakReference<ClassLoader>(loader);
        this.hashcode = loader == null ? System.identityHashCode(this) : loader.hashCode() * 37;
    }

    public ClassLoader getClassLoader() {
        ClassLoader instance = (ClassLoader)this.loaderRef.get();
        return instance;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WeakClassLoaderReference)) {
            return false;
        }
        WeakClassLoaderReference other = (WeakClassLoaderReference)obj;
        return other.hashcode == this.hashcode;
    }

    public int hashCode() {
        return this.hashcode;
    }
}

