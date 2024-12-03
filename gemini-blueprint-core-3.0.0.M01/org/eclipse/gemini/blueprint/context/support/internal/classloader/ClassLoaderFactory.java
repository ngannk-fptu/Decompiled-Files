/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.support.internal.classloader;

import org.eclipse.gemini.blueprint.context.support.internal.classloader.BundleClassLoaderFactory;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.CachingAopClassLoaderFactory;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.CachingBundleClassLoaderFactory;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.ChainedClassLoader;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.InternalAopClassLoaderFactory;
import org.osgi.framework.Bundle;
import org.springframework.util.Assert;

public abstract class ClassLoaderFactory {
    private static InternalAopClassLoaderFactory aopClassLoaderFactory = new CachingAopClassLoaderFactory();
    private static BundleClassLoaderFactory bundleClassLoaderFactory = new CachingBundleClassLoaderFactory();

    public static ChainedClassLoader getAopClassLoaderFor(ClassLoader classLoader) {
        Assert.notNull((Object)classLoader);
        return aopClassLoaderFactory.createClassLoader(classLoader);
    }

    public static ClassLoader getBundleClassLoaderFor(Bundle bundle) {
        Assert.notNull((Object)bundle);
        return bundleClassLoaderFactory.createClassLoader(bundle);
    }
}

