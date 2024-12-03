/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.Tag;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ReplicateKeyRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private String replicaRegion;
    private String policy;
    private Boolean bypassPolicyLockoutSafetyCheck;
    private String description;
    private SdkInternalList<Tag> tags;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public ReplicateKeyRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setReplicaRegion(String replicaRegion) {
        this.replicaRegion = replicaRegion;
    }

    public String getReplicaRegion() {
        return this.replicaRegion;
    }

    public ReplicateKeyRequest withReplicaRegion(String replicaRegion) {
        this.setReplicaRegion(replicaRegion);
        return this;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getPolicy() {
        return this.policy;
    }

    public ReplicateKeyRequest withPolicy(String policy) {
        this.setPolicy(policy);
        return this;
    }

    public void setBypassPolicyLockoutSafetyCheck(Boolean bypassPolicyLockoutSafetyCheck) {
        this.bypassPolicyLockoutSafetyCheck = bypassPolicyLockoutSafetyCheck;
    }

    public Boolean getBypassPolicyLockoutSafetyCheck() {
        return this.bypassPolicyLockoutSafetyCheck;
    }

    public ReplicateKeyRequest withBypassPolicyLockoutSafetyCheck(Boolean bypassPolicyLockoutSafetyCheck) {
        this.setBypassPolicyLockoutSafetyCheck(bypassPolicyLockoutSafetyCheck);
        return this;
    }

    public Boolean isBypassPolicyLockoutSafetyCheck() {
        return this.bypassPolicyLockoutSafetyCheck;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public ReplicateKeyRequest withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public List<Tag> getTags() {
        if (this.tags == null) {
            this.tags = new SdkInternalList();
        }
        return this.tags;
    }

    public void setTags(Collection<Tag> tags) {
        if (tags == null) {
            this.tags = null;
            return;
        }
        this.tags = new SdkInternalList<Tag>(tags);
    }

    public ReplicateKeyRequest withTags(Tag ... tags) {
        if (this.tags == null) {
            this.setTags(new SdkInternalList<Tag>(tags.length));
        }
        for (Tag ele : tags) {
            this.tags.add(ele);
        }
        return this;
    }

    public ReplicateKeyRequest withTags(Collection<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getReplicaRegion() != null) {
            sb.append("ReplicaRegion: ").append(this.getReplicaRegion()).append(",");
        }
        if (this.getPolicy() != null) {
            sb.append("Policy: ").append(this.getPolicy()).append(",");
        }
        if (this.getBypassPolicyLockoutSafetyCheck() != null) {
            sb.append("BypassPolicyLockoutSafetyCheck: ").append(this.getBypassPolicyLockoutSafetyCheck()).append(",");
        }
        if (this.getDescription() != null) {
            sb.append("Description: ").append(this.getDescription()).append(",");
        }
        if (this.getTags() != null) {
            sb.append("Tags: ").append(this.getTags());
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
        if (!(obj instanceof ReplicateKeyRequest)) {
            return false;
        }
        ReplicateKeyRequest other = (ReplicateKeyRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getReplicaRegion() == null ^ this.getReplicaRegion() == null) {
            return false;
        }
        if (other.getReplicaRegion() != null && !other.getReplicaRegion().equals(this.getReplicaRegion())) {
            return false;
        }
        if (other.getPolicy() == null ^ this.getPolicy() == null) {
            return false;
        }
        if (other.getPolicy() != null && !other.getPolicy().equals(this.getPolicy())) {
            return false;
        }
        if (other.getBypassPolicyLockoutSafetyCheck() == null ^ this.getBypassPolicyLockoutSafetyCheck() == null) {
            return false;
        }
        if (other.getBypassPolicyLockoutSafetyCheck() != null && !other.getBypassPolicyLockoutSafetyCheck().equals(this.getBypassPolicyLockoutSafetyCheck())) {
            return false;
        }
        if (other.getDescription() == null ^ this.getDescription() == null) {
            return false;
        }
        if (other.getDescription() != null && !other.getDescription().equals(this.getDescription())) {
            return false;
        }
        if (other.getTags() == null ^ this.getTags() == null) {
            return false;
        }
        return other.getTags() == null || other.getTags().equals(this.getTags());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getReplicaRegion() == null ? 0 : this.getReplicaRegion().hashCode());
        hashCode = 31 * hashCode + (this.getPolicy() == null ? 0 : this.getPolicy().hashCode());
        hashCode = 31 * hashCode + (this.getBypassPolicyLockoutSafetyCheck() == null ? 0 : this.getBypassPolicyLockoutSafetyCheck().hashCode());
        hashCode = 31 * hashCode + (this.getDescription() == null ? 0 : this.getDescription().hashCode());
        hashCode = 31 * hashCode + (this.getTags() == null ? 0 : this.getTags().hashCode());
        return hashCode;
    }

    @Override
    public ReplicateKeyRequest clone() {
        return (ReplicateKeyRequest)super.clone();
    }
}

