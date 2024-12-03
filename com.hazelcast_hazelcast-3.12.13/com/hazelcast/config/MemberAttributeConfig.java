/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MemberAttributeConfigReadOnly;
import java.util.HashMap;
import java.util.Map;

public class MemberAttributeConfig {
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public MemberAttributeConfig() {
    }

    public MemberAttributeConfig(MemberAttributeConfig source) {
        this.attributes.putAll(source.attributes);
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes.clear();
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
    }

    public String getStringAttribute(String key) {
        return (String)this.getAttribute(key);
    }

    public void setStringAttribute(String key, String value) {
        this.setAttribute(key, value);
    }

    public Boolean getBooleanAttribute(String key) {
        return (Boolean)this.getAttribute(key);
    }

    public void setBooleanAttribute(String key, boolean value) {
        this.setAttribute(key, value);
    }

    public Byte getByteAttribute(String key) {
        return (Byte)this.getAttribute(key);
    }

    public void setByteAttribute(String key, byte value) {
        this.setAttribute(key, value);
    }

    public Short getShortAttribute(String key) {
        return (Short)this.getAttribute(key);
    }

    public void setShortAttribute(String key, short value) {
        this.setAttribute(key, value);
    }

    public Integer getIntAttribute(String key) {
        return (Integer)this.getAttribute(key);
    }

    public void setIntAttribute(String key, int value) {
        this.setAttribute(key, value);
    }

    public Long getLongAttribute(String key) {
        return (Long)this.getAttribute(key);
    }

    public void setLongAttribute(String key, long value) {
        this.setAttribute(key, value);
    }

    public Float getFloatAttribute(String key) {
        return (Float)this.getAttribute(key);
    }

    public void setFloatAttribute(String key, float value) {
        this.setAttribute(key, Float.valueOf(value));
    }

    public Double getDoubleAttribute(String key) {
        return (Double)this.getAttribute(key);
    }

    public void setDoubleAttribute(String key, double value) {
        this.setAttribute(key, value);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }

    public MemberAttributeConfig asReadOnly() {
        return new MemberAttributeConfigReadOnly(this);
    }

    private Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    private void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }
}

