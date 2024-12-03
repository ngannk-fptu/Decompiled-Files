/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.UrlMode
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general;

import com.atlassian.analytics.client.api.ClientEvent;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.analytics.event.EventUtils;
import com.atlassian.sal.api.UrlMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MetaPropertyExtractor {
    private final PropertyExtractor propertyExtractor;
    private final AnalyticsPropertyService analyticsPropertyService;
    private final SenProvider senProvider;
    private final AtomicReference<String> server = new AtomicReference();

    MetaPropertyExtractor(PropertyExtractor propertyExtractor, AnalyticsPropertyService analyticsPropertyService, SenProvider senProvider) {
        this.propertyExtractor = propertyExtractor;
        this.analyticsPropertyService = analyticsPropertyService;
        this.senProvider = senProvider;
    }

    public String getApplicationAccess() {
        return this.propertyExtractor.getApplicationAccess();
    }

    public String extractAtlPathFromRequestInfo(RequestInfo requestInfo) {
        return requestInfo.getAtlPath();
    }

    public long getClientTime(Object event) {
        return this.getClientTimeFromEvent(event).orElse(this.getCurrentTime());
    }

    private Optional<Long> getClientTimeFromEvent(Object event) {
        return Optional.ofNullable(event).filter(ClientEvent.class::isInstance).map(ClientEvent.class::cast).map(ClientEvent::getClientTime);
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public String getEventName(Object event, Map<String, Object> properties) {
        String originalName = this.propertyExtractor.extractName(event);
        return EventUtils.getEventName(originalName, properties);
    }

    public Map<String, Object> getEventPropertiesWithClientEventDecorating(Object event, Map<String, Object> properties) {
        String originalName = this.propertyExtractor.extractName(event);
        return this.getEventProperties(properties, originalName);
    }

    private Map<String, Object> getEventProperties(Map<String, Object> usingProperties, String originalName) {
        return EventUtils.getEventProperties(originalName, usingProperties);
    }

    public String getRequestCorrelationId(RequestInfo requestInfo) {
        return this.propertyExtractor.extractRequestCorrelationId(requestInfo);
    }

    public String getSen() {
        return this.senProvider.getSen().orElse(null);
    }

    public String getServer() {
        String result = this.server.get();
        if (result != null) {
            return result;
        }
        String baseUrl = this.analyticsPropertyService.getBaseUrl(UrlMode.CANONICAL);
        try {
            result = new URL(baseUrl).getHost();
            this.server.set(result);
            return result;
        }
        catch (MalformedURLException e) {
            return "-";
        }
    }

    public String getSessionId(String sessionId) {
        return sessionId == null ? null : this.getHashedSessionId(sessionId);
    }

    private String getHashedSessionId(String sessionId) {
        return Integer.toString(sessionId.hashCode());
    }

    public String getSourceIp(RequestInfo requestInfo) {
        return requestInfo.getSourceIp();
    }

    public String getSubProduct(Object event) {
        String product = this.getProduct();
        return this.propertyExtractor.extractSubProduct(event, product);
    }

    public String getProduct() {
        return this.analyticsPropertyService.getDisplayName().toLowerCase();
    }

    public String getUser(Object event, Map<String, Object> properties) {
        return this.propertyExtractor.extractUser(event, properties);
    }

    public String getVersion() {
        return this.analyticsPropertyService.getVersion();
    }
}

