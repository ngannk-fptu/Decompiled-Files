/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class ReEncryptResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private ByteBuffer ciphertextBlob;
    private String sourceKeyId;
    private String keyId;
    private String sourceEncryptionAlgorithm;
    private String destinationEncryptionAlgorithm;

    public void setCiphertextBlob(ByteBuffer ciphertextBlob) {
        this.ciphertextBlob = ciphertextBlob;
    }

    public ByteBuffer getCiphertextBlob() {
        return this.ciphertextBlob;
    }

    public ReEncryptResult withCiphertextBlob(ByteBuffer ciphertextBlob) {
        this.setCiphertextBlob(ciphertextBlob);
        return this;
    }

    public void setSourceKeyId(String sourceKeyId) {
        this.sourceKeyId = sourceKeyId;
    }

    public String getSourceKeyId() {
        return this.sourceKeyId;
    }

    public ReEncryptResult withSourceKeyId(String sourceKeyId) {
        this.setSourceKeyId(sourceKeyId);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public ReEncryptResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setSourceEncryptionAlgorithm(String sourceEncryptionAlgorithm) {
        this.sourceEncryptionAlgorithm = sourceEncryptionAlgorithm;
    }

    public String getSourceEncryptionAlgorithm() {
        return this.sourceEncryptionAlgorithm;
    }

    public ReEncryptResult withSourceEncryptionAlgorithm(String sourceEncryptionAlgorithm) {
        this.setSourceEncryptionAlgorithm(sourceEncryptionAlgorithm);
        return this;
    }

    public ReEncryptResult withSourceEncryptionAlgorithm(EncryptionAlgorithmSpec sourceEncryptionAlgorithm) {
        this.sourceEncryptionAlgorithm = sourceEncryptionAlgorithm.toString();
        return this;
    }

    public void setDestinationEncryptionAlgorithm(String destinationEncryptionAlgorithm) {
        this.destinationEncryptionAlgorithm = destinationEncryptionAlgorithm;
    }

    public String getDestinationEncryptionAlgorithm() {
        return this.destinationEncryptionAlgorithm;
    }

    public ReEncryptResult withDestinationEncryptionAlgorithm(String destinationEncryptionAlgorithm) {
        this.setDestinationEncryptionAlgorithm(destinationEncryptionAlgorithm);
        return this;
    }

    public ReEncryptResult withDestinationEncryptionAlgorithm(EncryptionAlgorithmSpec destinationEncryptionAlgorithm) {
        this.destinationEncryptionAlgorithm = destinationEncryptionAlgorithm.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCiphertextBlob() != null) {
            sb.append("CiphertextBlob: ").append(this.getCiphertextBlob()).append(",");
        }
        if (this.getSourceKeyId() != null) {
            sb.append("SourceKeyId: ").append(this.getSourceKeyId()).append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getSourceEncryptionAlgorithm() != null) {
            sb.append("SourceEncryptionAlgorithm: ").append(this.getSourceEncryptionAlgorithm()).append(",");
        }
        if (this.getDestinationEncryptionAlgorithm() != null) {
            sb.append("DestinationEncryptionAlgorithm: ").append(this.getDestinationEncryptionAlgorithm());
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
        if (!(obj instanceof ReEncryptResult)) {
            return false;
        }
        ReEncryptResult other = (ReEncryptResult)obj;
        if (other.getCiphertextBlob() == null ^ this.getCiphertextBlob() == null) {
            return false;
        }
        if (other.getCiphertextBlob() != null && !other.getCiphertextBlob().equals(this.getCiphertextBlob())) {
            return false;
        }
        if (other.getSourceKeyId() == null ^ this.getSourceKeyId() == null) {
            return false;
        }
        if (other.getSourceKeyId() != null && !other.getSourceKeyId().equals(this.getSourceKeyId())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getSourceEncryptionAlgorithm() == null ^ this.getSourceEncryptionAlgorithm() == null) {
            return false;
        }
        if (other.getSourceEncryptionAlgorithm() != null && !other.getSourceEncryptionAlgorithm().equals(this.getSourceEncryptionAlgorithm())) {
            return false;
        }
        if (other.getDestinationEncryptionAlgorithm() == null ^ this.getDestinationEncryptionAlgorithm() == null) {
            return false;
        }
        return other.getDestinationEncryptionAlgorithm() == null || other.getDestinationEncryptionAlgorithm().equals(this.getDestinationEncryptionAlgorithm());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCiphertextBlob() == null ? 0 : this.getCiphertextBlob().hashCode());
        hashCode = 31 * hashCode + (this.getSourceKeyId() == null ? 0 : this.getSourceKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getSourceEncryptionAlgorithm() == null ? 0 : this.getSourceEncryptionAlgorithm().hashCode());
        hashCode = 31 * hashCode + (this.getDestinationEncryptionAlgorithm() == null ? 0 : this.getDestinationEncryptionAlgorithm().hashCode());
        return hashCode;
    }

    public ReEncryptResult clone() {
        try {
            return (ReEncryptResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

