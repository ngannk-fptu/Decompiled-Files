/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public interface SessionFactoryServiceInitiatorContext {
    public SessionFactoryImplementor getSessionFactory();

    public SessionFactoryOptions getSessionFactoryOptions();

    public ServiceRegistryImplementor getServiceRegistry();
}

