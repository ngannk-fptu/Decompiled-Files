/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MemberAttributeConfig;
import java.util.Collections;
import java.util.Map;

public class MemberAttributeConfigReadOnly
extends MemberAttributeConfig {
    MemberAttributeConfigReadOnly(MemberAttributeConfig source) {
        super(source);
    }

    @Override
    public void setStringAttribute(String key, String value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setBooleanAttribute(String key, boolean value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setByteAttribute(String key, byte value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setShortAttribute(String key, short value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setIntAttribute(String key, int value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setLongAttribute(String key, long value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setFloatAttribute(String key, float value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setDoubleAttribute(String key, double value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void removeAttribute(String key) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setAttributes(Map<String, Object> attributes) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(super.getAttributes());
    }
}

