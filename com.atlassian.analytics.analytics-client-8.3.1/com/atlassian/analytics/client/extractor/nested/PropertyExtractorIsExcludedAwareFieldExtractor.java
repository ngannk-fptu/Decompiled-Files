/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.extractor.nested;

import com.atlassian.analytics.client.extractor.FieldExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyExtractorIsExcludedAwareFieldExtractor
implements FieldExtractor {
    private final FieldExtractor fieldExtractor;
    private final PropertyExtractor propertyExtractor;

    public PropertyExtractorIsExcludedAwareFieldExtractor(FieldExtractor fieldExtractor, PropertyExtractor propertyExtractor) {
        this.fieldExtractor = fieldExtractor;
        this.propertyExtractor = propertyExtractor;
    }

    @Override
    public Map<String, Object> extractEventProperties(Object event) {
        HashMap<String, Object> properties = new HashMap<String, Object>(this.fieldExtractor.extractEventProperties(event));
        return properties.entrySet().stream().filter(entry -> !this.propertyExtractor.isExcluded((String)entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

