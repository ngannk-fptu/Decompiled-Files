/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service;

import org.hibernate.service.NullServiceException;
import org.hibernate.service.Service;

public interface ServiceRegistry
extends AutoCloseable {
    public ServiceRegistry getParentServiceRegistry();

    public <R extends Service> R getService(Class<R> var1);

    default public <R extends Service> R requireService(Class<R> serviceRole) {
        R service = this.getService(serviceRole);
        if (service == null) {
            throw new NullServiceException(serviceRole);
        }
        return service;
    }

    @Override
    public void close();
}

