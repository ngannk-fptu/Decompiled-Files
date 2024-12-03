/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ListenerConfigReadOnly;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.EventListener;

public class ListenerConfig
implements IdentifiedDataSerializable {
    protected String className;
    protected EventListener implementation;
    private ListenerConfigReadOnly readOnly;

    public ListenerConfig() {
    }

    public ListenerConfig(String className) {
        this.setClassName(className);
    }

    public ListenerConfig(ListenerConfig config) {
        this.implementation = config.getImplementation();
        this.className = config.getClassName();
    }

    public ListenerConfig(EventListener implementation) {
        this.implementation = Preconditions.isNotNull(implementation, "implementation");
    }

    public ListenerConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new ListenerConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getClassName() {
        return this.className;
    }

    public ListenerConfig setClassName(String className) {
        this.className = Preconditions.checkHasText(className, "className must contain text");
        this.implementation = null;
        return this;
    }

    public EventListener getImplementation() {
        return this.implementation;
    }

    public ListenerConfig setImplementation(EventListener implementation) {
        this.implementation = Preconditions.isNotNull(implementation, "implementation");
        this.className = null;
        return this;
    }

    public boolean isIncludeValue() {
        return true;
    }

    public boolean isLocal() {
        return false;
    }

    public String toString() {
        return "ListenerConfig [className=" + this.className + ", implementation=" + this.implementation + ", includeValue=" + this.isIncludeValue() + ", local=" + this.isLocal() + "]";
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 8;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.className);
        out.writeObject(this.implementation);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.className = in.readUTF();
        this.implementation = (EventListener)in.readObject();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ListenerConfig that = (ListenerConfig)o;
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.implementation != null ? !this.implementation.equals(that.implementation) : that.implementation != null) {
            return false;
        }
        return this.readOnly != null ? this.readOnly.equals(that.readOnly) : that.readOnly == null;
    }

    public int hashCode() {
        int result = this.className != null ? this.className.hashCode() : 0;
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        result = 31 * result + (this.readOnly != null ? this.readOnly.hashCode() : 0);
        return result;
    }
}

