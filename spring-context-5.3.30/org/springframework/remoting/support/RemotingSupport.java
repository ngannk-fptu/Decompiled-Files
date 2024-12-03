/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.remoting.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public abstract class RemotingSupport
implements BeanClassLoaderAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Nullable
    protected ClassLoader overrideThreadContextClassLoader() {
        return ClassUtils.overrideThreadContextClassLoader((ClassLoader)this.getBeanClassLoader());
    }

    protected void resetThreadContextClassLoader(@Nullable ClassLoader original) {
        if (original != null) {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}

