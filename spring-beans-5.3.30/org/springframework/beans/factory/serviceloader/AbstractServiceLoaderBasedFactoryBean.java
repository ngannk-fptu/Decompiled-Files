/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.beans.factory.serviceloader;

import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class AbstractServiceLoaderBasedFactoryBean
extends AbstractFactoryBean<Object>
implements BeanClassLoaderAware {
    @Nullable
    private Class<?> serviceType;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    public void setServiceType(@Nullable Class<?> serviceType) {
        this.serviceType = serviceType;
    }

    @Nullable
    public Class<?> getServiceType() {
        return this.serviceType;
    }

    @Override
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    protected Object createInstance() {
        Assert.notNull(this.getServiceType(), (String)"Property 'serviceType' is required");
        return this.getObjectToExpose(ServiceLoader.load(this.getServiceType(), this.beanClassLoader));
    }

    protected abstract Object getObjectToExpose(ServiceLoader<?> var1);
}

