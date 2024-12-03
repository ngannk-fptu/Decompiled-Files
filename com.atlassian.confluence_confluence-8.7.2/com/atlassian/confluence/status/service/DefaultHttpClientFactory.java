/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClients
 */
package com.atlassian.confluence.status.service;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.status.service.HttpClientFactory;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

class DefaultHttpClientFactory
implements HttpClientFactory {
    private final SettingsManager settingsManager;

    public DefaultHttpClientFactory(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public CloseableHttpClient getInstance() {
        ConfluenceHttpParameters confluenceHttpParameters = this.settingsManager.getGlobalSettings().getConfluenceHttpParameters();
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(confluenceHttpParameters.getSocketTimeout()).setConnectTimeout(confluenceHttpParameters.getConnectionTimeout()).setCookieSpec("standard").build();
        return this.getInstance(defaultRequestConfig);
    }

    @Override
    public CloseableHttpClient getInstance(RequestConfig requestConfig) {
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).disableAutomaticRetries().disableRedirectHandling().evictExpiredConnections().build();
    }
}

