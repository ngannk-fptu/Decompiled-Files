/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MCMutualAuthConfig;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.util.Preconditions;

public class ManagementCenterConfig {
    static final int UPDATE_INTERVAL = 3;
    private boolean enabled;
    private boolean scriptingEnabled = !BuildInfoProvider.getBuildInfo().isEnterprise();
    private String url;
    private int updateInterval = 3;
    private MCMutualAuthConfig mutualAuthConfig;

    public ManagementCenterConfig() {
    }

    public ManagementCenterConfig(String url, int dataUpdateInterval) {
        this.url = url;
        this.updateInterval = dataUpdateInterval;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public ManagementCenterConfig setScriptingEnabled(boolean scriptingEnabled) {
        this.scriptingEnabled = scriptingEnabled;
        return this;
    }

    public boolean isScriptingEnabled() {
        return this.scriptingEnabled;
    }

    public ManagementCenterConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public ManagementCenterConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getUpdateInterval() {
        return this.updateInterval;
    }

    public ManagementCenterConfig setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    public ManagementCenterConfig setMutualAuthConfig(MCMutualAuthConfig mutualAuthConfig) {
        Preconditions.checkNotNull(mutualAuthConfig);
        this.mutualAuthConfig = mutualAuthConfig;
        return this;
    }

    public MCMutualAuthConfig getMutualAuthConfig() {
        return this.mutualAuthConfig;
    }

    public String toString() {
        return "ManagementCenterConfig{enabled=" + this.enabled + ", url='" + this.url + "', updateInterval=" + this.updateInterval + ", mcMutualAuthConfig=" + this.mutualAuthConfig + "}";
    }
}

