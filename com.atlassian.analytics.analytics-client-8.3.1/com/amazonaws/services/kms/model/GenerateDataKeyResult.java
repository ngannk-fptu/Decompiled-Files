/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class GenerateDataKeyResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private ByteBuffer ciphertextBlob;
    private ByteBuffer plaintext;
    private String keyId;

    public void setCiphertextBlob(ByteBuffer ciphertextBlob) {
        this.ciphertextBlob = ciphertextBlob;
    }

    public ByteBuffer getCiphertextBlob() {
        return this.ciphertextBlob;
    }

    public GenerateDataKeyResult withCiphertextBlob(ByteBuffer ciphertextBlob) {
        this.setCiphertextBlob(ciphertextBlob);
        return this;
    }

    public void setPlaintext(ByteBuffer plaintext) {
        this.plaintext = plaintext;
    }

    public ByteBuffer getPlaintext() {
        return this.plaintext;
    }

    public GenerateDataKeyResult withPlaintext(ByteBuffer plaintext) {
        this.setPlaintext(plaintext);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GenerateDataKeyResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCiphertextBlob() != null) {
            sb.append("CiphertextBlob: ").append(this.getCiphertextBlob()).append(",");
        }
        if (this.getPlaintext() != null) {
            sb.append("Plaintext: ").append("***Sensitive Data Redacted***").append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId());
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
        if (!(obj instanceof GenerateDataKeyResult)) {
            return false;
        }
        GenerateDataKeyResult other = (GenerateDataKeyResult)obj;
        if (other.getCiphertextBlob() == null ^ this.getCiphertextBlob() == null) {
            return false;
        }
        if (other.getCiphertextBlob() != null && !other.getCiphertextBlob().equals(this.getCiphertextBlob())) {
            return false;
        }
        if (other.getPlaintext() == null ^ this.getPlaintext() == null) {
            return false;
        }
        if (other.getPlaintext() != null && !other.getPlaintext().equals(this.getPlaintext())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        return other.getKeyId() == null || other.getKeyId().equals(this.getKeyId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCiphertextBlob() == null ? 0 : this.getCiphertextBlob().hashCode());
        hashCode = 31 * hashCode + (this.getPlaintext() == null ? 0 : this.getPlaintext().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        return hashCode;
    }

    public GenerateDataKeyResult clone() {
        try {
            return (GenerateDataKeyResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

