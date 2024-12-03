/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.SseKmsEncryptedObjectsStatus;
import java.io.Serializable;

public class SseKmsEncryptedObjects
implements Serializable,
Cloneable {
    private String status;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SseKmsEncryptedObjects withStatus(String status) {
        this.setStatus(status);
        return this;
    }

    public SseKmsEncryptedObjects withStatus(SseKmsEncryptedObjectsStatus status) {
        this.setStatus(status == null ? null : status.toString());
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getStatus() != null) {
            sb.append("Status: ").append(this.getStatus()).append(",");
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
        if (!(obj instanceof SseKmsEncryptedObjects)) {
            return false;
        }
        SseKmsEncryptedObjects other = (SseKmsEncryptedObjects)obj;
        if (other.getStatus() == null ^ this.getStatus() == null) {
            return false;
        }
        return other.getStatus() == null || other.getStatus().equals(this.getStatus());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getStatus() == null ? 0 : this.getStatus().hashCode());
        return hashCode;
    }

    public SseKmsEncryptedObjects clone() {
        try {
            return (SseKmsEncryptedObjects)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

