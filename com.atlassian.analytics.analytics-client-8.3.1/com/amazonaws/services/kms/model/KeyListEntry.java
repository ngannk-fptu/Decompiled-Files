/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.transform.KeyListEntryMarshaller;
import java.io.Serializable;

public class KeyListEntry
implements Serializable,
Cloneable,
StructuredPojo {
    private String keyId;
    private String keyArn;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public KeyListEntry withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setKeyArn(String keyArn) {
        this.keyArn = keyArn;
    }

    public String getKeyArn() {
        return this.keyArn;
    }

    public KeyListEntry withKeyArn(String keyArn) {
        this.setKeyArn(keyArn);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getKeyArn() != null) {
            sb.append("KeyArn: ").append(this.getKeyArn());
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
        if (!(obj instanceof KeyListEntry)) {
            return false;
        }
        KeyListEntry other = (KeyListEntry)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getKeyArn() == null ^ this.getKeyArn() == null) {
            return false;
        }
        return other.getKeyArn() == null || other.getKeyArn().equals(this.getKeyArn());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getKeyArn() == null ? 0 : this.getKeyArn().hashCode());
        return hashCode;
    }

    public KeyListEntry clone() {
        try {
            return (KeyListEntry)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        KeyListEntryMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

