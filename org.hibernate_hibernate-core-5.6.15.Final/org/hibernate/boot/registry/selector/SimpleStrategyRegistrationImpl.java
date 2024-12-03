/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.selector;

import java.util.Arrays;
import org.hibernate.boot.registry.selector.StrategyRegistration;

public class SimpleStrategyRegistrationImpl<T>
implements StrategyRegistration<T> {
    private final Class<T> strategyRole;
    private final Class<? extends T> strategyImplementation;
    private final Iterable<String> selectorNames;

    public SimpleStrategyRegistrationImpl(Class<T> strategyRole, Class<? extends T> strategyImplementation, Iterable<String> selectorNames) {
        this.strategyRole = strategyRole;
        this.strategyImplementation = strategyImplementation;
        this.selectorNames = selectorNames;
    }

    public SimpleStrategyRegistrationImpl(Class<T> strategyRole, Class<? extends T> strategyImplementation, String ... selectorNames) {
        this(strategyRole, strategyImplementation, Arrays.asList(selectorNames));
    }

    @Override
    public Class<T> getStrategyRole() {
        return this.strategyRole;
    }

    @Override
    public Iterable<String> getSelectorNames() {
        return this.selectorNames;
    }

    @Override
    public Class<? extends T> getStrategyImplementation() {
        return this.strategyImplementation;
    }
}

