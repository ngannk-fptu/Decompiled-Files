/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.internal.SdkInternalMap;
import com.amazonaws.services.kms.model.DataKeyPairSpec;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GenerateDataKeyPairWithoutPlaintextRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private SdkInternalMap<String, String> encryptionContext;
    private String keyId;
    private String keyPairSpec;
    private SdkInternalList<String> grantTokens;

    public Map<String, String> getEncryptionContext() {
        if (this.encryptionContext == null) {
            this.encryptionContext = new SdkInternalMap();
        }
        return this.encryptionContext;
    }

    public void setEncryptionContext(Map<String, String> encryptionContext) {
        this.encryptionContext = encryptionContext == null ? null : new SdkInternalMap<String, String>(encryptionContext);
    }

    public GenerateDataKeyPairWithoutPlaintextRequest withEncryptionContext(Map<String, String> encryptionContext) {
        this.setEncryptionContext(encryptionContext);
        return this;
    }

    public GenerateDataKeyPairWithoutPlaintextRequest addEncryptionContextEntry(String key, String value) {
        if (null == this.encryptionContext) {
            this.encryptionContext = new SdkInternalMap();
        }
        if (this.encryptionContext.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        }
        this.encryptionContext.put(key, value);
        return this;
    }

    public GenerateDataKeyPairWithoutPlaintextRequest clearEncryptionContextEntries() {
        this.encryptionContext = null;
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GenerateDataKeyPairWithoutPlaintextRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setKeyPairSpec(String keyPairSpec) {
        this.keyPairSpec = keyPairSpec;
    }

    public String getKeyPairSpec() {
        return this.keyPairSpec;
    }

    public GenerateDataKeyPairWithoutPlaintextRequest withKeyPairSpec(String keyPairSpec) {
        this.setKeyPairSpec(keyPairSpec);
        return this;
    }

    public GenerateDataKeyPairWithoutPlaintextRequest withKeyPairSpec(DataKeyPairSpec keyPairSpec) {
        this.keyPairSpec = keyPairSpec.toString();
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

    public GenerateDataKeyPairWithoutPlaintextRequest withGrantTokens(String ... grantTokens) {
        if (this.grantTokens == null) {
            this.setGrantTokens(new SdkInternalList<String>(grantTokens.length));
        }
        for (String ele : grantTokens) {
            this.grantTokens.add(ele);
        }
        return this;
    }

    public GenerateDataKeyPairWithoutPlaintextRequest withGrantTokens(Collection<String> grantTokens) {
        this.setGrantTokens(grantTokens);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getEncryptionContext() != null) {
            sb.append("EncryptionContext: ").append(this.getEncryptionContext()).append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getKeyPairSpec() != null) {
            sb.append("KeyPairSpec: ").append(this.getKeyPairSpec()).append(",");
        }
        if (this.getGrantTokens() != null) {
            sb.append("GrantTokens: ").append(this.getGrantTokens());
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
        if (!(obj instanceof GenerateDataKeyPairWithoutPlaintextRequest)) {
            return false;
        }
        GenerateDataKeyPairWithoutPlaintextRequest other = (GenerateDataKeyPairWithoutPlaintextRequest)obj;
        if (other.getEncryptionContext() == null ^ this.getEncryptionContext() == null) {
            return false;
        }
        if (other.getEncryptionContext() != null && !other.getEncryptionContext().equals(this.getEncryptionContext())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getKeyPairSpec() == null ^ this.getKeyPairSpec() == null) {
            return false;
        }
        if (other.getKeyPairSpec() != null && !other.getKeyPairSpec().equals(this.getKeyPairSpec())) {
            return false;
        }
        if (other.getGrantTokens() == null ^ this.getGrantTokens() == null) {
            return false;
        }
        return other.getGrantTokens() == null || other.getGrantTokens().equals(this.getGrantTokens());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getEncryptionContext() == null ? 0 : this.getEncryptionContext().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getKeyPairSpec() == null ? 0 : this.getKeyPairSpec().hashCode());
        hashCode = 31 * hashCode + (this.getGrantTokens() == null ? 0 : this.getGrantTokens().hashCode());
        return hashCode;
    }

    @Override
    public GenerateDataKeyPairWithoutPlaintextRequest clone() {
        return (GenerateDataKeyPairWithoutPlaintextRequest)super.clone();
    }
}

