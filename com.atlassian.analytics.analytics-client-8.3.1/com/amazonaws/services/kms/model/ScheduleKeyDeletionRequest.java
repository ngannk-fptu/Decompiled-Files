/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class ScheduleKeyDeletionRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private Integer pendingWindowInDays;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public ScheduleKeyDeletionRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setPendingWindowInDays(Integer pendingWindowInDays) {
        this.pendingWindowInDays = pendingWindowInDays;
    }

    public Integer getPendingWindowInDays() {
        return this.pendingWindowInDays;
    }

    public ScheduleKeyDeletionRequest withPendingWindowInDays(Integer pendingWindowInDays) {
        this.setPendingWindowInDays(pendingWindowInDays);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getPendingWindowInDays() != null) {
            sb.append("PendingWindowInDays: ").append(this.getPendingWindowInDays());
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
        if (!(obj instanceof ScheduleKeyDeletionRequest)) {
            return false;
        }
        ScheduleKeyDeletionRequest other = (ScheduleKeyDeletionRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getPendingWindowInDays() == null ^ this.getPendingWindowInDays() == null) {
            return false;
        }
        return other.getPendingWindowInDays() == null || other.getPendingWindowInDays().equals(this.getPendingWindowInDays());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getPendingWindowInDays() == null ? 0 : this.getPendingWindowInDays().hashCode());
        return hashCode;
    }

    @Override
    public ScheduleKeyDeletionRequest clone() {
        return (ScheduleKeyDeletionRequest)super.clone();
    }
}

