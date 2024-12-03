/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class EncryptResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private ByteBuffer ciphertextBlob;
    private String keyId;
    private String encryptionAlgorithm;

    public void setCiphertextBlob(ByteBuffer ciphertextBlob) {
        this.ciphertextBlob = ciphertextBlob;
    }

    public ByteBuffer getCiphertextBlob() {
        return this.ciphertextBlob;
    }

    public EncryptResult withCiphertextBlob(ByteBuffer ciphertextBlob) {
        this.setCiphertextBlob(ciphertextBlob);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public EncryptResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }

    public EncryptResult withEncryptionAlgorithm(String encryptionAlgorithm) {
        this.setEncryptionAlgorithm(encryptionAlgorithm);
        return this;
    }

    public EncryptResult withEncryptionAlgorithm(EncryptionAlgorithmSpec encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCiphertextBlob() != null) {
            sb.append("CiphertextBlob: ").append(this.getCiphertextBlob()).append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getEncryptionAlgorithm() != null) {
            sb.append("EncryptionAlgorithm: ").append(this.getEncryptionAlgorithm());
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
        if (!(obj instanceof EncryptResult)) {
            return false;
        }
        EncryptResult other = (EncryptResult)obj;
        if (other.getCiphertextBlob() == null ^ this.getCiphertextBlob() == null) {
            return false;
        }
        if (other.getCiphertextBlob() != null && !other.getCiphertextBlob().equals(this.getCiphertextBlob())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getEncryptionAlgorithm() == null ^ this.getEncryptionAlgorithm() == null) {
            return false;
        }
        return other.getEncryptionAlgorithm() == null || other.getEncryptionAlgorithm().equals(this.getEncryptionAlgorithm());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCiphertextBlob() == null ? 0 : this.getCiphertextBlob().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptionAlgorithm() == null ? 0 : this.getEncryptionAlgorithm().hashCode());
        return hashCode;
    }

    public EncryptResult clone() {
        try {
            return (EncryptResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

