/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.internal.SdkInternalMap;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EncryptRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private ByteBuffer plaintext;
    private SdkInternalMap<String, String> encryptionContext;
    private SdkInternalList<String> grantTokens;
    private String encryptionAlgorithm;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public EncryptRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setPlaintext(ByteBuffer plaintext) {
        this.plaintext = plaintext;
    }

    public ByteBuffer getPlaintext() {
        return this.plaintext;
    }

    public EncryptRequest withPlaintext(ByteBuffer plaintext) {
        this.setPlaintext(plaintext);
        return this;
    }

    public Map<String, String> getEncryptionContext() {
        if (this.encryptionContext == null) {
            this.encryptionContext = new SdkInternalMap();
        }
        return this.encryptionContext;
    }

    public void setEncryptionContext(Map<String, String> encryptionContext) {
        this.encryptionContext = encryptionContext == null ? null : new SdkInternalMap<String, String>(encryptionContext);
    }

    public EncryptRequest withEncryptionContext(Map<String, String> encryptionContext) {
        this.setEncryptionContext(encryptionContext);
        return this;
    }

    public EncryptRequest addEncryptionContextEntry(String key, String value) {
        if (null == this.encryptionContext) {
            this.encryptionContext = new SdkInternalMap();
        }
        if (this.encryptionContext.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        }
        this.encryptionContext.put(key, value);
        return this;
    }

    public EncryptRequest clearEncryptionContextEntries() {
        this.encryptionContext = null;
        return this;
    }

    public List<String> getGrantTokens() {
        if (this.grantTokens == null) {
            this.grantTokens = new SdkInternalList();
        }
        return this.grantTokens;
    }

    public void setGrantTokens(Collection<String> grantTokens) {
        if (grantTokens == null) {
            this.grantTokens = null;
            return;
        }
        this.grantTokens = new SdkInternalList<String>(grantTokens);
    }

    public EncryptRequest withGrantTokens(String ... grantTokens) {
        if (this.grantTokens == null) {
            this.setGrantTokens(new SdkInternalList<String>(grantTokens.length));
        }
        for (String ele : grantTokens) {
            this.grantTokens.add(ele);
        }
        return this;
    }

    public EncryptRequest withGrantTokens(Collection<String> grantTokens) {
        this.setGrantTokens(grantTokens);
        return this;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }

    public EncryptRequest withEncryptionAlgorithm(String encryptionAlgorithm) {
        this.setEncryptionAlgorithm(encryptionAlgorithm);
        return this;
    }

    public EncryptRequest withEncryptionAlgorithm(EncryptionAlgorithmSpec encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getPlaintext() != null) {
            sb.append("Plaintext: ").append("***Sensitive Data Redacted***").append(",");
        }
        if (this.getEncryptionContext() != null) {
            sb.append("EncryptionContext: ").append(this.getEncryptionContext()).append(",");
        }
        if (this.getGrantTokens() != null) {
            sb.append("GrantTokens: ").append(this.getGrantTokens()).append(",");
        }
        if (this.getEncryptionAlgorithm() != null) {
            sb.append("EncryptionAlgorithm: ").append(this.getEncryptionAlgorithm());
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
        if (!(obj instanceof EncryptRequest)) {
            return false;
        }
        EncryptRequest other = (EncryptRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getPlaintext() == null ^ this.getPlaintext() == null) {
            return false;
        }
        if (other.getPlaintext() != null && !other.getPlaintext().equals(this.getPlaintext())) {
            return false;
        }
        if (other.getEncryptionContext() == null ^ this.getEncryptionContext() == null) {
            return false;
        }
        if (other.getEncryptionContext() != null && !other.getEncryptionContext().equals(this.getEncryptionContext())) {
            return false;
        }
        if (other.getGrantTokens() == null ^ this.getGrantTokens() == null) {
            return false;
        }
        if (other.getGrantTokens() != null && !other.getGrantTokens().equals(this.getGrantTokens())) {
            return false;
        }
        if (other.getEncryptionAlgorithm() == null ^ this.getEncryptionAlgorithm() == null) {
            return false;
        }
        return other.getEncryptionAlgorithm() == null || other.getEncryptionAlgorithm().equals(this.getEncryptionAlgorithm());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getPlaintext() == null ? 0 : this.getPlaintext().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptionContext() == null ? 0 : this.getEncryptionContext().hashCode());
        hashCode = 31 * hashCode + (this.getGrantTokens() == null ? 0 : this.getGrantTokens().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptionAlgorithm() == null ? 0 : this.getEncryptionAlgorithm().hashCode());
        return hashCode;
    }

    @Override
    public EncryptRequest clone() {
        return (EncryptRequest)super.clone();
    }
}

