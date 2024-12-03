/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.util;

import org.aspectj.apache.bcel.util.ClassLoaderReference;

public class DefaultClassLoaderReference
implements ClassLoaderReference {
    private ClassLoader loader;

    public DefaultClassLoaderReference(ClassLoader classLoader) {
        this.loader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.loader;
    }
}

