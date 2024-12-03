/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.internal.SdkInternalMap;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.transform.GrantConstraintsMarshaller;
import java.io.Serializable;
import java.util.Map;

public class GrantConstraints
implements Serializable,
Cloneable,
StructuredPojo {
    private SdkInternalMap<String, String> encryptionContextSubset;
    private SdkInternalMap<String, String> encryptionContextEquals;

    public Map<String, String> getEncryptionContextSubset() {
        if (this.encryptionContextSubset == null) {
            this.encryptionContextSubset = new SdkInternalMap();
        }
        return this.encryptionContextSubset;
    }

    public void setEncryptionContextSubset(Map<String, String> encryptionContextSubset) {
        this.encryptionContextSubset = encryptionContextSubset == null ? null : new SdkInternalMap<String, String>(encryptionContextSubset);
    }

    public GrantConstraints withEncryptionContextSubset(Map<String, String> encryptionContextSubset) {
        this.setEncryptionContextSubset(encryptionContextSubset);
        return this;
    }

    public GrantConstraints addEncryptionContextSubsetEntry(String key, String value) {
        if (null == this.encryptionContextSubset) {
            this.encryptionContextSubset = new SdkInternalMap();
        }
        if (this.encryptionContextSubset.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        }
        this.encryptionContextSubset.put(key, value);
        return this;
    }

    public GrantConstraints clearEncryptionContextSubsetEntries() {
        this.encryptionContextSubset = null;
        return this;
    }

    public Map<String, String> getEncryptionContextEquals() {
        if (this.encryptionContextEquals == null) {
            this.encryptionContextEquals = new SdkInternalMap();
        }
        return this.encryptionContextEquals;
    }

    public void setEncryptionContextEquals(Map<String, String> encryptionContextEquals) {
        this.encryptionContextEquals = encryptionContextEquals == null ? null : new SdkInternalMap<String, String>(encryptionContextEquals);
    }

    public GrantConstraints withEncryptionContextEquals(Map<String, String> encryptionContextEquals) {
        this.setEncryptionContextEquals(encryptionContextEquals);
        return this;
    }

    public GrantConstraints addEncryptionContextEqualsEntry(String key, String value) {
        if (null == this.encryptionContextEquals) {
            this.encryptionContextEquals = new SdkInternalMap();
        }
        if (this.encryptionContextEquals.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        }
        this.encryptionContextEquals.put(key, value);
        return this;
    }

    public GrantConstraints clearEncryptionContextEqualsEntries() {
        this.encryptionContextEquals = null;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getEncryptionContextSubset() != null) {
            sb.append("EncryptionContextSubset: ").append(this.getEncryptionContextSubset()).append(",");
        }
        if (this.getEncryptionContextEquals() != null) {
            sb.append("EncryptionContextEquals: ").append(this.getEncryptionContextEquals());
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
        if (!(obj instanceof GrantConstraints)) {
            return false;
        }
        GrantConstraints other = (GrantConstraints)obj;
        if (other.getEncryptionContextSubset() == null ^ this.getEncryptionContextSubset() == null) {
            return false;
        }
        if (other.getEncryptionContextSubset() != null && !other.getEncryptionContextSubset().equals(this.getEncryptionContextSubset())) {
            return false;
        }
        if (other.getEncryptionContextEquals() == null ^ this.getEncryptionContextEquals() == null) {
            return false;
        }
        return other.getEncryptionContextEquals() == null || other.getEncryptionContextEquals().equals(this.getEncryptionContextEquals());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getEncryptionContextSubset() == null ? 0 : this.getEncryptionContextSubset().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptionContextEquals() == null ? 0 : this.getEncryptionContextEquals().hashCode());
        return hashCode;
    }

    public GrantConstraints clone() {
        try {
            return (GrantConstraints)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        GrantConstraintsMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

