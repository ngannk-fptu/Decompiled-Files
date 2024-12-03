/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.config.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.engine.config.internal.ConfigurationServiceImpl;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class ConfigurationServiceInitiator
implements StandardServiceInitiator<ConfigurationService> {
    public static final ConfigurationServiceInitiator INSTANCE = new ConfigurationServiceInitiator();

    @Override
    public ConfigurationService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new ConfigurationServiceImpl(configurationValues);
    }

    @Override
    public Class<ConfigurationService> getServiceInitiated() {
        return ConfigurationService.class;
    }
}

