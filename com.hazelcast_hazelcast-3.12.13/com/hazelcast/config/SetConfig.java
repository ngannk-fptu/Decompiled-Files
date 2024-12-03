/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CollectionConfig;
import com.hazelcast.config.SetConfigReadOnly;

public class SetConfig
extends CollectionConfig<SetConfig> {
    private transient SetConfigReadOnly readOnly;

    public SetConfig() {
    }

    public SetConfig(String name) {
        this.setName(name);
    }

    public SetConfig(SetConfig config) {
        super(config);
    }

    @Override
    public SetConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new SetConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public int getId() {
        return 29;
    }

    public String toString() {
        return "SetConfig{" + super.fieldsToString() + "}";
    }
}

