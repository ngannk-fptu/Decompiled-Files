/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.DataKeyPairSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class GenerateDataKeyPairWithoutPlaintextResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private ByteBuffer privateKeyCiphertextBlob;
    private ByteBuffer publicKey;
    private String keyId;
    private String keyPairSpec;

    public void setPrivateKeyCiphertextBlob(ByteBuffer privateKeyCiphertextBlob) {
        this.privateKeyCiphertextBlob = privateKeyCiphertextBlob;
    }

    public ByteBuffer getPrivateKeyCiphertextBlob() {
        return this.privateKeyCiphertextBlob;
    }

    public GenerateDataKeyPairWithoutPlaintextResult withPrivateKeyCiphertextBlob(ByteBuffer privateKeyCiphertextBlob) {
        this.setPrivateKeyCiphertextBlob(privateKeyCiphertextBlob);
        return this;
    }

    public void setPublicKey(ByteBuffer publicKey) {
        this.publicKey = publicKey;
    }

    public ByteBuffer getPublicKey() {
        return this.publicKey;
    }

    public GenerateDataKeyPairWithoutPlaintextResult withPublicKey(ByteBuffer publicKey) {
        this.setPublicKey(publicKey);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GenerateDataKeyPairWithoutPlaintextResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setKeyPairSpec(String keyPairSpec) {
        this.keyPairSpec = keyPairSpec;
    }

    public String getKeyPairSpec() {
        return this.keyPairSpec;
    }

    public GenerateDataKeyPairWithoutPlaintextResult withKeyPairSpec(String keyPairSpec) {
        this.setKeyPairSpec(keyPairSpec);
        return this;
    }

    public GenerateDataKeyPairWithoutPlaintextResult withKeyPairSpec(DataKeyPairSpec keyPairSpec) {
        this.keyPairSpec = keyPairSpec.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getPrivateKeyCiphertextBlob() != null) {
            sb.append("PrivateKeyCiphertextBlob: ").append(this.getPrivateKeyCiphertextBlob()).append(",");
        }
        if (this.getPublicKey() != null) {
            sb.append("PublicKey: ").append(this.getPublicKey()).append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getKeyPairSpec() != null) {
            sb.append("KeyPairSpec: ").append(this.getKeyPairSpec());
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
        if (!(obj instanceof GenerateDataKeyPairWithoutPlaintextResult)) {
            return false;
        }
        GenerateDataKeyPairWithoutPlaintextResult other = (GenerateDataKeyPairWithoutPlaintextResult)obj;
        if (other.getPrivateKeyCiphertextBlob() == null ^ this.getPrivateKeyCiphertextBlob() == null) {
            return false;
        }
        if (other.getPrivateKeyCiphertextBlob() != null && !other.getPrivateKeyCiphertextBlob().equals(this.getPrivateKeyCiphertextBlob())) {
            return false;
        }
        if (other.getPublicKey() == null ^ this.getPublicKey() == null) {
            return false;
        }
        if (other.getPublicKey() != null && !other.getPublicKey().equals(this.getPublicKey())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getKeyPairSpec() == null ^ this.getKeyPairSpec() == null) {
            return false;
        }
        return other.getKeyPairSpec() == null || other.getKeyPairSpec().equals(this.getKeyPairSpec());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getPrivateKeyCiphertextBlob() == null ? 0 : this.getPrivateKeyCiphertextBlob().hashCode());
        hashCode = 31 * hashCode + (this.getPublicKey() == null ? 0 : this.getPublicKey().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getKeyPairSpec() == null ? 0 : this.getKeyPairSpec().hashCode());
        return hashCode;
    }

    public GenerateDataKeyPairWithoutPlaintextResult clone() {
        try {
            return (GenerateDataKeyPairWithoutPlaintextResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

