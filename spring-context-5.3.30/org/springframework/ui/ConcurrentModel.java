/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Conventions
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.ui;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.util.Assert;

public class ConcurrentModel
extends ConcurrentHashMap<String, Object>
implements Model {
    public ConcurrentModel() {
    }

    public ConcurrentModel(String attributeName, Object attributeValue) {
        this.addAttribute(attributeName, attributeValue);
    }

    public ConcurrentModel(Object attributeValue) {
        this.addAttribute(attributeValue);
    }

    @Override
    @Nullable
    public Object put(String key, @Nullable Object value) {
        if (value != null) {
            return super.put(key, value);
        }
        return this.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public ConcurrentModel addAttribute(String attributeName, @Nullable Object attributeValue) {
        Assert.notNull((Object)attributeName, (String)"Model attribute name must not be null");
        this.put(attributeName, attributeValue);
        return this;
    }

    @Override
    public ConcurrentModel addAttribute(Object attributeValue) {
        Assert.notNull((Object)attributeValue, (String)"Model attribute value must not be null");
        if (attributeValue instanceof Collection && ((Collection)attributeValue).isEmpty()) {
            return this;
        }
        return this.addAttribute(Conventions.getVariableName((Object)attributeValue), attributeValue);
    }

    @Override
    public ConcurrentModel addAllAttributes(@Nullable Collection<?> attributeValues) {
        if (attributeValues != null) {
            for (Object attributeValue : attributeValues) {
                this.addAttribute(attributeValue);
            }
        }
        return this;
    }

    @Override
    public ConcurrentModel addAllAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            this.putAll((Map<? extends String, ?>)attributes);
        }
        return this;
    }

    @Override
    public ConcurrentModel mergeAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach((key, value) -> {
                if (!this.containsKey(key)) {
                    this.put((String)key, value);
                }
            });
        }
        return this;
    }

    @Override
    public boolean containsAttribute(String attributeName) {
        return this.containsKey(attributeName);
    }

    @Override
    @Nullable
    public Object getAttribute(String attributeName) {
        return this.get(attributeName);
    }

    @Override
    public Map<String, Object> asMap() {
        return this;
    }
}

