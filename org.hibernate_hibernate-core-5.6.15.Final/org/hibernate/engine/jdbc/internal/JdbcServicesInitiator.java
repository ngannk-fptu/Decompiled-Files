/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.engine.jdbc.internal.JdbcServicesImpl;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class JdbcServicesInitiator
implements StandardServiceInitiator<JdbcServices> {
    public static final JdbcServicesInitiator INSTANCE = new JdbcServicesInitiator();

    @Override
    public Class<JdbcServices> getServiceInitiated() {
        return JdbcServices.class;
    }

    @Override
    public JdbcServices initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new JdbcServicesImpl();
    }
}

