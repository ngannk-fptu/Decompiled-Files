/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;

public class GetParametersForImportResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String keyId;
    private ByteBuffer importToken;
    private ByteBuffer publicKey;
    private Date parametersValidTo;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GetParametersForImportResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setImportToken(ByteBuffer importToken) {
        this.importToken = importToken;
    }

    public ByteBuffer getImportToken() {
        return this.importToken;
    }

    public GetParametersForImportResult withImportToken(ByteBuffer importToken) {
        this.setImportToken(importToken);
        return this;
    }

    public void setPublicKey(ByteBuffer publicKey) {
        this.publicKey = publicKey;
    }

    public ByteBuffer getPublicKey() {
        return this.publicKey;
    }

    public GetParametersForImportResult withPublicKey(ByteBuffer publicKey) {
        this.setPublicKey(publicKey);
        return this;
    }

    public void setParametersValidTo(Date parametersValidTo) {
        this.parametersValidTo = parametersValidTo;
    }

    public Date getParametersValidTo() {
        return this.parametersValidTo;
    }

    public GetParametersForImportResult withParametersValidTo(Date parametersValidTo) {
        this.setParametersValidTo(parametersValidTo);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getImportToken() != null) {
            sb.append("ImportToken: ").append(this.getImportToken()).append(",");
        }
        if (this.getPublicKey() != null) {
            sb.append("PublicKey: ").append("***Sensitive Data Redacted***").append(",");
        }
        if (this.getParametersValidTo() != null) {
            sb.append("ParametersValidTo: ").append(this.getParametersValidTo());
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
        if (!(obj instanceof GetParametersForImportResult)) {
            return false;
        }
        GetParametersForImportResult other = (GetParametersForImportResult)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getImportToken() == null ^ this.getImportToken() == null) {
            return false;
        }
        if (other.getImportToken() != null && !other.getImportToken().equals(this.getImportToken())) {
            return false;
        }
        if (other.getPublicKey() == null ^ this.getPublicKey() == null) {
            return false;
        }
        if (other.getPublicKey() != null && !other.getPublicKey().equals(this.getPublicKey())) {
            return false;
        }
        if (other.getParametersValidTo() == null ^ this.getParametersValidTo() == null) {
            return false;
        }
        return other.getParametersValidTo() == null || other.getParametersValidTo().equals(this.getParametersValidTo());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getImportToken() == null ? 0 : this.getImportToken().hashCode());
        hashCode = 31 * hashCode + (this.getPublicKey() == null ? 0 : this.getPublicKey().hashCode());
        hashCode = 31 * hashCode + (this.getParametersValidTo() == null ? 0 : this.getParametersValidTo().hashCode());
        return hashCode;
    }

    public GetParametersForImportResult clone() {
        try {
            return (GetParametersForImportResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

