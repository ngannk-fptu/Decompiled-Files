/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class ListGrantsRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private Integer limit;
    private String marker;
    private String keyId;
    private String grantId;
    private String granteePrincipal;

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public ListGrantsRequest withLimit(Integer limit) {
        this.setLimit(limit);
        return this;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return this.marker;
    }

    public ListGrantsRequest withMarker(String marker) {
        this.setMarker(marker);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public ListGrantsRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setGrantId(String grantId) {
        this.grantId = grantId;
    }

    public String getGrantId() {
        return this.grantId;
    }

    public ListGrantsRequest withGrantId(String grantId) {
        this.setGrantId(grantId);
        return this;
    }

    public void setGranteePrincipal(String granteePrincipal) {
        this.granteePrincipal = granteePrincipal;
    }

    public String getGranteePrincipal() {
        return this.granteePrincipal;
    }

    public ListGrantsRequest withGranteePrincipal(String granteePrincipal) {
        this.setGranteePrincipal(granteePrincipal);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getLimit() != null) {
            sb.append("Limit: ").append(this.getLimit()).append(",");
        }
        if (this.getMarker() != null) {
            sb.append("Marker: ").append(this.getMarker()).append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getGrantId() != null) {
            sb.append("GrantId: ").append(this.getGrantId()).append(",");
        }
        if (this.getGranteePrincipal() != null) {
            sb.append("GranteePrincipal: ").append(this.getGranteePrincipal());
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
        if (!(obj instanceof ListGrantsRequest)) {
            return false;
        }
        ListGrantsRequest other = (ListGrantsRequest)obj;
        if (other.getLimit() == null ^ this.getLimit() == null) {
            return false;
        }
        if (other.getLimit() != null && !other.getLimit().equals(this.getLimit())) {
            return false;
        }
        if (other.getMarker() == null ^ this.getMarker() == null) {
            return false;
        }
        if (other.getMarker() != null && !other.getMarker().equals(this.getMarker())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getGrantId() == null ^ this.getGrantId() == null) {
            return false;
        }
        if (other.getGrantId() != null && !other.getGrantId().equals(this.getGrantId())) {
            return false;
        }
        if (other.getGranteePrincipal() == null ^ this.getGranteePrincipal() == null) {
            return false;
        }
        return other.getGranteePrincipal() == null || other.getGranteePrincipal().equals(this.getGranteePrincipal());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getLimit() == null ? 0 : this.getLimit().hashCode());
        hashCode = 31 * hashCode + (this.getMarker() == null ? 0 : this.getMarker().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getGrantId() == null ? 0 : this.getGrantId().hashCode());
        hashCode = 31 * hashCode + (this.getGranteePrincipal() == null ? 0 : this.getGranteePrincipal().hashCode());
        return hashCode;
    }

    @Override
    public ListGrantsRequest clone() {
        return (ListGrantsRequest)super.clone();
    }
}

