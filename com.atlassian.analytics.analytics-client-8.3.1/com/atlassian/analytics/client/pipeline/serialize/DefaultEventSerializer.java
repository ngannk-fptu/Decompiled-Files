/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.client.pipeline.serialize;

import com.atlassian.analytics.client.extractor.FieldExtractor;
import com.atlassian.analytics.client.pipeline.serialize.EventSerializer;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.analytics.client.pipeline.serialize.properties.ExtractionSupplier;
import com.atlassian.analytics.client.pipeline.serialize.properties.dto.ForExtractionDTO;
import com.atlassian.analytics.event.RawEvent;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class DefaultEventSerializer
implements EventSerializer {
    private final FieldExtractor fieldExtractor;
    private final ExtractionSupplier extractionSupplier;

    public DefaultEventSerializer(FieldExtractor fieldExtractor, ExtractionSupplier extractionSupplier) {
        this.fieldExtractor = fieldExtractor;
        this.extractionSupplier = extractionSupplier;
    }

    @Override
    public Supplier<RawEvent> toAnalyticsEvent(Object event, @Nullable String sessionId, RequestInfo requestInfo) {
        Map<String, Object> properties = this.fieldExtractor.extractEventProperties(event);
        ForExtractionDTO forExtractionDTO = new ForExtractionDTO(event, properties, requestInfo, sessionId);
        return this.extractionSupplier.toExtractionSupplier(forExtractionDTO);
    }
}

