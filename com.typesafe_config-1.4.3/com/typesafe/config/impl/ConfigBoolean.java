/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.SerializedConfigValue;
import java.io.ObjectStreamException;
import java.io.Serializable;

final class ConfigBoolean
extends AbstractConfigValue
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final boolean value;

    ConfigBoolean(ConfigOrigin origin, boolean value) {
        super(origin);
        this.value = value;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.BOOLEAN;
    }

    @Override
    public Boolean unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        return this.value ? "true" : "false";
    }

    @Override
    protected ConfigBoolean newCopy(ConfigOrigin origin) {
        return new ConfigBoolean(origin, this.value);
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }
}

