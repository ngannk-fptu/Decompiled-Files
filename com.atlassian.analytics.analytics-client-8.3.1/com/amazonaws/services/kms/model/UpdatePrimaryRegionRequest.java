/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class UpdatePrimaryRegionRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private String primaryRegion;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public UpdatePrimaryRegionRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setPrimaryRegion(String primaryRegion) {
        this.primaryRegion = primaryRegion;
    }

    public String getPrimaryRegion() {
        return this.primaryRegion;
    }

    public UpdatePrimaryRegionRequest withPrimaryRegion(String primaryRegion) {
        this.setPrimaryRegion(primaryRegion);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getPrimaryRegion() != null) {
            sb.append("PrimaryRegion: ").append(this.getPrimaryRegion());
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
        if (!(obj instanceof UpdatePrimaryRegionRequest)) {
            return false;
        }
        UpdatePrimaryRegionRequest other = (UpdatePrimaryRegionRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getPrimaryRegion() == null ^ this.getPrimaryRegion() == null) {
            return false;
        }
        return other.getPrimaryRegion() == null || other.getPrimaryRegion().equals(this.getPrimaryRegion());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getPrimaryRegion() == null ? 0 : this.getPrimaryRegion().hashCode());
        return hashCode;
    }

    @Override
    public UpdatePrimaryRegionRequest clone() {
        return (UpdatePrimaryRegionRequest)super.clone();
    }
}

