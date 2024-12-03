/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class RevokeGrantRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private String grantId;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public RevokeGrantRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setGrantId(String grantId) {
        this.grantId = grantId;
    }

    public String getGrantId() {
        return this.grantId;
    }

    public RevokeGrantRequest withGrantId(String grantId) {
        this.setGrantId(grantId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getGrantId() != null) {
            sb.append("GrantId: ").append(this.getGrantId());
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
        if (!(obj instanceof RevokeGrantRequest)) {
            return false;
        }
        RevokeGrantRequest other = (RevokeGrantRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getGrantId() == null ^ this.getGrantId() == null) {
            return false;
        }
        return other.getGrantId() == null || other.getGrantId().equals(this.getGrantId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getGrantId() == null ? 0 : this.getGrantId().hashCode());
        return hashCode;
    }

    @Override
    public RevokeGrantRequest clone() {
        return (RevokeGrantRequest)super.clone();
    }
}

