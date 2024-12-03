/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class UpdateKeyDescriptionRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private String description;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public UpdateKeyDescriptionRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public UpdateKeyDescriptionRequest withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getDescription() != null) {
            sb.append("Description: ").append(this.getDescription());
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
        if (!(obj instanceof UpdateKeyDescriptionRequest)) {
            return false;
        }
        UpdateKeyDescriptionRequest other = (UpdateKeyDescriptionRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getDescription() == null ^ this.getDescription() == null) {
            return false;
        }
        return other.getDescription() == null || other.getDescription().equals(this.getDescription());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getDescription() == null ? 0 : this.getDescription().hashCode());
        return hashCode;
    }

    @Override
    public UpdateKeyDescriptionRequest clone() {
        return (UpdateKeyDescriptionRequest)super.clone();
    }
}

