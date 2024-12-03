/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class DescribeCustomKeyStoresRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String customKeyStoreId;
    private String customKeyStoreName;
    private Integer limit;
    private String marker;

    public void setCustomKeyStoreId(String customKeyStoreId) {
        this.customKeyStoreId = customKeyStoreId;
    }

    public String getCustomKeyStoreId() {
        return this.customKeyStoreId;
    }

    public DescribeCustomKeyStoresRequest withCustomKeyStoreId(String customKeyStoreId) {
        this.setCustomKeyStoreId(customKeyStoreId);
        return this;
    }

    public void setCustomKeyStoreName(String customKeyStoreName) {
        this.customKeyStoreName = customKeyStoreName;
    }

    public String getCustomKeyStoreName() {
        return this.customKeyStoreName;
    }

    public DescribeCustomKeyStoresRequest withCustomKeyStoreName(String customKeyStoreName) {
        this.setCustomKeyStoreName(customKeyStoreName);
        return this;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public DescribeCustomKeyStoresRequest withLimit(Integer limit) {
        this.setLimit(limit);
        return this;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return this.marker;
    }

    public DescribeCustomKeyStoresRequest withMarker(String marker) {
        this.setMarker(marker);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCustomKeyStoreId() != null) {
            sb.append("CustomKeyStoreId: ").append(this.getCustomKeyStoreId()).append(",");
        }
        if (this.getCustomKeyStoreName() != null) {
            sb.append("CustomKeyStoreName: ").append(this.getCustomKeyStoreName()).append(",");
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
        if (!(obj instanceof DescribeCustomKeyStoresRequest)) {
            return false;
        }
        DescribeCustomKeyStoresRequest other = (DescribeCustomKeyStoresRequest)obj;
        if (other.getCustomKeyStoreId() == null ^ this.getCustomKeyStoreId() == null) {
            return false;
        }
        if (other.getCustomKeyStoreId() != null && !other.getCustomKeyStoreId().equals(this.getCustomKeyStoreId())) {
            return false;
        }
        if (other.getCustomKeyStoreName() == null ^ this.getCustomKeyStoreName() == null) {
            return false;
        }
        if (other.getCustomKeyStoreName() != null && !other.getCustomKeyStoreName().equals(this.getCustomKeyStoreName())) {
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
        hashCode = 31 * hashCode + (this.getCustomKeyStoreId() == null ? 0 : this.getCustomKeyStoreId().hashCode());
        hashCode = 31 * hashCode + (this.getCustomKeyStoreName() == null ? 0 : this.getCustomKeyStoreName().hashCode());
        hashCode = 31 * hashCode + (this.getLimit() == null ? 0 : this.getLimit().hashCode());
        hashCode = 31 * hashCode + (this.getMarker() == null ? 0 : this.getMarker().hashCode());
        return hashCode;
    }

    @Override
    public DescribeCustomKeyStoresRequest clone() {
        return (DescribeCustomKeyStoresRequest)super.clone();
    }
}

