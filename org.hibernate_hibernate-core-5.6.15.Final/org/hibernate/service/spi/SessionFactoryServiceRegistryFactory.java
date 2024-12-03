/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.Service;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public interface SessionFactoryServiceRegistryFactory
extends Service {
    public SessionFactoryServiceRegistry buildServiceRegistry(SessionFactoryImplementor var1, SessionFactoryOptions var2);
}

