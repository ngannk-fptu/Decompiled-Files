/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.entity.UrlEncodedFormEntity
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.message.BasicNameValuePair
 *  org.apache.http.util.EntityUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.upm.analytics.impl;

import com.atlassian.marketplace.client.http.HttpConfiguration;
import com.atlassian.marketplace.client.impl.CommonsHttpTransport;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.analytics.event.UpmUiAnalyticsEvent;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.HttpClientFactory;
import com.atlassian.upm.core.analytics.AnalyticsEvent;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.AnalyticsPublisher;
import com.atlassian.upm.core.pac.ClientContextFactory;
import com.atlassian.upm.core.pac.MarketplaceClientConfiguration;
import com.atlassian.upm.core.pac.MarketplaceClientManager;
import java.io.Closeable;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import javax.ws.rs.core.UriBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class MpacAnalyticsPublisher
implements AnalyticsPublisher {
    private static final int CONNECT_TIMEOUT_MILLIS = 2000;
    private static final int READ_TIMEOUT_MILLIS = 2000;
    private static final String PLUGIN_KEY = "pk";
    private static final String PLUGIN_VERSION = "pv";
    private static final String SEN = "psen";
    private static final Logger logger = LoggerFactory.getLogger(MpacAnalyticsPublisher.class);
    private final HttpClient httpClient;
    private final ClientContextFactory clientContextFactory;
    private final MarketplaceClientManager marketplaceClientFactory;
    private final AnalyticsLogger analytics;
    private final SysPersisted sysPersisted;

    public MpacAnalyticsPublisher(AnalyticsLogger analytics, ClientContextFactory clientContextFactory, MarketplaceClientManager marketplaceClientFactory, SysPersisted sysPersisted, @Qualifier(value="mpacAnalyticsHttpClientFactory") HttpClientFactory httpClientFactory) {
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.clientContextFactory = Objects.requireNonNull(clientContextFactory, "clientContextFactory");
        this.marketplaceClientFactory = Objects.requireNonNull(marketplaceClientFactory, "marketplaceClientFactory");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.httpClient = Objects.requireNonNull(httpClientFactory, "httpClientFactory").createClient();
    }

    @Override
    public void publish(AnalyticsEvent event) throws Exception {
        if (event.isRecordedByMarketplace()) {
            String eventType = event instanceof UpmUiAnalyticsEvent ? "ui/" + event.getEventType() : event.getEventType();
            this.sendAuditEvent(eventType, event.getInvolvedPluginInfo(), event.getMetadata());
        }
    }

    private void sendAuditEvent(String type, Iterable<AnalyticsEvent.AnalyticsEventInfo> pluginKeysAndVersions, Iterable<Pair<String, String>> metadata) throws Exception {
        if (!this.sysPersisted.is(UpmSettings.PAC_DISABLED)) {
            UriBuilder uri = UriBuilder.fromUri((String)UpmSys.getMpacBaseUrl()).path("rest/1.0/plugins/usage").path(type);
            HttpPost post = new HttpPost(uri.build(new Object[0]));
            ArrayList<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
            for (AnalyticsEvent.AnalyticsEventInfo analyticsEventInfo : pluginKeysAndVersions) {
                formParams.add(new BasicNameValuePair(PLUGIN_KEY, analyticsEventInfo.getPluginKey()));
                formParams.add(new BasicNameValuePair(PLUGIN_VERSION, analyticsEventInfo.getVersion()));
                formParams.add(new BasicNameValuePair(SEN, analyticsEventInfo.getSen().getOrElse("")));
            }
            for (Pair pair : metadata) {
                formParams.add(new BasicNameValuePair((String)pair.first(), (String)pair.second()));
            }
            post.setEntity((HttpEntity)new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8));
            post.addHeader("User-Agent", this.marketplaceClientFactory.getUserAgent());
            post.addHeader("X-Pac-Client-Info", this.clientContextFactory.getClientContext().toString());
            HttpResponse response = this.httpClient.execute((HttpUriRequest)post);
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("MPAC returned error " + response.getStatusLine().getStatusCode() + " for audit event (" + type + ")");
            }
            if (response.getEntity() != null) {
                EntityUtils.consumeQuietly((HttpEntity)response.getEntity());
            }
        }
    }

    public void afterPropertiesSet() {
        this.analytics.register(this);
    }

    public void destroy() throws Exception {
        this.analytics.unregister(this);
        if (this.httpClient instanceof Closeable) {
            ((Closeable)this.httpClient).close();
        }
    }

    public static class ClientFactory
    implements HttpClientFactory {
        @Override
        public HttpClient createClient() {
            HttpConfiguration config = MarketplaceClientConfiguration.httpConfigurationFromSystemProperties().connectTimeoutMillis(2000).readTimeoutMillis(2000).build();
            return CommonsHttpTransport.createHttpClient(config, UpmFugueConverters.fugueNone(URI.class));
        }
    }
}

