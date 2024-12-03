/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.ConfigNumber;
import com.typesafe.config.impl.SerializedConfigValue;
import java.io.ObjectStreamException;
import java.io.Serializable;

final class ConfigLong
extends ConfigNumber
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final long value;

    ConfigLong(ConfigOrigin origin, long value, String originalText) {
        super(origin, originalText);
        this.value = value;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.NUMBER;
    }

    @Override
    public Long unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        String s = super.transformToString();
        if (s == null) {
            return Long.toString(this.value);
        }
        return s;
    }

    @Override
    protected long longValue() {
        return this.value;
    }

    @Override
    protected double doubleValue() {
        return this.value;
    }

    @Override
    protected ConfigLong newCopy(ConfigOrigin origin) {
        return new ConfigLong(origin, this.value, this.originalText);
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }
}

