/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl
 *  org.hibernate.boot.registry.selector.StrategyRegistration
 *  org.hibernate.boot.registry.selector.StrategyRegistrationProvider
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 */
package org.hibernate.c3p0.internal;

import java.util.Collections;
import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
import org.hibernate.boot.registry.selector.StrategyRegistration;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

public final class StrategyRegistrationProviderImpl
implements StrategyRegistrationProvider {
    public Iterable<StrategyRegistration> getStrategyRegistrations() {
        SimpleStrategyRegistrationImpl c3p0 = new SimpleStrategyRegistrationImpl(ConnectionProvider.class, C3P0ConnectionProvider.class, new String[]{"c3p0", C3P0ConnectionProvider.class.getSimpleName(), "org.hibernate.connection.C3P0ConnectionProvider", "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider"});
        return Collections.singleton(c3p0);
    }
}

