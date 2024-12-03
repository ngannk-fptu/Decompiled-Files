/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class TransactionCoordinatorBuilderInitiator
implements StandardServiceInitiator<TransactionCoordinatorBuilder> {
    public static final String LEGACY_SETTING_NAME = "hibernate.transaction.factory_class";
    public static final TransactionCoordinatorBuilderInitiator INSTANCE = new TransactionCoordinatorBuilderInitiator();

    @Override
    public TransactionCoordinatorBuilder initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return registry.getService(StrategySelector.class).resolveDefaultableStrategy(TransactionCoordinatorBuilder.class, TransactionCoordinatorBuilderInitiator.determineStrategySelection(configurationValues), JdbcResourceLocalTransactionCoordinatorBuilderImpl.INSTANCE);
    }

    private static Object determineStrategySelection(Map configurationValues) {
        Object coordinatorStrategy = configurationValues.get("hibernate.transaction.coordinator_class");
        if (coordinatorStrategy != null) {
            return coordinatorStrategy;
        }
        Object legacySetting = configurationValues.get(LEGACY_SETTING_NAME);
        if (legacySetting != null) {
            DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedTransactionFactorySetting(LEGACY_SETTING_NAME, "hibernate.transaction.coordinator_class");
            return legacySetting;
        }
        return null;
    }

    @Override
    public Class<TransactionCoordinatorBuilder> getServiceInitiated() {
        return TransactionCoordinatorBuilder.class;
    }
}

