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

final class ConfigDouble
extends ConfigNumber
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final double value;

    ConfigDouble(ConfigOrigin origin, double value, String originalText) {
        super(origin, originalText);
        this.value = value;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.NUMBER;
    }

    @Override
    public Double unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        String s = super.transformToString();
        if (s == null) {
            return Double.toString(this.value);
        }
        return s;
    }

    @Override
    protected long longValue() {
        return (long)this.value;
    }

    @Override
    protected double doubleValue() {
        return this.value;
    }

    @Override
    protected ConfigDouble newCopy(ConfigOrigin origin) {
        return new ConfigDouble(origin, this.value, this.originalText);
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }
}

