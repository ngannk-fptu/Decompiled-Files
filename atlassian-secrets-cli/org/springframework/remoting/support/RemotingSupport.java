/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Nullable
    protected ClassLoader overrideThreadContextClassLoader() {
        return ClassUtils.overrideThreadContextClassLoader(this.getBeanClassLoader());
    }

    protected void resetThreadContextClassLoader(@Nullable ClassLoader original) {
        if (original != null) {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}

