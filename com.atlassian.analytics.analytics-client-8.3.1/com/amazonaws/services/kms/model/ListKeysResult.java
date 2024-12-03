/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.KeyListEntry;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ListKeysResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private SdkInternalList<KeyListEntry> keys;
    private String nextMarker;
    private Boolean truncated;

    public List<KeyListEntry> getKeys() {
        if (this.keys == null) {
            this.keys = new SdkInternalList();
        }
        return this.keys;
    }

    public void setKeys(Collection<KeyListEntry> keys) {
        if (keys == null) {
            this.keys = null;
            return;
        }
        this.keys = new SdkInternalList<KeyListEntry>(keys);
    }

    public ListKeysResult withKeys(KeyListEntry ... keys) {
        if (this.keys == null) {
            this.setKeys(new SdkInternalList<KeyListEntry>(keys.length));
        }
        for (KeyListEntry ele : keys) {
            this.keys.add(ele);
        }
        return this;
    }

    public ListKeysResult withKeys(Collection<KeyListEntry> keys) {
        this.setKeys(keys);
        return this;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    public String getNextMarker() {
        return this.nextMarker;
    }

    public ListKeysResult withNextMarker(String nextMarker) {
        this.setNextMarker(nextMarker);
        return this;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public Boolean getTruncated() {
        return this.truncated;
    }

    public ListKeysResult withTruncated(Boolean truncated) {
        this.setTruncated(truncated);
        return this;
    }

    public Boolean isTruncated() {
        return this.truncated;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeys() != null) {
            sb.append("Keys: ").append(this.getKeys()).append(",");
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
        if (!(obj instanceof ListKeysResult)) {
            return false;
        }
        ListKeysResult other = (ListKeysResult)obj;
        if (other.getKeys() == null ^ this.getKeys() == null) {
            return false;
        }
        if (other.getKeys() != null && !other.getKeys().equals(this.getKeys())) {
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
        hashCode = 31 * hashCode + (this.getKeys() == null ? 0 : this.getKeys().hashCode());
        hashCode = 31 * hashCode + (this.getNextMarker() == null ? 0 : this.getNextMarker().hashCode());
        hashCode = 31 * hashCode + (this.getTruncated() == null ? 0 : this.getTruncated().hashCode());
        return hashCode;
    }

    public ListKeysResult clone() {
        try {
            return (ListKeysResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

