/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class UntagResourceRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private SdkInternalList<String> tagKeys;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public UntagResourceRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public List<String> getTagKeys() {
        if (this.tagKeys == null) {
            this.tagKeys = new SdkInternalList();
        }
        return this.tagKeys;
    }

    public void setTagKeys(Collection<String> tagKeys) {
        if (tagKeys == null) {
            this.tagKeys = null;
            return;
        }
        this.tagKeys = new SdkInternalList<String>(tagKeys);
    }

    public UntagResourceRequest withTagKeys(String ... tagKeys) {
        if (this.tagKeys == null) {
            this.setTagKeys(new SdkInternalList<String>(tagKeys.length));
        }
        for (String ele : tagKeys) {
            this.tagKeys.add(ele);
        }
        return this;
    }

    public UntagResourceRequest withTagKeys(Collection<String> tagKeys) {
        this.setTagKeys(tagKeys);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getTagKeys() != null) {
            sb.append("TagKeys: ").append(this.getTagKeys());
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
        if (!(obj instanceof UntagResourceRequest)) {
            return false;
        }
        UntagResourceRequest other = (UntagResourceRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getTagKeys() == null ^ this.getTagKeys() == null) {
            return false;
        }
        return other.getTagKeys() == null || other.getTagKeys().equals(this.getTagKeys());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getTagKeys() == null ? 0 : this.getTagKeys().hashCode());
        return hashCode;
    }

    @Override
    public UntagResourceRequest clone() {
        return (UntagResourceRequest)super.clone();
    }
}

