/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.SigningAlgorithmSpec;
import java.io.Serializable;

public class VerifyResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String keyId;
    private Boolean signatureValid;
    private String signingAlgorithm;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public VerifyResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setSignatureValid(Boolean signatureValid) {
        this.signatureValid = signatureValid;
    }

    public Boolean getSignatureValid() {
        return this.signatureValid;
    }

    public VerifyResult withSignatureValid(Boolean signatureValid) {
        this.setSignatureValid(signatureValid);
        return this;
    }

    public Boolean isSignatureValid() {
        return this.signatureValid;
    }

    public void setSigningAlgorithm(String signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
    }

    public String getSigningAlgorithm() {
        return this.signingAlgorithm;
    }

    public VerifyResult withSigningAlgorithm(String signingAlgorithm) {
        this.setSigningAlgorithm(signingAlgorithm);
        return this;
    }

    public VerifyResult withSigningAlgorithm(SigningAlgorithmSpec signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getSignatureValid() != null) {
            sb.append("SignatureValid: ").append(this.getSignatureValid()).append(",");
        }
        if (this.getSigningAlgorithm() != null) {
            sb.append("SigningAlgorithm: ").append(this.getSigningAlgorithm());
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
        if (!(obj instanceof VerifyResult)) {
            return false;
        }
        VerifyResult other = (VerifyResult)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getSignatureValid() == null ^ this.getSignatureValid() == null) {
            return false;
        }
        if (other.getSignatureValid() != null && !other.getSignatureValid().equals(this.getSignatureValid())) {
            return false;
        }
        if (other.getSigningAlgorithm() == null ^ this.getSigningAlgorithm() == null) {
            return false;
        }
        return other.getSigningAlgorithm() == null || other.getSigningAlgorithm().equals(this.getSigningAlgorithm());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getSignatureValid() == null ? 0 : this.getSignatureValid().hashCode());
        hashCode = 31 * hashCode + (this.getSigningAlgorithm() == null ? 0 : this.getSigningAlgorithm().hashCode());
        return hashCode;
    }

    public VerifyResult clone() {
        try {
            return (VerifyResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

