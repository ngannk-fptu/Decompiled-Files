/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.MultiRegionKey;
import com.amazonaws.services.kms.model.MultiRegionKeyType;
import com.amazonaws.services.kms.model.transform.MultiRegionConfigurationMarshaller;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class MultiRegionConfiguration
implements Serializable,
Cloneable,
StructuredPojo {
    private String multiRegionKeyType;
    private MultiRegionKey primaryKey;
    private SdkInternalList<MultiRegionKey> replicaKeys;

    public void setMultiRegionKeyType(String multiRegionKeyType) {
        this.multiRegionKeyType = multiRegionKeyType;
    }

    public String getMultiRegionKeyType() {
        return this.multiRegionKeyType;
    }

    public MultiRegionConfiguration withMultiRegionKeyType(String multiRegionKeyType) {
        this.setMultiRegionKeyType(multiRegionKeyType);
        return this;
    }

    public MultiRegionConfiguration withMultiRegionKeyType(MultiRegionKeyType multiRegionKeyType) {
        this.multiRegionKeyType = multiRegionKeyType.toString();
        return this;
    }

    public void setPrimaryKey(MultiRegionKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public MultiRegionKey getPrimaryKey() {
        return this.primaryKey;
    }

    public MultiRegionConfiguration withPrimaryKey(MultiRegionKey primaryKey) {
        this.setPrimaryKey(primaryKey);
        return this;
    }

    public List<MultiRegionKey> getReplicaKeys() {
        if (this.replicaKeys == null) {
            this.replicaKeys = new SdkInternalList();
        }
        return this.replicaKeys;
    }

    public void setReplicaKeys(Collection<MultiRegionKey> replicaKeys) {
        if (replicaKeys == null) {
            this.replicaKeys = null;
            return;
        }
        this.replicaKeys = new SdkInternalList<MultiRegionKey>(replicaKeys);
    }

    public MultiRegionConfiguration withReplicaKeys(MultiRegionKey ... replicaKeys) {
        if (this.replicaKeys == null) {
            this.setReplicaKeys(new SdkInternalList<MultiRegionKey>(replicaKeys.length));
        }
        for (MultiRegionKey ele : replicaKeys) {
            this.replicaKeys.add(ele);
        }
        return this;
    }

    public MultiRegionConfiguration withReplicaKeys(Collection<MultiRegionKey> replicaKeys) {
        this.setReplicaKeys(replicaKeys);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getMultiRegionKeyType() != null) {
            sb.append("MultiRegionKeyType: ").append(this.getMultiRegionKeyType()).append(",");
        }
        if (this.getPrimaryKey() != null) {
            sb.append("PrimaryKey: ").append(this.getPrimaryKey()).append(",");
        }
        if (this.getReplicaKeys() != null) {
            sb.append("ReplicaKeys: ").append(this.getReplicaKeys());
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MultiRegionConfiguration)) {
            return false;
        }
        MultiRegionConfiguration other = (MultiRegionConfiguration)obj;
        if (other.getMultiRegionKeyType() == null ^ this.getMultiRegionKeyType() == null) {
            return false;
        }
        if (other.getMultiRegionKeyType() != null && !other.getMultiRegionKeyType().equals(this.getMultiRegionKeyType())) {
            return false;
        }
        if (other.getPrimaryKey() == null ^ this.getPrimaryKey() == null) {
            return false;
        }
        if (other.getPrimaryKey() != null && !other.getPrimaryKey().equals(this.getPrimaryKey())) {
            return false;
        }
        if (other.getReplicaKeys() == null ^ this.getReplicaKeys() == null) {
            return false;
        }
        return other.getReplicaKeys() == null || other.getReplicaKeys().equals(this.getReplicaKeys());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getMultiRegionKeyType() == null ? 0 : this.getMultiRegionKeyType().hashCode());
        hashCode = 31 * hashCode + (this.getPrimaryKey() == null ? 0 : this.getPrimaryKey().hashCode());
        hashCode = 31 * hashCode + (this.getReplicaKeys() == null ? 0 : this.getReplicaKeys().hashCode());
        return hashCode;
    }

    public MultiRegionConfiguration clone() {
        try {
            return (MultiRegionConfiguration)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        MultiRegionConfigurationMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

