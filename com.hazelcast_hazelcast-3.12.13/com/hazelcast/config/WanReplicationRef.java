/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.WanReplicationRefReadOnly;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@BinaryInterface
public class WanReplicationRef
implements DataSerializable,
Serializable {
    private boolean republishingEnabled = true;
    private String name;
    private String mergePolicy;
    private List<String> filters = new LinkedList<String>();
    private WanReplicationRefReadOnly readOnly;

    public WanReplicationRef() {
    }

    public WanReplicationRef(WanReplicationRef ref) {
        this(ref.name, ref.mergePolicy, ref.filters, ref.republishingEnabled);
        this.readOnly = ref.readOnly;
    }

    public WanReplicationRef(String name, String mergePolicy, List<String> filters, boolean republishingEnabled) {
        this.name = name;
        this.mergePolicy = mergePolicy;
        this.filters = filters;
        this.republishingEnabled = republishingEnabled;
        this.readOnly = null;
    }

    public WanReplicationRefReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new WanReplicationRefReadOnly(this);
        }
        return this.readOnly;
    }

    public String getName() {
        return this.name;
    }

    public WanReplicationRef setName(String name) {
        this.name = name;
        return this;
    }

    public String getMergePolicy() {
        return this.mergePolicy;
    }

    public WanReplicationRef setMergePolicy(String mergePolicy) {
        this.mergePolicy = mergePolicy;
        return this;
    }

    public WanReplicationRef addFilter(String filter) {
        this.filters.add(filter);
        return this;
    }

    public List<String> getFilters() {
        return this.filters;
    }

    public WanReplicationRef setFilters(List<String> filters) {
        this.filters = filters;
        return this;
    }

    public boolean isRepublishingEnabled() {
        return this.republishingEnabled;
    }

    public WanReplicationRef setRepublishingEnabled(boolean republishEnabled) {
        this.republishingEnabled = republishEnabled;
        return this;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.mergePolicy);
        out.writeInt(this.filters.size());
        for (String filter : this.filters) {
            out.writeUTF(filter);
        }
        out.writeBoolean(this.republishingEnabled);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.mergePolicy = in.readUTF();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.filters.add(in.readUTF());
        }
        this.republishingEnabled = in.readBoolean();
    }

    public String toString() {
        return "WanReplicationRef{name='" + this.name + '\'' + ", mergePolicy='" + this.mergePolicy + '\'' + ", filters='" + this.filters + '\'' + ", republishingEnabled='" + this.republishingEnabled + '\'' + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WanReplicationRef that = (WanReplicationRef)o;
        if (this.republishingEnabled != that.republishingEnabled) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.mergePolicy != null ? !this.mergePolicy.equals(that.mergePolicy) : that.mergePolicy != null) {
            return false;
        }
        return this.filters != null ? this.filters.equals(that.filters) : that.filters == null;
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.mergePolicy != null ? this.mergePolicy.hashCode() : 0);
        result = 31 * result + (this.filters != null ? this.filters.hashCode() : 0);
        result = 31 * result + (this.republishingEnabled ? 1 : 0);
        return result;
    }
}

