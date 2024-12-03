/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.selector;

import org.hibernate.boot.registry.selector.StrategyRegistration;

public interface StrategyRegistrationProvider {
    public Iterable<StrategyRegistration> getStrategyRegistrations();
}

