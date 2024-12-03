/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.boot.spi;

import java.util.List;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;

public interface StrategyRegistrationProviderList {
    public List<StrategyRegistrationProvider> getStrategyRegistrationProviders();
}

