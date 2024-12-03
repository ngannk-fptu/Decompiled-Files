/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.KeyState;
import java.io.Serializable;
import java.util.Date;

public class ScheduleKeyDeletionResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String keyId;
    private Date deletionDate;
    private String keyState;
    private Integer pendingWindowInDays;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public ScheduleKeyDeletionResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setDeletionDate(Date deletionDate) {
        this.deletionDate = deletionDate;
    }

    public Date getDeletionDate() {
        return this.deletionDate;
    }

    public ScheduleKeyDeletionResult withDeletionDate(Date deletionDate) {
        this.setDeletionDate(deletionDate);
        return this;
    }

    public void setKeyState(String keyState) {
        this.keyState = keyState;
    }

    public String getKeyState() {
        return this.keyState;
    }

    public ScheduleKeyDeletionResult withKeyState(String keyState) {
        this.setKeyState(keyState);
        return this;
    }

    public ScheduleKeyDeletionResult withKeyState(KeyState keyState) {
        this.keyState = keyState.toString();
        return this;
    }

    public void setPendingWindowInDays(Integer pendingWindowInDays) {
        this.pendingWindowInDays = pendingWindowInDays;
    }

    public Integer getPendingWindowInDays() {
        return this.pendingWindowInDays;
    }

    public ScheduleKeyDeletionResult withPendingWindowInDays(Integer pendingWindowInDays) {
        this.setPendingWindowInDays(pendingWindowInDays);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getDeletionDate() != null) {
            sb.append("DeletionDate: ").append(this.getDeletionDate()).append(",");
        }
        if (this.getKeyState() != null) {
            sb.append("KeyState: ").append(this.getKeyState()).append(",");
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
        if (!(obj instanceof ScheduleKeyDeletionResult)) {
            return false;
        }
        ScheduleKeyDeletionResult other = (ScheduleKeyDeletionResult)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getDeletionDate() == null ^ this.getDeletionDate() == null) {
            return false;
        }
        if (other.getDeletionDate() != null && !other.getDeletionDate().equals(this.getDeletionDate())) {
            return false;
        }
        if (other.getKeyState() == null ^ this.getKeyState() == null) {
            return false;
        }
        if (other.getKeyState() != null && !other.getKeyState().equals(this.getKeyState())) {
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
        hashCode = 31 * hashCode + (this.getDeletionDate() == null ? 0 : this.getDeletionDate().hashCode());
        hashCode = 31 * hashCode + (this.getKeyState() == null ? 0 : this.getKeyState().hashCode());
        hashCode = 31 * hashCode + (this.getPendingWindowInDays() == null ? 0 : this.getPendingWindowInDays().hashCode());
        return hashCode;
    }

    public ScheduleKeyDeletionResult clone() {
        try {
            return (ScheduleKeyDeletionResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

