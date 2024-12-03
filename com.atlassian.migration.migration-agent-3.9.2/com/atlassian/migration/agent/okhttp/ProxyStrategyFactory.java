/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.okhttp.ProxyStrategy;
import com.atlassian.migration.agent.okhttp.ProxyType;
import java.util.EnumMap;
import java.util.Set;

public class ProxyStrategyFactory {
    private EnumMap<ProxyType, ProxyStrategy> strategies;

    public ProxyStrategyFactory(Set<ProxyStrategy> strategySet) {
        this.createStrategy(strategySet);
    }

    public ProxyStrategy getProxyStrategy(ProxyType proxyType) {
        return this.strategies.get((Object)proxyType);
    }

    private void createStrategy(Set<ProxyStrategy> strategySet) {
        this.strategies = new EnumMap(ProxyType.class);
        strategySet.forEach(strategy -> this.strategies.put(strategy.getProxyType(), (ProxyStrategy)strategy));
    }
}

