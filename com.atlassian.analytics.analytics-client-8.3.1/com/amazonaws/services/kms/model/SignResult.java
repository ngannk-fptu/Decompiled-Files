/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.SigningAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class SignResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String keyId;
    private ByteBuffer signature;
    private String signingAlgorithm;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public SignResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setSignature(ByteBuffer signature) {
        this.signature = signature;
    }

    public ByteBuffer getSignature() {
        return this.signature;
    }

    public SignResult withSignature(ByteBuffer signature) {
        this.setSignature(signature);
        return this;
    }

    public void setSigningAlgorithm(String signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
    }

    public String getSigningAlgorithm() {
        return this.signingAlgorithm;
    }

    public SignResult withSigningAlgorithm(String signingAlgorithm) {
        this.setSigningAlgorithm(signingAlgorithm);
        return this;
    }

    public SignResult withSigningAlgorithm(SigningAlgorithmSpec signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getSignature() != null) {
            sb.append("Signature: ").append(this.getSignature()).append(",");
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
        if (!(obj instanceof SignResult)) {
            return false;
        }
        SignResult other = (SignResult)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getSignature() == null ^ this.getSignature() == null) {
            return false;
        }
        if (other.getSignature() != null && !other.getSignature().equals(this.getSignature())) {
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
        hashCode = 31 * hashCode + (this.getSignature() == null ? 0 : this.getSignature().hashCode());
        hashCode = 31 * hashCode + (this.getSigningAlgorithm() == null ? 0 : this.getSigningAlgorithm().hashCode());
        return hashCode;
    }

    public SignResult clone() {
        try {
            return (SignResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

