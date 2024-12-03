/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.internal.SdkInternalMap;
import com.amazonaws.services.kms.model.DataKeySpec;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GenerateDataKeyWithoutPlaintextRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private SdkInternalMap<String, String> encryptionContext;
    private String keySpec;
    private Integer numberOfBytes;
    private SdkInternalList<String> grantTokens;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GenerateDataKeyWithoutPlaintextRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
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

    public GenerateDataKeyWithoutPlaintextRequest withEncryptionContext(Map<String, String> encryptionContext) {
        this.setEncryptionContext(encryptionContext);
        return this;
    }

    public GenerateDataKeyWithoutPlaintextRequest addEncryptionContextEntry(String key, String value) {
        if (null == this.encryptionContext) {
            this.encryptionContext = new SdkInternalMap();
        }
        if (this.encryptionContext.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        }
        this.encryptionContext.put(key, value);
        return this;
    }

    public GenerateDataKeyWithoutPlaintextRequest clearEncryptionContextEntries() {
        this.encryptionContext = null;
        return this;
    }

    public void setKeySpec(String keySpec) {
        this.keySpec = keySpec;
    }

    public String getKeySpec() {
        return this.keySpec;
    }

    public GenerateDataKeyWithoutPlaintextRequest withKeySpec(String keySpec) {
        this.setKeySpec(keySpec);
        return this;
    }

    public void setKeySpec(DataKeySpec keySpec) {
        this.withKeySpec(keySpec);
    }

    public GenerateDataKeyWithoutPlaintextRequest withKeySpec(DataKeySpec keySpec) {
        this.keySpec = keySpec.toString();
        return this;
    }

    public void setNumberOfBytes(Integer numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }

    public Integer getNumberOfBytes() {
        return this.numberOfBytes;
    }

    public GenerateDataKeyWithoutPlaintextRequest withNumberOfBytes(Integer numberOfBytes) {
        this.setNumberOfBytes(numberOfBytes);
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

    public GenerateDataKeyWithoutPlaintextRequest withGrantTokens(String ... grantTokens) {
        if (this.grantTokens == null) {
            this.setGrantTokens(new SdkInternalList<String>(grantTokens.length));
        }
        for (String ele : grantTokens) {
            this.grantTokens.add(ele);
        }
        return this;
    }

    public GenerateDataKeyWithoutPlaintextRequest withGrantTokens(Collection<String> grantTokens) {
        this.setGrantTokens(grantTokens);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getEncryptionContext() != null) {
            sb.append("EncryptionContext: ").append(this.getEncryptionContext()).append(",");
        }
        if (this.getKeySpec() != null) {
            sb.append("KeySpec: ").append(this.getKeySpec()).append(",");
        }
        if (this.getNumberOfBytes() != null) {
            sb.append("NumberOfBytes: ").append(this.getNumberOfBytes()).append(",");
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
        if (!(obj instanceof GenerateDataKeyWithoutPlaintextRequest)) {
            return false;
        }
        GenerateDataKeyWithoutPlaintextRequest other = (GenerateDataKeyWithoutPlaintextRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getEncryptionContext() == null ^ this.getEncryptionContext() == null) {
            return false;
        }
        if (other.getEncryptionContext() != null && !other.getEncryptionContext().equals(this.getEncryptionContext())) {
            return false;
        }
        if (other.getKeySpec() == null ^ this.getKeySpec() == null) {
            return false;
        }
        if (other.getKeySpec() != null && !other.getKeySpec().equals(this.getKeySpec())) {
            return false;
        }
        if (other.getNumberOfBytes() == null ^ this.getNumberOfBytes() == null) {
            return false;
        }
        if (other.getNumberOfBytes() != null && !other.getNumberOfBytes().equals(this.getNumberOfBytes())) {
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
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptionContext() == null ? 0 : this.getEncryptionContext().hashCode());
        hashCode = 31 * hashCode + (this.getKeySpec() == null ? 0 : this.getKeySpec().hashCode());
        hashCode = 31 * hashCode + (this.getNumberOfBytes() == null ? 0 : this.getNumberOfBytes().hashCode());
        hashCode = 31 * hashCode + (this.getGrantTokens() == null ? 0 : this.getGrantTokens().hashCode());
        return hashCode;
    }

    @Override
    public GenerateDataKeyWithoutPlaintextRequest clone() {
        return (GenerateDataKeyWithoutPlaintextRequest)super.clone();
    }
}

