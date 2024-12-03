/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.registry.selector.internal;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
import org.hibernate.boot.registry.selector.StrategyRegistration;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.boot.registry.selector.internal.DefaultDialectSelector;
import org.hibernate.boot.registry.selector.internal.DefaultJtaPlatformSelector;
import org.hibernate.boot.registry.selector.internal.StrategySelectorImpl;
import org.hibernate.boot.registry.selector.spi.StrategySelectionException;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.internal.SimpleCacheKeysFactory;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.persistent.PersistentTableBulkIdStrategy;
import org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.jboss.logging.Logger;

public class StrategySelectorBuilder {
    private static final Logger log = Logger.getLogger(StrategySelectorBuilder.class);
    private final List<StrategyRegistration> explicitStrategyRegistrations = new ArrayList<StrategyRegistration>();

    public <T> void addExplicitStrategyRegistration(Class<T> strategy, Class<? extends T> implementation, String name) {
        this.addExplicitStrategyRegistration(new SimpleStrategyRegistrationImpl<T>(strategy, implementation, name));
    }

    public <T> void addExplicitStrategyRegistration(StrategyRegistration<T> strategyRegistration) {
        if (!strategyRegistration.getStrategyRole().isInterface()) {
            log.debug((Object)("Registering non-interface strategy : " + strategyRegistration.getStrategyRole().getName()));
        }
        if (!strategyRegistration.getStrategyRole().isAssignableFrom(strategyRegistration.getStrategyImplementation())) {
            throw new StrategySelectionException("Implementation class [" + strategyRegistration.getStrategyImplementation().getName() + "] does not implement strategy interface [" + strategyRegistration.getStrategyRole().getName() + "]");
        }
        this.explicitStrategyRegistrations.add(strategyRegistration);
    }

    public StrategySelector buildSelector(ClassLoaderService classLoaderService) {
        StrategySelectorImpl strategySelector = new StrategySelectorImpl(classLoaderService);
        strategySelector.registerStrategyLazily(Dialect.class, new DefaultDialectSelector());
        strategySelector.registerStrategyLazily(JtaPlatform.class, new DefaultJtaPlatformSelector());
        this.addTransactionCoordinatorBuilders(strategySelector);
        this.addMultiTableBulkIdStrategies(strategySelector);
        this.addImplicitNamingStrategies(strategySelector);
        this.addCacheKeysFactories(strategySelector);
        for (StrategyRegistrationProvider provider : classLoaderService.loadJavaServices(StrategyRegistrationProvider.class)) {
            for (StrategyRegistration discoveredStrategyRegistration : provider.getStrategyRegistrations()) {
                this.applyFromStrategyRegistration(strategySelector, discoveredStrategyRegistration);
            }
        }
        for (StrategyRegistration explicitStrategyRegistration : this.explicitStrategyRegistrations) {
            this.applyFromStrategyRegistration(strategySelector, explicitStrategyRegistration);
        }
        return strategySelector;
    }

    private <T> void applyFromStrategyRegistration(StrategySelectorImpl strategySelector, StrategyRegistration<T> strategyRegistration) {
        for (String name : strategyRegistration.getSelectorNames()) {
            strategySelector.registerStrategyImplementor(strategyRegistration.getStrategyRole(), name, strategyRegistration.getStrategyImplementation());
        }
    }

    private void addTransactionCoordinatorBuilders(StrategySelectorImpl strategySelector) {
        strategySelector.registerStrategyImplementor(TransactionCoordinatorBuilder.class, "jdbc", JdbcResourceLocalTransactionCoordinatorBuilderImpl.class);
        strategySelector.registerStrategyImplementor(TransactionCoordinatorBuilder.class, "jta", JtaTransactionCoordinatorBuilderImpl.class);
        strategySelector.registerStrategyImplementor(TransactionCoordinatorBuilder.class, "org.hibernate.transaction.JDBCTransactionFactory", JdbcResourceLocalTransactionCoordinatorBuilderImpl.class);
        strategySelector.registerStrategyImplementor(TransactionCoordinatorBuilder.class, "org.hibernate.transaction.JTATransactionFactory", JtaTransactionCoordinatorBuilderImpl.class);
        strategySelector.registerStrategyImplementor(TransactionCoordinatorBuilder.class, "org.hibernate.transaction.CMTTransactionFactory", JtaTransactionCoordinatorBuilderImpl.class);
    }

    private void addMultiTableBulkIdStrategies(StrategySelectorImpl strategySelector) {
        strategySelector.registerStrategyImplementor(MultiTableBulkIdStrategy.class, "persistent", PersistentTableBulkIdStrategy.class);
        strategySelector.registerStrategyImplementor(MultiTableBulkIdStrategy.class, "global_temporary", GlobalTemporaryTableBulkIdStrategy.class);
        strategySelector.registerStrategyImplementor(MultiTableBulkIdStrategy.class, "local_temporary", LocalTemporaryTableBulkIdStrategy.class);
    }

    private void addImplicitNamingStrategies(StrategySelectorImpl strategySelector) {
        strategySelector.registerStrategyImplementor(ImplicitNamingStrategy.class, "default", ImplicitNamingStrategyJpaCompliantImpl.class);
        strategySelector.registerStrategyImplementor(ImplicitNamingStrategy.class, "jpa", ImplicitNamingStrategyJpaCompliantImpl.class);
        strategySelector.registerStrategyImplementor(ImplicitNamingStrategy.class, "legacy-jpa", ImplicitNamingStrategyLegacyJpaImpl.class);
        strategySelector.registerStrategyImplementor(ImplicitNamingStrategy.class, "legacy-hbm", ImplicitNamingStrategyLegacyHbmImpl.class);
        strategySelector.registerStrategyImplementor(ImplicitNamingStrategy.class, "component-path", ImplicitNamingStrategyComponentPathImpl.class);
    }

    private void addCacheKeysFactories(StrategySelectorImpl strategySelector) {
        strategySelector.registerStrategyImplementor(CacheKeysFactory.class, "default", DefaultCacheKeysFactory.class);
        strategySelector.registerStrategyImplementor(CacheKeysFactory.class, "simple", SimpleCacheKeysFactory.class);
    }
}

