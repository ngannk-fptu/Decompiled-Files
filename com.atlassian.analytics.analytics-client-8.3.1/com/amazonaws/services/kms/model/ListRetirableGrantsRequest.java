/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class ListRetirableGrantsRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private Integer limit;
    private String marker;
    private String retiringPrincipal;

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public ListRetirableGrantsRequest withLimit(Integer limit) {
        this.setLimit(limit);
        return this;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return this.marker;
    }

    public ListRetirableGrantsRequest withMarker(String marker) {
        this.setMarker(marker);
        return this;
    }

    public void setRetiringPrincipal(String retiringPrincipal) {
        this.retiringPrincipal = retiringPrincipal;
    }

    public String getRetiringPrincipal() {
        return this.retiringPrincipal;
    }

    public ListRetirableGrantsRequest withRetiringPrincipal(String retiringPrincipal) {
        this.setRetiringPrincipal(retiringPrincipal);
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
        if (this.getRetiringPrincipal() != null) {
            sb.append("RetiringPrincipal: ").append(this.getRetiringPrincipal());
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
        if (!(obj instanceof ListRetirableGrantsRequest)) {
            return false;
        }
        ListRetirableGrantsRequest other = (ListRetirableGrantsRequest)obj;
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
        if (other.getRetiringPrincipal() == null ^ this.getRetiringPrincipal() == null) {
            return false;
        }
        return other.getRetiringPrincipal() == null || other.getRetiringPrincipal().equals(this.getRetiringPrincipal());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getLimit() == null ? 0 : this.getLimit().hashCode());
        hashCode = 31 * hashCode + (this.getMarker() == null ? 0 : this.getMarker().hashCode());
        hashCode = 31 * hashCode + (this.getRetiringPrincipal() == null ? 0 : this.getRetiringPrincipal().hashCode());
        return hashCode;
    }

    @Override
    public ListRetirableGrantsRequest clone() {
        return (ListRetirableGrantsRequest)super.clone();
    }
}

