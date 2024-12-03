/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.Tag;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ListResourceTagsResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private SdkInternalList<Tag> tags;
    private String nextMarker;
    private Boolean truncated;

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

    public ListResourceTagsResult withTags(Tag ... tags) {
        if (this.tags == null) {
            this.setTags(new SdkInternalList<Tag>(tags.length));
        }
        for (Tag ele : tags) {
            this.tags.add(ele);
        }
        return this;
    }

    public ListResourceTagsResult withTags(Collection<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    public String getNextMarker() {
        return this.nextMarker;
    }

    public ListResourceTagsResult withNextMarker(String nextMarker) {
        this.setNextMarker(nextMarker);
        return this;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public Boolean getTruncated() {
        return this.truncated;
    }

    public ListResourceTagsResult withTruncated(Boolean truncated) {
        this.setTruncated(truncated);
        return this;
    }

    public Boolean isTruncated() {
        return this.truncated;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getTags() != null) {
            sb.append("Tags: ").append(this.getTags()).append(",");
        }
        if (this.getNextMarker() != null) {
            sb.append("NextMarker: ").append(this.getNextMarker()).append(",");
        }
        if (this.getTruncated() != null) {
            sb.append("Truncated: ").append(this.getTruncated());
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
        if (!(obj instanceof ListResourceTagsResult)) {
            return false;
        }
        ListResourceTagsResult other = (ListResourceTagsResult)obj;
        if (other.getTags() == null ^ this.getTags() == null) {
            return false;
        }
        if (other.getTags() != null && !other.getTags().equals(this.getTags())) {
            return false;
        }
        if (other.getNextMarker() == null ^ this.getNextMarker() == null) {
            return false;
        }
        if (other.getNextMarker() != null && !other.getNextMarker().equals(this.getNextMarker())) {
            return false;
        }
        if (other.getTruncated() == null ^ this.getTruncated() == null) {
            return false;
        }
        return other.getTruncated() == null || other.getTruncated().equals(this.getTruncated());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getTags() == null ? 0 : this.getTags().hashCode());
        hashCode = 31 * hashCode + (this.getNextMarker() == null ? 0 : this.getNextMarker().hashCode());
        hashCode = 31 * hashCode + (this.getTruncated() == null ? 0 : this.getTruncated().hashCode());
        return hashCode;
    }

    public ListResourceTagsResult clone() {
        try {
            return (ListResourceTagsResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

