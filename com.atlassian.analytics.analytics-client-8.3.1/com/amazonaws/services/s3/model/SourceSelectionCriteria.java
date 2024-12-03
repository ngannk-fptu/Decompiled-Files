/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ReplicaModifications;
import com.amazonaws.services.s3.model.SseKmsEncryptedObjects;
import java.io.Serializable;

public class SourceSelectionCriteria
implements Serializable,
Cloneable {
    private SseKmsEncryptedObjects sseKmsEncryptedObjects;
    private ReplicaModifications replicaModifications;

    public SseKmsEncryptedObjects getSseKmsEncryptedObjects() {
        return this.sseKmsEncryptedObjects;
    }

    public void setSseKmsEncryptedObjects(SseKmsEncryptedObjects sseKmsEncryptedObjects) {
        this.sseKmsEncryptedObjects = sseKmsEncryptedObjects;
    }

    public SourceSelectionCriteria withSseKmsEncryptedObjects(SseKmsEncryptedObjects sseKmsEncryptedObjects) {
        this.setSseKmsEncryptedObjects(sseKmsEncryptedObjects);
        return this;
    }

    public ReplicaModifications getReplicaModifications() {
        return this.replicaModifications;
    }

    public void setReplicaModifications(ReplicaModifications replicaModifications) {
        this.replicaModifications = replicaModifications;
    }

    public SourceSelectionCriteria withReplicaModifications(ReplicaModifications replicaModifications) {
        this.setReplicaModifications(replicaModifications);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getSseKmsEncryptedObjects() != null) {
            sb.append("SseKmsEncryptedObjects: ").append(this.getSseKmsEncryptedObjects()).append(",");
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
        if (!(obj instanceof SourceSelectionCriteria)) {
            return false;
        }
        SourceSelectionCriteria other = (SourceSelectionCriteria)obj;
        if (other.getSseKmsEncryptedObjects() == null ^ this.getSseKmsEncryptedObjects() == null) {
            return false;
        }
        return other.getSseKmsEncryptedObjects() == null || other.getSseKmsEncryptedObjects().equals(this.getSseKmsEncryptedObjects());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getSseKmsEncryptedObjects() == null ? 0 : this.getSseKmsEncryptedObjects().hashCode());
        return hashCode;
    }

    public SourceSelectionCriteria clone() {
        try {
            return (SourceSelectionCriteria)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

