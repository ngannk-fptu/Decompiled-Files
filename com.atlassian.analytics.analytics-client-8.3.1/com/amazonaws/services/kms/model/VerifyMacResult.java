/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.MacAlgorithmSpec;
import java.io.Serializable;

public class VerifyMacResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String keyId;
    private Boolean macValid;
    private String macAlgorithm;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public VerifyMacResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setMacValid(Boolean macValid) {
        this.macValid = macValid;
    }

    public Boolean getMacValid() {
        return this.macValid;
    }

    public VerifyMacResult withMacValid(Boolean macValid) {
        this.setMacValid(macValid);
        return this;
    }

    public Boolean isMacValid() {
        return this.macValid;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

    public String getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public VerifyMacResult withMacAlgorithm(String macAlgorithm) {
        this.setMacAlgorithm(macAlgorithm);
        return this;
    }

    public VerifyMacResult withMacAlgorithm(MacAlgorithmSpec macAlgorithm) {
        this.macAlgorithm = macAlgorithm.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getMacValid() != null) {
            sb.append("MacValid: ").append(this.getMacValid()).append(",");
        }
        if (this.getMacAlgorithm() != null) {
            sb.append("MacAlgorithm: ").append(this.getMacAlgorithm());
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
        if (!(obj instanceof VerifyMacResult)) {
            return false;
        }
        VerifyMacResult other = (VerifyMacResult)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getMacValid() == null ^ this.getMacValid() == null) {
            return false;
        }
        if (other.getMacValid() != null && !other.getMacValid().equals(this.getMacValid())) {
            return false;
        }
        if (other.getMacAlgorithm() == null ^ this.getMacAlgorithm() == null) {
            return false;
        }
        return other.getMacAlgorithm() == null || other.getMacAlgorithm().equals(this.getMacAlgorithm());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getMacValid() == null ? 0 : this.getMacValid().hashCode());
        hashCode = 31 * hashCode + (this.getMacAlgorithm() == null ? 0 : this.getMacAlgorithm().hashCode());
        return hashCode;
    }

    public VerifyMacResult clone() {
        try {
            return (VerifyMacResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

