/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.serviceloader;

import java.util.Iterator;
import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.serviceloader.AbstractServiceLoaderBasedFactoryBean;
import org.springframework.lang.Nullable;

public class ServiceFactoryBean
extends AbstractServiceLoaderBasedFactoryBean
implements BeanClassLoaderAware {
    @Override
    protected Object getObjectToExpose(ServiceLoader<?> serviceLoader) {
        Iterator<?> it = serviceLoader.iterator();
        if (!it.hasNext()) {
            throw new IllegalStateException("ServiceLoader could not find service for type [" + this.getServiceType() + "]");
        }
        return it.next();
    }

    @Override
    @Nullable
    public Class<?> getObjectType() {
        return this.getServiceType();
    }
}

