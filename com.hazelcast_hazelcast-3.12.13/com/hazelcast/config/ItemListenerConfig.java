/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ItemListenerConfigReadOnly;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.ItemListener;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class ItemListenerConfig
extends ListenerConfig {
    private boolean includeValue = true;
    private transient ItemListenerConfigReadOnly readOnly;

    public ItemListenerConfig() {
    }

    public ItemListenerConfig(String className, boolean includeValue) {
        super(className);
        this.includeValue = includeValue;
    }

    public ItemListenerConfig(ItemListener implementation, boolean includeValue) {
        super(implementation);
        this.includeValue = includeValue;
    }

    public ItemListenerConfig(ItemListenerConfig config) {
        this.includeValue = config.isIncludeValue();
        this.implementation = config.getImplementation();
        this.className = config.getClassName();
    }

    @Override
    public ItemListenerConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new ItemListenerConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public ItemListener getImplementation() {
        return (ItemListener)this.implementation;
    }

    public ItemListenerConfig setImplementation(ItemListener implementation) {
        super.setImplementation(implementation);
        return this;
    }

    @Override
    public boolean isIncludeValue() {
        return this.includeValue;
    }

    public ItemListenerConfig setIncludeValue(boolean includeValue) {
        this.includeValue = includeValue;
        return this;
    }

    @Override
    public String toString() {
        return "ItemListenerConfig{includeValue=" + this.includeValue + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ItemListenerConfig that = (ItemListenerConfig)o;
        return this.includeValue == that.includeValue;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.includeValue ? 1 : 0);
        return result;
    }

    @Override
    public int getId() {
        return 24;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeBoolean(this.includeValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.includeValue = in.readBoolean();
    }
}

