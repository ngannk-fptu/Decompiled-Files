/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.core.net.HttpClientRequest
 *  com.atlassian.sal.core.net.SystemPropertiesConnectionConfig
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.Advisor
 */
package com.atlassian.confluence.api.impl.sal;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.impl.sal.AbstractHttpClientFactory;
import com.atlassian.confluence.api.impl.sal.util.HttpTimeoutParameters;
import com.atlassian.confluence.api.impl.sal.util.OutboundWhiteListBypassUtil;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.AopUtils;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.core.net.HttpClientRequest;
import com.atlassian.sal.core.net.SystemPropertiesConnectionConfig;
import com.atlassian.spring.container.ContainerManager;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;

public class ConfluenceHttpClientRequestFactory
extends AbstractHttpClientFactory {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceHttpClientRequestFactory.class);
    private static final boolean NET_REQUEST_ALLOW_ALL_HOSTS = Boolean.getBoolean("net.request.allow.all.hosts");
    private final SettingsManager settingsManager;
    private final BandanaManager bandanaManager;
    private OutboundWhitelist outboundWhitelist;

    public ConfluenceHttpClientRequestFactory(SettingsManager settingsManager, Advisor classLoaderAdvisor, BandanaManager bandanaManager1) {
        super(classLoaderAdvisor);
        this.settingsManager = settingsManager;
        this.bandanaManager = bandanaManager1;
    }

    public Request createRequest(Request.MethodType methodType, String url) {
        if (!OutboundWhiteListBypassUtil.byPassOutboundWhitelist(url, (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "synchrony_collaborative_editor_app_base_url"))) {
            this.checkOutboundWhitelistForUrl(url);
        }
        HttpClientRequest request = this.httpClientRequestFactory.createRequest(methodType, url);
        ConfluenceHttpParameters confluenceHttpParameters = this.settingsManager.getGlobalSettings().getConfluenceHttpParameters();
        SystemPropertiesConnectionConfig systemPropConfigs = new SystemPropertiesConnectionConfig();
        request.setConnectionTimeout(HttpTimeoutParameters.getConnectionTimeoutFrom(systemPropConfigs, confluenceHttpParameters));
        request.setSoTimeout(HttpTimeoutParameters.getSocketTimeoutFrom(systemPropConfigs, confluenceHttpParameters));
        return AopUtils.createAdvisedProxy((Object)request, this.classLoaderAdvisor, Request.class);
    }

    private void checkOutboundWhitelistForUrl(String url) {
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

