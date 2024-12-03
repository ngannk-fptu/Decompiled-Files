/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class DecryptResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String keyId;
    private ByteBuffer plaintext;
    private String encryptionAlgorithm;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public DecryptResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setPlaintext(ByteBuffer plaintext) {
        this.plaintext = plaintext;
    }

    public ByteBuffer getPlaintext() {
        return this.plaintext;
    }

    public DecryptResult withPlaintext(ByteBuffer plaintext) {
        this.setPlaintext(plaintext);
        return this;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }

    public DecryptResult withEncryptionAlgorithm(String encryptionAlgorithm) {
        this.setEncryptionAlgorithm(encryptionAlgorithm);
        return this;
    }

    public DecryptResult withEncryptionAlgorithm(EncryptionAlgorithmSpec encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getPlaintext() != null) {
            sb.append("Plaintext: ").append("***Sensitive Data Redacted***").append(",");
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
        if (!(obj instanceof DecryptResult)) {
            return false;
        }
        DecryptResult other = (DecryptResult)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getPlaintext() == null ^ this.getPlaintext() == null) {
            return false;
        }
        if (other.getPlaintext() != null && !other.getPlaintext().equals(this.getPlaintext())) {
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
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getPlaintext() == null ? 0 : this.getPlaintext().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptionAlgorithm() == null ? 0 : this.getEncryptionAlgorithm().hashCode());
        return hashCode;
    }

    public DecryptResult clone() {
        try {
            return (DecryptResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

