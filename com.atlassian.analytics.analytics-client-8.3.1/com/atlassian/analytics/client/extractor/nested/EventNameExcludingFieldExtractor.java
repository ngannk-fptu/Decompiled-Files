/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.extractor.nested;

import com.atlassian.analytics.client.extractor.FieldExtractor;
import java.util.Map;

public class EventNameExcludingFieldExtractor
implements FieldExtractor {
    private final FieldExtractor fieldExtractor;

    public EventNameExcludingFieldExtractor(FieldExtractor fieldExtractor) {
        this.fieldExtractor = fieldExtractor;
    }

    @Override
    public Map<String, Object> extractEventProperties(Object event) {
        Map<String, Object> properties = this.fieldExtractor.extractEventProperties(event);
        properties.remove("eventName");
        return properties;
    }
}

