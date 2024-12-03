/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.math.NumberUtils
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.domain;

import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.config.JfrProperty;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class JfrPropertyDto {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final Object value;
    @JsonProperty
    private final boolean mutable;

    JfrPropertyDto(String name, Object value, boolean mutable) {
        this.name = name;
        this.value = value;
        this.mutable = mutable;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean isMutable() {
        return this.mutable;
    }

    public static JfrPropertyDto create(JfrProperty jfrProperty, JfrProperties properties) {
        String value = properties.getProperty(jfrProperty);
        return new JfrPropertyDto(jfrProperty.getPropertyName(), NumberUtils.isNumber((String)value) ? NumberUtils.createNumber((String)value.trim()) : value, jfrProperty.isMutable());
    }
}

