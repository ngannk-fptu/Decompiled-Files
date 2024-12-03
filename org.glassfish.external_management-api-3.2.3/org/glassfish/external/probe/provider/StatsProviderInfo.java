/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.probe.provider;

import org.glassfish.external.probe.provider.PluginPoint;

public class StatsProviderInfo {
    private String configElement;
    private PluginPoint pp;
    private String subTreeRoot;
    private Object statsProvider;
    private String configLevelStr = null;
    private final String invokerId;

    public StatsProviderInfo(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider) {
        this(configElement, pp, subTreeRoot, statsProvider, null);
    }

    public StatsProviderInfo(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider, String invokerId) {
        this.configElement = configElement;
        this.pp = pp;
        this.subTreeRoot = subTreeRoot;
        this.statsProvider = statsProvider;
        this.invokerId = invokerId;
    }

    public String getConfigElement() {
        return this.configElement;
    }

    public PluginPoint getPluginPoint() {
        return this.pp;
    }

    public String getSubTreeRoot() {
        return this.subTreeRoot;
    }

    public Object getStatsProvider() {
        return this.statsProvider;
    }

    public String getConfigLevel() {
        return this.configLevelStr;
    }

    public void setConfigLevel(String configLevelStr) {
        this.configLevelStr = configLevelStr;
    }

    public String getInvokerId() {
        return this.invokerId;
    }
}

