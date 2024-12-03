/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CollectionConfig;
import com.hazelcast.config.ListConfigReadOnly;

public class ListConfig
extends CollectionConfig<ListConfig> {
    private transient ListConfigReadOnly readOnly;

    public ListConfig() {
    }

    public ListConfig(String name) {
        this.setName(name);
    }

    public ListConfig(ListConfig config) {
        super(config);
    }

    @Override
    public ListConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new ListConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public int getId() {
        return 28;
    }

    public String toString() {
        return "ListConfig{" + super.fieldsToString() + "}";
    }
}

