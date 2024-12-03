/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.inventory;

import com.amazonaws.services.s3.model.inventory.InventoryEncryption;
import java.io.Serializable;

public class ServerSideEncryptionKMS
implements InventoryEncryption,
Serializable,
Cloneable {
    private String keyId;

    public String getKeyId() {
        return this.keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public ServerSideEncryptionKMS withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
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
        if (!(obj instanceof ServerSideEncryptionKMS)) {
            return false;
        }
        ServerSideEncryptionKMS other = (ServerSideEncryptionKMS)obj;
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

    public ServerSideEncryptionKMS clone() {
        try {
            return (ServerSideEncryptionKMS)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

