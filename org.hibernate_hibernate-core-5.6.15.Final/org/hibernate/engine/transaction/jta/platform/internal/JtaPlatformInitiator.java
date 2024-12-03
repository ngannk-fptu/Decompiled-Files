/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformResolver;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.jboss.logging.Logger;

public class JtaPlatformInitiator
implements StandardServiceInitiator<JtaPlatform> {
    public static final JtaPlatformInitiator INSTANCE = new JtaPlatformInitiator();
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)JtaPlatformInitiator.class.getName());

    @Override
    public Class<JtaPlatform> getServiceInitiated() {
        return JtaPlatform.class;
    }

    @Override
    public JtaPlatform initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Object setting = configurationValues.get("hibernate.transaction.jta.platform");
        JtaPlatform platform = registry.getService(StrategySelector.class).resolveStrategy(JtaPlatform.class, setting);
        if (platform == null) {
            LOG.debug("No JtaPlatform was specified, checking resolver");
            platform = registry.getService(JtaPlatformResolver.class).resolveJtaPlatform(configurationValues, registry);
        }
        if (platform == null) {
            LOG.debug("No JtaPlatform was specified, checking resolver");
            platform = this.getFallbackProvider(configurationValues, registry);
        }
        LOG.usingJtaPlatform(platform != null ? platform.getClass().getName() : "null");
        return platform;
    }

    protected JtaPlatform getFallbackProvider(Map configurationValues, ServiceRegistryImplementor registry) {
        return null;
    }
}

