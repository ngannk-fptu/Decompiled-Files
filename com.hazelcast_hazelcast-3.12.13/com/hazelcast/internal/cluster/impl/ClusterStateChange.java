/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.version.Version;
import java.io.IOException;

public class ClusterStateChange<T>
implements IdentifiedDataSerializable {
    private Class<T> type;
    private T newState;

    public ClusterStateChange() {
    }

    public ClusterStateChange(T newState) {
        this.type = newState.getClass();
        this.newState = newState;
    }

    public Class<T> getType() {
        return this.type;
    }

    public T getNewState() {
        return this.newState;
    }

    public <T_SUGGESTED> boolean isOfType(Class<T_SUGGESTED> type) {
        return this.type.equals(type);
    }

    public ClusterState getClusterStateOrNull() {
        return this.isOfType(ClusterState.class) ? (ClusterState)((Object)this.newState) : null;
    }

    public void validate() {
        if (this.type == null || this.newState == null) {
            throw new IllegalArgumentException("Invalid null state");
        }
        if (this.isOfType(Version.class) && ((Version)this.newState).isUnknown()) {
            throw new IllegalArgumentException("Cannot change Version to UNKNOWN!");
        }
        if (this.isOfType(ClusterState.class) && this.newState == ClusterState.IN_TRANSITION) {
            throw new IllegalArgumentException("IN_TRANSITION is an internal state!");
        }
    }

    public static <T> ClusterStateChange<T> from(T object) {
        return new ClusterStateChange<T>(object);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.type);
        out.writeObject(this.newState);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.type = (Class)in.readObject();
        this.newState = in.readObject();
    }

    public String toString() {
        return "ClusterStateChange{type=" + this.type + ", newState=" + this.newState + '}';
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 33;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClusterStateChange that = (ClusterStateChange)o;
        if (!this.type.equals(that.type)) {
            return false;
        }
        return this.newState.equals(that.newState);
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.newState.hashCode();
        return result;
    }
}

