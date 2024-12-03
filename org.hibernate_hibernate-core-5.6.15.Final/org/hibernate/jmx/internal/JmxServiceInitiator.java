/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jmx.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jmx.internal.DisabledJmxServiceImpl;
import org.hibernate.jmx.internal.JmxServiceImpl;
import org.hibernate.jmx.spi.JmxService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class JmxServiceInitiator
implements StandardServiceInitiator<JmxService> {
    public static final JmxServiceInitiator INSTANCE = new JmxServiceInitiator();

    @Override
    public Class<JmxService> getServiceInitiated() {
        return JmxService.class;
    }

    @Override
    public JmxService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        if (ConfigurationHelper.getBoolean("hibernate.jmx.enabled", configurationValues, false)) {
            DeprecationLogger.DEPRECATION_LOGGER.deprecatedJmxSupport("hibernate.jmx.enabled");
            return new JmxServiceImpl(configurationValues);
        }
        return DisabledJmxServiceImpl.INSTANCE;
    }
}

