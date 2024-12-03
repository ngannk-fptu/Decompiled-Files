/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  okhttp3.OkHttpClient$Builder
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.okhttp.ProxyStrategy;
import com.atlassian.migration.agent.okhttp.ProxyStrategyFactory;
import com.atlassian.migration.agent.okhttp.ProxyType;
import com.atlassian.sal.api.features.DarkFeatureManager;
import okhttp3.OkHttpClient;

public class OKHttpProxyBuilder {
    private final DarkFeatureManager darkFeatureManager;
    private final ProxyStrategyFactory proxyStrategyFactory;
    private static final String ENABLE_MIGRATION_HTTPS_PROXY_FEATURE_FLAG = "migration-assistant.enable.migration.https.proxy";

    public OKHttpProxyBuilder(DarkFeatureManager darkFeatureManager, ProxyStrategyFactory proxyStrategyFactory) {
        this.darkFeatureManager = darkFeatureManager;
        this.proxyStrategyFactory = proxyStrategyFactory;
    }

    public OkHttpClient.Builder getProxyBuilder() {
        return this.getProxyStrategy().getProxyBuilder();
    }

    private ProxyStrategy getProxyStrategy() {
        if (this.darkFeatureManager.isEnabledForAllUsers(ENABLE_MIGRATION_HTTPS_PROXY_FEATURE_FLAG).orElse(false).booleanValue()) {
            return this.proxyStrategyFactory.getProxyStrategy(ProxyType.HTTPS);
        }
        return this.proxyStrategyFactory.getProxyStrategy(ProxyType.HTTP);
    }
}

