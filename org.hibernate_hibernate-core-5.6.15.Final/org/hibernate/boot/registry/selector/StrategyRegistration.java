/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.selector;

public interface StrategyRegistration<T> {
    public Class<T> getStrategyRole();

    public Iterable<String> getSelectorNames();

    public Class<? extends T> getStrategyImplementation();
}

