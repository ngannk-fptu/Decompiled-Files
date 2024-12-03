/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate;

import java.util.Locale;
import java.util.Map;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public enum MultiTenancyStrategy {
    DISCRIMINATOR,
    SCHEMA,
    DATABASE,
    NONE;

    private static final CoreMessageLogger LOG;

    public boolean requiresMultiTenantConnectionProvider() {
        return this == DATABASE || this == SCHEMA;
    }

    public static MultiTenancyStrategy determineMultiTenancyStrategy(Map properties) {
        Object strategy = properties.get("hibernate.multiTenancy");
        if (strategy == null) {
            return NONE;
        }
        if (MultiTenancyStrategy.class.isInstance(strategy)) {
            return (MultiTenancyStrategy)((Object)strategy);
        }
        String strategyName = strategy.toString();
        try {
            return MultiTenancyStrategy.valueOf(strategyName.toUpperCase(Locale.ROOT));
        }
        catch (RuntimeException e) {
            LOG.warn("Unknown multi tenancy strategy [ " + strategyName + " ], using MultiTenancyStrategy.NONE.");
            return NONE;
        }
    }

    static {
        LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)MultiTenancyStrategy.class.getName());
    }
}

