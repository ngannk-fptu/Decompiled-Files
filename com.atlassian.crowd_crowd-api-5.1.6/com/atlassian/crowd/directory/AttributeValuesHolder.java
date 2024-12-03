/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.Attributes;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.math.NumberUtils;

public class AttributeValuesHolder
implements Attributes {
    private final Map<String, String> attributes;

    public AttributeValuesHolder(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Set<String> getValues(String name) {
        String value = this.getValue(name);
        if (value != null) {
            return Collections.singleton(value);
        }
        return null;
    }

    public String getValue(String name) {
        return this.attributes.get(name);
    }

    public long getAttributeAsLong(String name, long defaultValue) {
        String value = this.getValue(name);
        if (NumberUtils.isNumber((String)value)) {
            return NumberUtils.createLong((String)value);
        }
        return defaultValue;
    }

    public boolean getAttributeAsBoolean(String name, boolean defaultValue) {
        String value = this.getValue(name);
        if (value != null) {
            return Boolean.valueOf(value);
        }
        return defaultValue;
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }
}

