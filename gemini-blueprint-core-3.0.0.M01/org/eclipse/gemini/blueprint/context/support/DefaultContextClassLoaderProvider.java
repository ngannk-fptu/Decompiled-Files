/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanClassLoaderAware
 */
package org.eclipse.gemini.blueprint.context.support;

import org.eclipse.gemini.blueprint.context.support.ContextClassLoaderProvider;
import org.springframework.beans.factory.BeanClassLoaderAware;

public class DefaultContextClassLoaderProvider
implements ContextClassLoaderProvider,
BeanClassLoaderAware {
    private ClassLoader beanClassLoader;

    @Override
    public ClassLoader getContextClassLoader() {
        return this.beanClassLoader != null ? this.beanClassLoader : Thread.currentThread().getContextClassLoader();
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
}

