/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class CancelKeyDeletionRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public CancelKeyDeletionRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
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
        if (!(obj instanceof CancelKeyDeletionRequest)) {
            return false;
        }
        CancelKeyDeletionRequest other = (CancelKeyDeletionRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        return other.getKeyId() == null || other.getKeyId().equals(this.getKeyId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        return hashCode;
    }

    @Override
    public CancelKeyDeletionRequest clone() {
        return (CancelKeyDeletionRequest)super.clone();
    }
}

