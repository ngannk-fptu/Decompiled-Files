/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class ListResourceTagsRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private Integer limit;
    private String marker;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public ListResourceTagsRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public ListResourceTagsRequest withLimit(Integer limit) {
        this.setLimit(limit);
        return this;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return this.marker;
    }

    public ListResourceTagsRequest withMarker(String marker) {
        this.setMarker(marker);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getLimit() != null) {
            sb.append("Limit: ").append(this.getLimit()).append(",");
        }
        if (this.getMarker() != null) {
            sb.append("Marker: ").append(this.getMarker());
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
        if (!(obj instanceof ListResourceTagsRequest)) {
            return false;
        }
        ListResourceTagsRequest other = (ListResourceTagsRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getLimit() == null ^ this.getLimit() == null) {
            return false;
        }
        if (other.getLimit() != null && !other.getLimit().equals(this.getLimit())) {
            return false;
        }
        if (other.getMarker() == null ^ this.getMarker() == null) {
            return false;
        }
        return other.getMarker() == null || other.getMarker().equals(this.getMarker());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getLimit() == null ? 0 : this.getLimit().hashCode());
        hashCode = 31 * hashCode + (this.getMarker() == null ? 0 : this.getMarker().hashCode());
        return hashCode;
    }

    @Override
    public ListResourceTagsRequest clone() {
        return (ListResourceTagsRequest)super.clone();
    }
}

