/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.serviceloader;

import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.serviceloader.AbstractServiceLoaderBasedFactoryBean;

public class ServiceLoaderFactoryBean
extends AbstractServiceLoaderBasedFactoryBean
implements BeanClassLoaderAware {
    @Override
    protected Object getObjectToExpose(ServiceLoader<?> serviceLoader) {
        return serviceLoader;
    }

    @Override
    public Class<?> getObjectType() {
        return ServiceLoader.class;
    }
}

