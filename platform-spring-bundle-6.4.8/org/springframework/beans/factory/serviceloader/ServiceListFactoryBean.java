/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.serviceloader;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.serviceloader.AbstractServiceLoaderBasedFactoryBean;

public class ServiceListFactoryBean
extends AbstractServiceLoaderBasedFactoryBean
implements BeanClassLoaderAware {
    @Override
    protected Object getObjectToExpose(ServiceLoader<?> serviceLoader) {
        ArrayList result = new ArrayList();
        for (Object loaderObject : serviceLoader) {
            result.add(loaderObject);
        }
        return result;
    }

    @Override
    public Class<?> getObjectType() {
        return List.class;
    }
}

