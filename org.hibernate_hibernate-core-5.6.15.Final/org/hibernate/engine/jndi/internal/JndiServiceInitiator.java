/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jndi.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.engine.jndi.internal.JndiServiceImpl;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class JndiServiceInitiator
implements StandardServiceInitiator<JndiService> {
    public static final JndiServiceInitiator INSTANCE = new JndiServiceInitiator();

    @Override
    public Class<JndiService> getServiceInitiated() {
        return JndiService.class;
    }

    @Override
    public JndiService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new JndiServiceImpl(configurationValues);
    }
}

