/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.Service;
import org.hibernate.service.spi.ServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceInitiatorContext;

public interface SessionFactoryServiceInitiator<R extends Service>
extends ServiceInitiator<R> {
    default public R initiateService(SessionFactoryServiceInitiatorContext context) {
        return this.initiateService(context.getSessionFactory(), context.getSessionFactoryOptions(), context.getServiceRegistry());
    }

    @Deprecated
    public R initiateService(SessionFactoryImplementor var1, SessionFactoryOptions var2, ServiceRegistryImplementor var3);
}

