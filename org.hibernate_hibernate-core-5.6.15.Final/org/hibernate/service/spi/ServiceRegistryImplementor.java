/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceBinding;

public interface ServiceRegistryImplementor
extends ServiceRegistry {
    public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> var1);

    @Override
    default public void close() {
        this.destroy();
    }

    public void destroy();

    public void registerChild(ServiceRegistryImplementor var1);

    public void deRegisterChild(ServiceRegistryImplementor var1);
}

