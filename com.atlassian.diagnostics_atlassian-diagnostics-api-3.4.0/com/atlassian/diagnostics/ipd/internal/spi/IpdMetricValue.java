/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.ipd.internal.spi;

import java.util.Map;

public class IpdMetricValue {
    private final String label;
    private final String objectName;
    private final Map<String, String> tags;
    private final Map<String, Object> attributes;

    public IpdMetricValue(String label, String objectName, Map<String, String> tags, Map<String, Object> attributes) {
        this.label = label;
        this.objectName = objectName;
        this.tags = tags;
        this.attributes = attributes;
    }

    public String getLabel() {
        return this.label;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}

