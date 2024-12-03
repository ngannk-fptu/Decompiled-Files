/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl
 *  org.hibernate.boot.registry.selector.StrategyRegistration
 *  org.hibernate.boot.registry.selector.StrategyRegistrationProvider
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 */
package org.hibernate.hikaricp.internal;

import java.util.Collections;
import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
import org.hibernate.boot.registry.selector.StrategyRegistration;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;

public final class StrategyRegistrationProviderImpl
implements StrategyRegistrationProvider {
    public Iterable<StrategyRegistration> getStrategyRegistrations() {
        SimpleStrategyRegistrationImpl strategyRegistration = new SimpleStrategyRegistrationImpl(ConnectionProvider.class, HikariCPConnectionProvider.class, new String[]{"hikari", "hikaricp", HikariCPConnectionProvider.class.getSimpleName(), "org.hibernate.connection.HikariCPConnectionProvider"});
        return Collections.singleton(strategyRegistration);
    }
}

