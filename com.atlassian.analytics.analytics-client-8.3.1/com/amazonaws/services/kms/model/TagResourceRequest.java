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

public class TagResourceRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private SdkInternalList<Tag> tags;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public TagResourceRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
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

    public TagResourceRequest withTags(Tag ... tags) {
        if (this.tags == null) {
            this.setTags(new SdkInternalList<Tag>(tags.length));
        }
        for (Tag ele : tags) {
            this.tags.add(ele);
        }
        return this;
    }

    public TagResourceRequest withTags(Collection<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
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
        if (!(obj instanceof TagResourceRequest)) {
            return false;
        }
        TagResourceRequest other = (TagResourceRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
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
        hashCode = 31 * hashCode + (this.getTags() == null ? 0 : this.getTags().hashCode());
        return hashCode;
    }

    @Override
    public TagResourceRequest clone() {
        return (TagResourceRequest)super.clone();
    }
}

