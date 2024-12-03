/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class RetireGrantRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String grantToken;
    private String keyId;
    private String grantId;

    public void setGrantToken(String grantToken) {
        this.grantToken = grantToken;
    }

    public String getGrantToken() {
        return this.grantToken;
    }

    public RetireGrantRequest withGrantToken(String grantToken) {
        this.setGrantToken(grantToken);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public RetireGrantRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setGrantId(String grantId) {
        this.grantId = grantId;
    }

    public String getGrantId() {
        return this.grantId;
    }

    public RetireGrantRequest withGrantId(String grantId) {
        this.setGrantId(grantId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getGrantToken() != null) {
            sb.append("GrantToken: ").append(this.getGrantToken()).append(",");
        }
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
        if (!(obj instanceof RetireGrantRequest)) {
            return false;
        }
        RetireGrantRequest other = (RetireGrantRequest)obj;
        if (other.getGrantToken() == null ^ this.getGrantToken() == null) {
            return false;
        }
        if (other.getGrantToken() != null && !other.getGrantToken().equals(this.getGrantToken())) {
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
        return other.getGrantId() == null || other.getGrantId().equals(this.getGrantId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getGrantToken() == null ? 0 : this.getGrantToken().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getGrantId() == null ? 0 : this.getGrantId().hashCode());
        return hashCode;
    }

    @Override
    public RetireGrantRequest clone() {
        return (RetireGrantRequest)super.clone();
    }
}

