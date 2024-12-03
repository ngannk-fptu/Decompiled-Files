/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.GrantListEntry;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ListRetirableGrantsResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private SdkInternalList<GrantListEntry> grants;
    private String nextMarker;
    private Boolean truncated;

    public List<GrantListEntry> getGrants() {
        if (this.grants == null) {
            this.grants = new SdkInternalList();
        }
        return this.grants;
    }

    public void setGrants(Collection<GrantListEntry> grants) {
        if (grants == null) {
            this.grants = null;
            return;
        }
        this.grants = new SdkInternalList<GrantListEntry>(grants);
    }

    public ListRetirableGrantsResult withGrants(GrantListEntry ... grants) {
        if (this.grants == null) {
            this.setGrants(new SdkInternalList<GrantListEntry>(grants.length));
        }
        for (GrantListEntry ele : grants) {
            this.grants.add(ele);
        }
        return this;
    }

    public ListRetirableGrantsResult withGrants(Collection<GrantListEntry> grants) {
        this.setGrants(grants);
        return this;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    public String getNextMarker() {
        return this.nextMarker;
    }

    public ListRetirableGrantsResult withNextMarker(String nextMarker) {
        this.setNextMarker(nextMarker);
        return this;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public Boolean getTruncated() {
        return this.truncated;
    }

    public ListRetirableGrantsResult withTruncated(Boolean truncated) {
        this.setTruncated(truncated);
        return this;
    }

    public Boolean isTruncated() {
        return this.truncated;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getGrants() != null) {
            sb.append("Grants: ").append(this.getGrants()).append(",");
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
        if (!(obj instanceof ListRetirableGrantsResult)) {
            return false;
        }
        ListRetirableGrantsResult other = (ListRetirableGrantsResult)obj;
        if (other.getGrants() == null ^ this.getGrants() == null) {
            return false;
        }
        if (other.getGrants() != null && !other.getGrants().equals(this.getGrants())) {
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
        hashCode = 31 * hashCode + (this.getGrants() == null ? 0 : this.getGrants().hashCode());
        hashCode = 31 * hashCode + (this.getNextMarker() == null ? 0 : this.getNextMarker().hashCode());
        hashCode = 31 * hashCode + (this.getTruncated() == null ? 0 : this.getTruncated().hashCode());
        return hashCode;
    }

    public ListRetirableGrantsResult clone() {
        try {
            return (ListRetirableGrantsResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

