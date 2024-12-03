/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

import org.hibernate.service.Service;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;

public interface SessionFactoryServiceRegistryBuilder {
    public SessionFactoryServiceRegistryBuilder addInitiator(SessionFactoryServiceInitiator var1);

    public SessionFactoryServiceRegistryBuilder addService(Class var1, Service var2);
}

