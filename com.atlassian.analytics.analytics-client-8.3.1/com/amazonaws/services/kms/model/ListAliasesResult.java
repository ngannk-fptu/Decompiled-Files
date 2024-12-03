/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.AliasListEntry;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ListAliasesResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private SdkInternalList<AliasListEntry> aliases;
    private String nextMarker;
    private Boolean truncated;

    public List<AliasListEntry> getAliases() {
        if (this.aliases == null) {
            this.aliases = new SdkInternalList();
        }
        return this.aliases;
    }

    public void setAliases(Collection<AliasListEntry> aliases) {
        if (aliases == null) {
            this.aliases = null;
            return;
        }
        this.aliases = new SdkInternalList<AliasListEntry>(aliases);
    }

    public ListAliasesResult withAliases(AliasListEntry ... aliases) {
        if (this.aliases == null) {
            this.setAliases(new SdkInternalList<AliasListEntry>(aliases.length));
        }
        for (AliasListEntry ele : aliases) {
            this.aliases.add(ele);
        }
        return this;
    }

    public ListAliasesResult withAliases(Collection<AliasListEntry> aliases) {
        this.setAliases(aliases);
        return this;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    public String getNextMarker() {
        return this.nextMarker;
    }

    public ListAliasesResult withNextMarker(String nextMarker) {
        this.setNextMarker(nextMarker);
        return this;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public Boolean getTruncated() {
        return this.truncated;
    }

    public ListAliasesResult withTruncated(Boolean truncated) {
        this.setTruncated(truncated);
        return this;
    }

    public Boolean isTruncated() {
        return this.truncated;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getAliases() != null) {
            sb.append("Aliases: ").append(this.getAliases()).append(",");
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
        if (!(obj instanceof ListAliasesResult)) {
            return false;
        }
        ListAliasesResult other = (ListAliasesResult)obj;
        if (other.getAliases() == null ^ this.getAliases() == null) {
            return false;
        }
        if (other.getAliases() != null && !other.getAliases().equals(this.getAliases())) {
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
        hashCode = 31 * hashCode + (this.getAliases() == null ? 0 : this.getAliases().hashCode());
        hashCode = 31 * hashCode + (this.getNextMarker() == null ? 0 : this.getNextMarker().hashCode());
        hashCode = 31 * hashCode + (this.getTruncated() == null ? 0 : this.getTruncated().hashCode());
        return hashCode;
    }

    public ListAliasesResult clone() {
        try {
            return (ListAliasesResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

