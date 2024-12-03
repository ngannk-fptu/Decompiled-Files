/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.kms.model.ExpirationModelType;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;

public class ImportKeyMaterialRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private ByteBuffer importToken;
    private ByteBuffer encryptedKeyMaterial;
    private Date validTo;
    private String expirationModel;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public ImportKeyMaterialRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setImportToken(ByteBuffer importToken) {
        this.importToken = importToken;
    }

    public ByteBuffer getImportToken() {
        return this.importToken;
    }

    public ImportKeyMaterialRequest withImportToken(ByteBuffer importToken) {
        this.setImportToken(importToken);
        return this;
    }

    public void setEncryptedKeyMaterial(ByteBuffer encryptedKeyMaterial) {
        this.encryptedKeyMaterial = encryptedKeyMaterial;
    }

    public ByteBuffer getEncryptedKeyMaterial() {
        return this.encryptedKeyMaterial;
    }

    public ImportKeyMaterialRequest withEncryptedKeyMaterial(ByteBuffer encryptedKeyMaterial) {
        this.setEncryptedKeyMaterial(encryptedKeyMaterial);
        return this;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getValidTo() {
        return this.validTo;
    }

    public ImportKeyMaterialRequest withValidTo(Date validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setExpirationModel(String expirationModel) {
        this.expirationModel = expirationModel;
    }

    public String getExpirationModel() {
        return this.expirationModel;
    }

    public ImportKeyMaterialRequest withExpirationModel(String expirationModel) {
        this.setExpirationModel(expirationModel);
        return this;
    }

    public void setExpirationModel(ExpirationModelType expirationModel) {
        this.withExpirationModel(expirationModel);
    }

    public ImportKeyMaterialRequest withExpirationModel(ExpirationModelType expirationModel) {
        this.expirationModel = expirationModel.toString();
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
        if (this.getEncryptedKeyMaterial() != null) {
            sb.append("EncryptedKeyMaterial: ").append(this.getEncryptedKeyMaterial()).append(",");
        }
        if (this.getValidTo() != null) {
            sb.append("ValidTo: ").append(this.getValidTo()).append(",");
        }
        if (this.getExpirationModel() != null) {
            sb.append("ExpirationModel: ").append(this.getExpirationModel());
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
        if (!(obj instanceof ImportKeyMaterialRequest)) {
            return false;
        }
        ImportKeyMaterialRequest other = (ImportKeyMaterialRequest)obj;
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
        if (other.getEncryptedKeyMaterial() == null ^ this.getEncryptedKeyMaterial() == null) {
            return false;
        }
        if (other.getEncryptedKeyMaterial() != null && !other.getEncryptedKeyMaterial().equals(this.getEncryptedKeyMaterial())) {
            return false;
        }
        if (other.getValidTo() == null ^ this.getValidTo() == null) {
            return false;
        }
        if (other.getValidTo() != null && !other.getValidTo().equals(this.getValidTo())) {
            return false;
        }
        if (other.getExpirationModel() == null ^ this.getExpirationModel() == null) {
            return false;
        }
        return other.getExpirationModel() == null || other.getExpirationModel().equals(this.getExpirationModel());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getImportToken() == null ? 0 : this.getImportToken().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptedKeyMaterial() == null ? 0 : this.getEncryptedKeyMaterial().hashCode());
        hashCode = 31 * hashCode + (this.getValidTo() == null ? 0 : this.getValidTo().hashCode());
        hashCode = 31 * hashCode + (this.getExpirationModel() == null ? 0 : this.getExpirationModel().hashCode());
        return hashCode;
    }

    @Override
    public ImportKeyMaterialRequest clone() {
        return (ImportKeyMaterialRequest)super.clone();
    }
}

