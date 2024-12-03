/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ModelMap
extends LinkedHashMap<String, Object> {
    public ModelMap() {
    }

    public ModelMap(String attributeName, @Nullable Object attributeValue) {
        this.addAttribute(attributeName, attributeValue);
    }

    public ModelMap(Object attributeValue) {
        this.addAttribute(attributeValue);
    }

    public ModelMap addAttribute(String attributeName, @Nullable Object attributeValue) {
        Assert.notNull((Object)attributeName, "Model attribute name must not be null");
        this.put(attributeName, attributeValue);
        return this;
    }

    public ModelMap addAttribute(Object attributeValue) {
        Assert.notNull(attributeValue, "Model object must not be null");
        if (attributeValue instanceof Collection && ((Collection)attributeValue).isEmpty()) {
            return this;
        }
        return this.addAttribute(Conventions.getVariableName(attributeValue), attributeValue);
    }

    public ModelMap addAllAttributes(@Nullable Collection<?> attributeValues) {
        if (attributeValues != null) {
            for (Object attributeValue : attributeValues) {
                this.addAttribute(attributeValue);
            }
        }
        return this;
    }

    public ModelMap addAllAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            this.putAll(attributes);
        }
        return this;
    }

    public ModelMap mergeAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach((key, value) -> {
                if (!this.containsKey(key)) {
                    this.put(key, value);
                }
            });
        }
        return this;
    }

    public boolean containsAttribute(String attributeName) {
        return this.containsKey(attributeName);
    }
}

