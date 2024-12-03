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
import org.hibernate.engine.transaction.jta.platform.internal.StandardJtaPlatformResolver;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformResolver;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.jboss.logging.Logger;

public class JtaPlatformResolverInitiator
implements StandardServiceInitiator<JtaPlatformResolver> {
    public static final JtaPlatformResolverInitiator INSTANCE = new JtaPlatformResolverInitiator();
    private static final Logger log = Logger.getLogger(JtaPlatformResolverInitiator.class);

    @Override
    public JtaPlatformResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Object setting = configurationValues.get("hibernate.transaction.jta.platform_resolver");
        JtaPlatformResolver resolver = registry.getService(StrategySelector.class).resolveStrategy(JtaPlatformResolver.class, setting);
        if (resolver == null) {
            log.debugf("No JtaPlatformResolver was specified, using default [%s]", (Object)StandardJtaPlatformResolver.class.getName());
            return StandardJtaPlatformResolver.INSTANCE;
        }
        return resolver;
    }

    @Override
    public Class<JtaPlatformResolver> getServiceInitiated() {
        return JtaPlatformResolver.class;
    }
}

