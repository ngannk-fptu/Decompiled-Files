/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

public class UserCodeDeploymentConfig {
    private ClassCacheMode classCacheMode = ClassCacheMode.ETERNAL;
    private ProviderMode providerMode = ProviderMode.LOCAL_AND_CACHED_CLASSES;
    private String blacklistedPrefixes;
    private String whitelistedPrefixes;
    private String providerFilter;
    private boolean enabled;

    public UserCodeDeploymentConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public UserCodeDeploymentConfig setProviderFilter(String providerFilter) {
        this.providerFilter = providerFilter;
        return this;
    }

    public String getProviderFilter() {
        return this.providerFilter;
    }

    public UserCodeDeploymentConfig setBlacklistedPrefixes(String blacklistedPrefixes) {
        this.blacklistedPrefixes = blacklistedPrefixes;
        return this;
    }

    public String getBlacklistedPrefixes() {
        return this.blacklistedPrefixes;
    }

    public UserCodeDeploymentConfig setWhitelistedPrefixes(String whitelistedPrefixes) {
        this.whitelistedPrefixes = whitelistedPrefixes;
        return this;
    }

    public String getWhitelistedPrefixes() {
        return this.whitelistedPrefixes;
    }

    public UserCodeDeploymentConfig setProviderMode(ProviderMode providerMode) {
        this.providerMode = providerMode;
        return this;
    }

    public ProviderMode getProviderMode() {
        return this.providerMode;
    }

    public UserCodeDeploymentConfig setClassCacheMode(ClassCacheMode classCacheMode) {
        this.classCacheMode = classCacheMode;
        return this;
    }

    public ClassCacheMode getClassCacheMode() {
        return this.classCacheMode;
    }

    public static enum ProviderMode {
        OFF,
        LOCAL_CLASSES_ONLY,
        LOCAL_AND_CACHED_CLASSES;

    }

    public static enum ClassCacheMode {
        OFF,
        ETERNAL;

    }
}

