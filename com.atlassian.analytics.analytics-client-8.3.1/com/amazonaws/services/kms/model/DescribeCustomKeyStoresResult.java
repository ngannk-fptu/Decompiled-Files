/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.CustomKeyStoresListEntry;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class DescribeCustomKeyStoresResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private SdkInternalList<CustomKeyStoresListEntry> customKeyStores;
    private String nextMarker;
    private Boolean truncated;

    public List<CustomKeyStoresListEntry> getCustomKeyStores() {
        if (this.customKeyStores == null) {
            this.customKeyStores = new SdkInternalList();
        }
        return this.customKeyStores;
    }

    public void setCustomKeyStores(Collection<CustomKeyStoresListEntry> customKeyStores) {
        if (customKeyStores == null) {
            this.customKeyStores = null;
            return;
        }
        this.customKeyStores = new SdkInternalList<CustomKeyStoresListEntry>(customKeyStores);
    }

    public DescribeCustomKeyStoresResult withCustomKeyStores(CustomKeyStoresListEntry ... customKeyStores) {
        if (this.customKeyStores == null) {
            this.setCustomKeyStores(new SdkInternalList<CustomKeyStoresListEntry>(customKeyStores.length));
        }
        for (CustomKeyStoresListEntry ele : customKeyStores) {
            this.customKeyStores.add(ele);
        }
        return this;
    }

    public DescribeCustomKeyStoresResult withCustomKeyStores(Collection<CustomKeyStoresListEntry> customKeyStores) {
        this.setCustomKeyStores(customKeyStores);
        return this;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    public String getNextMarker() {
        return this.nextMarker;
    }

    public DescribeCustomKeyStoresResult withNextMarker(String nextMarker) {
        this.setNextMarker(nextMarker);
        return this;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public Boolean getTruncated() {
        return this.truncated;
    }

    public DescribeCustomKeyStoresResult withTruncated(Boolean truncated) {
        this.setTruncated(truncated);
        return this;
    }

    public Boolean isTruncated() {
        return this.truncated;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCustomKeyStores() != null) {
            sb.append("CustomKeyStores: ").append(this.getCustomKeyStores()).append(",");
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
        if (!(obj instanceof DescribeCustomKeyStoresResult)) {
            return false;
        }
        DescribeCustomKeyStoresResult other = (DescribeCustomKeyStoresResult)obj;
        if (other.getCustomKeyStores() == null ^ this.getCustomKeyStores() == null) {
            return false;
        }
        if (other.getCustomKeyStores() != null && !other.getCustomKeyStores().equals(this.getCustomKeyStores())) {
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
        hashCode = 31 * hashCode + (this.getCustomKeyStores() == null ? 0 : this.getCustomKeyStores().hashCode());
        hashCode = 31 * hashCode + (this.getNextMarker() == null ? 0 : this.getNextMarker().hashCode());
        hashCode = 31 * hashCode + (this.getTruncated() == null ? 0 : this.getTruncated().hashCode());
        return hashCode;
    }

    public DescribeCustomKeyStoresResult clone() {
        try {
            return (DescribeCustomKeyStoresResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

