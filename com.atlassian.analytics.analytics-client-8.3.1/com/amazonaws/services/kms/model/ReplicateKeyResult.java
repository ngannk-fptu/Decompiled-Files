/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.KeyMetadata;
import com.amazonaws.services.kms.model.Tag;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ReplicateKeyResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private KeyMetadata replicaKeyMetadata;
    private String replicaPolicy;
    private SdkInternalList<Tag> replicaTags;

    public void setReplicaKeyMetadata(KeyMetadata replicaKeyMetadata) {
        this.replicaKeyMetadata = replicaKeyMetadata;
    }

    public KeyMetadata getReplicaKeyMetadata() {
        return this.replicaKeyMetadata;
    }

    public ReplicateKeyResult withReplicaKeyMetadata(KeyMetadata replicaKeyMetadata) {
        this.setReplicaKeyMetadata(replicaKeyMetadata);
        return this;
    }

    public void setReplicaPolicy(String replicaPolicy) {
        this.replicaPolicy = replicaPolicy;
    }

    public String getReplicaPolicy() {
        return this.replicaPolicy;
    }

    public ReplicateKeyResult withReplicaPolicy(String replicaPolicy) {
        this.setReplicaPolicy(replicaPolicy);
        return this;
    }

    public List<Tag> getReplicaTags() {
        if (this.replicaTags == null) {
            this.replicaTags = new SdkInternalList();
        }
        return this.replicaTags;
    }

    public void setReplicaTags(Collection<Tag> replicaTags) {
        if (replicaTags == null) {
            this.replicaTags = null;
            return;
        }
        this.replicaTags = new SdkInternalList<Tag>(replicaTags);
    }

    public ReplicateKeyResult withReplicaTags(Tag ... replicaTags) {
        if (this.replicaTags == null) {
            this.setReplicaTags(new SdkInternalList<Tag>(replicaTags.length));
        }
        for (Tag ele : replicaTags) {
            this.replicaTags.add(ele);
        }
        return this;
    }

    public ReplicateKeyResult withReplicaTags(Collection<Tag> replicaTags) {
        this.setReplicaTags(replicaTags);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getReplicaKeyMetadata() != null) {
            sb.append("ReplicaKeyMetadata: ").append(this.getReplicaKeyMetadata()).append(",");
        }
        if (this.getReplicaPolicy() != null) {
            sb.append("ReplicaPolicy: ").append(this.getReplicaPolicy()).append(",");
        }
        if (this.getReplicaTags() != null) {
            sb.append("ReplicaTags: ").append(this.getReplicaTags());
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
        if (!(obj instanceof ReplicateKeyResult)) {
            return false;
        }
        ReplicateKeyResult other = (ReplicateKeyResult)obj;
        if (other.getReplicaKeyMetadata() == null ^ this.getReplicaKeyMetadata() == null) {
            return false;
        }
        if (other.getReplicaKeyMetadata() != null && !other.getReplicaKeyMetadata().equals(this.getReplicaKeyMetadata())) {
            return false;
        }
        if (other.getReplicaPolicy() == null ^ this.getReplicaPolicy() == null) {
            return false;
        }
        if (other.getReplicaPolicy() != null && !other.getReplicaPolicy().equals(this.getReplicaPolicy())) {
            return false;
        }
        if (other.getReplicaTags() == null ^ this.getReplicaTags() == null) {
            return false;
        }
        return other.getReplicaTags() == null || other.getReplicaTags().equals(this.getReplicaTags());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getReplicaKeyMetadata() == null ? 0 : this.getReplicaKeyMetadata().hashCode());
        hashCode = 31 * hashCode + (this.getReplicaPolicy() == null ? 0 : this.getReplicaPolicy().hashCode());
        hashCode = 31 * hashCode + (this.getReplicaTags() == null ? 0 : this.getReplicaTags().hashCode());
        return hashCode;
    }

    public ReplicateKeyResult clone() {
        try {
            return (ReplicateKeyResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

