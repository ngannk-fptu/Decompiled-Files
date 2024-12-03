/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 */
package com.atlassian.confluence.util.http;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.confluence.util.http.HttpRequest;
import com.atlassian.confluence.util.http.HttpRequestConfig;
import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.http.HttpRetrievalService;
import com.atlassian.confluence.util.http.HttpRetrievalServiceConfig;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import java.io.IOException;

@Deprecated(forRemoval=true)
public abstract class BaseHttpRetrievalService
implements HttpRetrievalService {
    protected BandanaManager bandanaManager;
    private SettingsManager settingsManager;
    protected OutboundWhitelist outboundWhitelist;

    @Override
    public HttpResponse get(String url) throws IOException {
        return this.get(this.getDefaultRequestFor(url));
    }

    @Override
    public HttpRequest getDefaultRequestFor(String url) {
        HttpRetrievalServiceConfig serviceConfig = this.getHttpRetrievalServiceConfig();
        HttpRequestConfig requestConfig = this.findMatchingRequestConfig(serviceConfig, url);
        HttpRequest request = new HttpRequest();
        request.setMaximumCacheAgeInMillis(requestConfig.getMaxCacheAge());
        request.setMaximumSize(requestConfig.getMaxDownloadSize());
        request.setUrl(url);
        request.setAuthenticator(requestConfig.getAuthenticator());
        return request;
    }

    private HttpRequestConfig findMatchingRequestConfig(HttpRetrievalServiceConfig globalConfig, String url) {
        HttpRequestConfig config = globalConfig.getDefaultConfiguration();
        for (HttpRequestConfig possibleConfig : this.getHttpRetrievalServiceConfig().getConfigurations()) {
            if (!possibleConfig.matches(url)) continue;
            config = possibleConfig;
            break;
        }
        return config;
    }

    @Override
    public HttpRetrievalServiceConfig getHttpRetrievalServiceConfig() {
        HttpRetrievalServiceConfig config = (HttpRetrievalServiceConfig)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.util.http");
        if (config == null) {
            config = this.generateVirginConfig();
        }
        return config;
    }

    public ConfluenceHttpParameters getConnectionParameters() {
        return this.settingsManager.getGlobalSettings().getConfluenceHttpParameters();
    }

    @Override
    public void setHttpRetrievalServiceConfig(HttpRetrievalServiceConfig config) {
        this.saveConfig(config);
    }

    private HttpRetrievalServiceConfig generateVirginConfig() {
        HttpRetrievalServiceConfig config = new HttpRetrievalServiceConfig();
        this.saveConfig(config);
        return config;
    }

    private void saveConfig(HttpRetrievalServiceConfig config) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.util.http", (Object)config);
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setOutboundWhitelist(OutboundWhitelist outboundWhitelist) {
        this.outboundWhitelist = outboundWhitelist;
    }
}

