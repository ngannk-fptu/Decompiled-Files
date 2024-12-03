/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class DialectFactoryInitiator
implements StandardServiceInitiator<DialectFactory> {
    public static final DialectFactoryInitiator INSTANCE = new DialectFactoryInitiator();

    @Override
    public Class<DialectFactory> getServiceInitiated() {
        return DialectFactory.class;
    }

    @Override
    public DialectFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new DialectFactoryImpl();
    }
}

