/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.util.http.ConfluenceHttpParameters
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.TrustedRequestFactory
 *  com.atlassian.sal.core.net.HttpClientRequest
 *  com.atlassian.sal.core.net.HttpClientTrustedRequest
 *  com.atlassian.sal.core.net.HttpClientTrustedRequestFactory
 *  com.atlassian.sal.core.net.SystemPropertiesConnectionConfig
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.net;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.TrustedRequestFactory;
import com.atlassian.sal.confluence.net.util.HttpTimeoutParameters;
import com.atlassian.sal.confluence.net.util.OutboundWhiteListBypassUtil;
import com.atlassian.sal.core.net.HttpClientRequest;
import com.atlassian.sal.core.net.HttpClientTrustedRequest;
import com.atlassian.sal.core.net.HttpClientTrustedRequestFactory;
import com.atlassian.sal.core.net.SystemPropertiesConnectionConfig;
import com.atlassian.spring.container.ContainerManager;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceHttpClientTrustedRequestFactory
implements TrustedRequestFactory<HttpClientTrustedRequest> {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceHttpClientTrustedRequestFactory.class);
    private static final boolean NET_REQUEST_ALLOW_ALL_HOSTS = Boolean.getBoolean("net.request.allow.all.hosts");
    private final GlobalSettingsManager settingsManager;
    private final HttpClientTrustedRequestFactory trustedRequestFactory;
    private OutboundWhitelist outboundWhitelist;
    private final BandanaManager bandanaManager;

    public ConfluenceHttpClientTrustedRequestFactory(GlobalSettingsManager settingsManager, HttpClientTrustedRequestFactory trustedRequestFactory, BandanaManager bandanaManager) {
        this.settingsManager = settingsManager;
        this.trustedRequestFactory = trustedRequestFactory;
        this.bandanaManager = bandanaManager;
    }

    public HttpClientTrustedRequest createTrustedRequest(Request.MethodType methodType, String url) {
        if (!OutboundWhiteListBypassUtil.byPassOutboundWhitelist(url, (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "synchrony_collaborative_editor_app_base_url"))) {
            this.checkOutboundWhitelistForUrl(url);
        }
        HttpClientTrustedRequest request = this.trustedRequestFactory.createTrustedRequest(methodType, url);
        this.adjustTimeouts((HttpClientRequest)request);
        return request;
    }

    private void adjustTimeouts(HttpClientRequest request) {
        ConfluenceHttpParameters confluenceHttpParameters = this.settingsManager.getGlobalSettings().getConfluenceHttpParameters();
        SystemPropertiesConnectionConfig systemPropConfigs = new SystemPropertiesConnectionConfig();
        request.setConnectionTimeout(HttpTimeoutParameters.getConnectionTimeoutFrom(systemPropConfigs, confluenceHttpParameters));
        request.setSoTimeout(HttpTimeoutParameters.getSocketTimeoutFrom(systemPropConfigs, confluenceHttpParameters));
    }

    public HttpClientRequest createRequest(Request.MethodType methodType, String url) {
        if (!OutboundWhiteListBypassUtil.byPassOutboundWhitelist(url, (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "synchrony_collaborative_editor_app_base_url"))) {
            this.checkOutboundWhitelistForUrl(url);
        }
        HttpClientRequest request = this.trustedRequestFactory.createRequest(methodType, url);
        this.adjustTimeouts(request);
        return request;
    }

    public boolean supportsHeader() {
        return this.trustedRequestFactory.supportsHeader();
    }

    protected void checkOutboundWhitelistForUrl(String url) {
        if (!NET_REQUEST_ALLOW_ALL_HOSTS) {
            if (null == this.outboundWhitelist) {
                this.outboundWhitelist = (OutboundWhitelist)ContainerManager.getComponent((String)"outboundWhitelist");
            }
            if (null != this.outboundWhitelist && !this.outboundWhitelist.isAllowed(URI.create(url))) {
                log.warn(String.format("The provided url- %s is not included in the whitelist!.Please add the url to whitelist to allow access", url));
                throw new IllegalArgumentException(String.format("The provided url- %s is not included in the whitelist!.Please add the url to whitelist to allow access", url));
            }
        }
    }
}

