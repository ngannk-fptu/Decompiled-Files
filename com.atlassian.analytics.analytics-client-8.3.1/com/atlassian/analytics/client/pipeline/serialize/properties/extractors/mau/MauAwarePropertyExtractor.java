/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau;

import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general.MetaPropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau.MauService;
import java.util.Map;

public class MauAwarePropertyExtractor {
    private final MauService mauService;
    private final MetaPropertyExtractor metaPropertyExtractor;

    MauAwarePropertyExtractor(MauService mauService, MetaPropertyExtractor propertiesMetaExtractor) {
        this.mauService = mauService;
        this.metaPropertyExtractor = propertiesMetaExtractor;
    }

    public Map<String, Object> getEventPropertiesWithHashedEmail(Object event, Map<String, Object> properties) {
        Map<String, Object> newEventProperties = this.metaPropertyExtractor.getEventPropertiesWithClientEventDecorating(event, properties);
        return this.mauService.hashEmailPropertyForMauEvent(event, newEventProperties);
    }

    public String getRequestCorrelationIdButSuppressForMau(RequestInfo requestInfo, Object event) {
        if (this.mauService.isMauEvent(event)) {
            return "";
        }
        return this.metaPropertyExtractor.getRequestCorrelationId(requestInfo);
    }

    public String getUserButSuppressForMau(Object event, Map<String, Object> properties) {
        if (this.mauService.isMauEvent(event)) {
            return "suppressed";
        }
        return this.metaPropertyExtractor.getUser(event, properties);
    }
}

