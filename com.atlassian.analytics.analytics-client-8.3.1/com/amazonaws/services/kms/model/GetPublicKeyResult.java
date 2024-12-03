/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.CustomerMasterKeySpec;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import com.amazonaws.services.kms.model.KeySpec;
import com.amazonaws.services.kms.model.KeyUsageType;
import com.amazonaws.services.kms.model.SigningAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

public class GetPublicKeyResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String keyId;
    private ByteBuffer publicKey;
    @Deprecated
    private String customerMasterKeySpec;
    private String keySpec;
    private String keyUsage;
    private SdkInternalList<String> encryptionAlgorithms;
    private SdkInternalList<String> signingAlgorithms;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GetPublicKeyResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setPublicKey(ByteBuffer publicKey) {
        this.publicKey = publicKey;
    }

    public ByteBuffer getPublicKey() {
        return this.publicKey;
    }

    public GetPublicKeyResult withPublicKey(ByteBuffer publicKey) {
        this.setPublicKey(publicKey);
        return this;
    }

    @Deprecated
    public void setCustomerMasterKeySpec(String customerMasterKeySpec) {
        this.customerMasterKeySpec = customerMasterKeySpec;
    }

    @Deprecated
    public String getCustomerMasterKeySpec() {
        return this.customerMasterKeySpec;
    }

    @Deprecated
    public GetPublicKeyResult withCustomerMasterKeySpec(String customerMasterKeySpec) {
        this.setCustomerMasterKeySpec(customerMasterKeySpec);
        return this;
    }

    @Deprecated
    public GetPublicKeyResult withCustomerMasterKeySpec(CustomerMasterKeySpec customerMasterKeySpec) {
        this.customerMasterKeySpec = customerMasterKeySpec.toString();
        return this;
    }

    public void setKeySpec(String keySpec) {
        this.keySpec = keySpec;
    }

    public String getKeySpec() {
        return this.keySpec;
    }

    public GetPublicKeyResult withKeySpec(String keySpec) {
        this.setKeySpec(keySpec);
        return this;
    }

    public GetPublicKeyResult withKeySpec(KeySpec keySpec) {
        this.keySpec = keySpec.toString();
        return this;
    }

    public void setKeyUsage(String keyUsage) {
        this.keyUsage = keyUsage;
    }

    public String getKeyUsage() {
        return this.keyUsage;
    }

    public GetPublicKeyResult withKeyUsage(String keyUsage) {
        this.setKeyUsage(keyUsage);
        return this;
    }

    public GetPublicKeyResult withKeyUsage(KeyUsageType keyUsage) {
        this.keyUsage = keyUsage.toString();
        return this;
    }

    public List<String> getEncryptionAlgorithms() {
        if (this.encryptionAlgorithms == null) {
            this.encryptionAlgorithms = new SdkInternalList();
        }
        return this.encryptionAlgorithms;
    }

    public void setEncryptionAlgorithms(Collection<String> encryptionAlgorithms) {
        if (encryptionAlgorithms == null) {
            this.encryptionAlgorithms = null;
            return;
        }
        this.encryptionAlgorithms = new SdkInternalList<String>(encryptionAlgorithms);
    }

    public GetPublicKeyResult withEncryptionAlgorithms(String ... encryptionAlgorithms) {
        if (this.encryptionAlgorithms == null) {
            this.setEncryptionAlgorithms(new SdkInternalList<String>(encryptionAlgorithms.length));
        }
        for (String ele : encryptionAlgorithms) {
            this.encryptionAlgorithms.add(ele);
        }
        return this;
    }

    public GetPublicKeyResult withEncryptionAlgorithms(Collection<String> encryptionAlgorithms) {
        this.setEncryptionAlgorithms(encryptionAlgorithms);
        return this;
    }

    public GetPublicKeyResult withEncryptionAlgorithms(EncryptionAlgorithmSpec ... encryptionAlgorithms) {
        SdkInternalList<String> encryptionAlgorithmsCopy = new SdkInternalList<String>(encryptionAlgorithms.length);
        for (EncryptionAlgorithmSpec value : encryptionAlgorithms) {
            encryptionAlgorithmsCopy.add(value.toString());
        }
        if (this.getEncryptionAlgorithms() == null) {
            this.setEncryptionAlgorithms(encryptionAlgorithmsCopy);
        } else {
            this.getEncryptionAlgorithms().addAll(encryptionAlgorithmsCopy);
        }
        return this;
    }

    public List<String> getSigningAlgorithms() {
        if (this.signingAlgorithms == null) {
            this.signingAlgorithms = new SdkInternalList();
        }
        return this.signingAlgorithms;
    }

    public void setSigningAlgorithms(Collection<String> signingAlgorithms) {
        if (signingAlgorithms == null) {
            this.signingAlgorithms = null;
            return;
        }
        this.signingAlgorithms = new SdkInternalList<String>(signingAlgorithms);
    }

    public GetPublicKeyResult withSigningAlgorithms(String ... signingAlgorithms) {
        if (this.signingAlgorithms == null) {
            this.setSigningAlgorithms(new SdkInternalList<String>(signingAlgorithms.length));
        }
        for (String ele : signingAlgorithms) {
            this.signingAlgorithms.add(ele);
        }
        return this;
    }

    public GetPublicKeyResult withSigningAlgorithms(Collection<String> signingAlgorithms) {
        this.setSigningAlgorithms(signingAlgorithms);
        return this;
    }

    public GetPublicKeyResult withSigningAlgorithms(SigningAlgorithmSpec ... signingAlgorithms) {
        SdkInternalList<String> signingAlgorithmsCopy = new SdkInternalList<String>(signingAlgorithms.length);
        for (SigningAlgorithmSpec value : signingAlgorithms) {
            signingAlgorithmsCopy.add(value.toString());
        }
        if (this.getSigningAlgorithms() == null) {
            this.setSigningAlgorithms(signingAlgorithmsCopy);
        } else {
            this.getSigningAlgorithms().addAll(signingAlgorithmsCopy);
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getPublicKey() != null) {
            sb.append("PublicKey: ").append(this.getPublicKey()).append(",");
        }
        if (this.getCustomerMasterKeySpec() != null) {
            sb.append("CustomerMasterKeySpec: ").append(this.getCustomerMasterKeySpec()).append(",");
        }
        if (this.getKeySpec() != null) {
            sb.append("KeySpec: ").append(this.getKeySpec()).append(",");
        }
        if (this.getKeyUsage() != null) {
            sb.append("KeyUsage: ").append(this.getKeyUsage()).append(",");
        }
        if (this.getEncryptionAlgorithms() != null) {
            sb.append("EncryptionAlgorithms: ").append(this.getEncryptionAlgorithms()).append(",");
        }
        if (this.getSigningAlgorithms() != null) {
            sb.append("SigningAlgorithms: ").append(this.getSigningAlgorithms());
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
        if (!(obj instanceof GetPublicKeyResult)) {
            return false;
        }
        GetPublicKeyResult other = (GetPublicKeyResult)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getPublicKey() == null ^ this.getPublicKey() == null) {
            return false;
        }
        if (other.getPublicKey() != null && !other.getPublicKey().equals(this.getPublicKey())) {
            return false;
        }
        if (other.getCustomerMasterKeySpec() == null ^ this.getCustomerMasterKeySpec() == null) {
            return false;
        }
        if (other.getCustomerMasterKeySpec() != null && !other.getCustomerMasterKeySpec().equals(this.getCustomerMasterKeySpec())) {
            return false;
        }
        if (other.getKeySpec() == null ^ this.getKeySpec() == null) {
            return false;
        }
        if (other.getKeySpec() != null && !other.getKeySpec().equals(this.getKeySpec())) {
            return false;
        }
        if (other.getKeyUsage() == null ^ this.getKeyUsage() == null) {
            return false;
        }
        if (other.getKeyUsage() != null && !other.getKeyUsage().equals(this.getKeyUsage())) {
            return false;
        }
        if (other.getEncryptionAlgorithms() == null ^ this.getEncryptionAlgorithms() == null) {
            return false;
        }
        if (other.getEncryptionAlgorithms() != null && !other.getEncryptionAlgorithms().equals(this.getEncryptionAlgorithms())) {
            return false;
        }
        if (other.getSigningAlgorithms() == null ^ this.getSigningAlgorithms() == null) {
            return false;
        }
        return other.getSigningAlgorithms() == null || other.getSigningAlgorithms().equals(this.getSigningAlgorithms());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getPublicKey() == null ? 0 : this.getPublicKey().hashCode());
        hashCode = 31 * hashCode + (this.getCustomerMasterKeySpec() == null ? 0 : this.getCustomerMasterKeySpec().hashCode());
        hashCode = 31 * hashCode + (this.getKeySpec() == null ? 0 : this.getKeySpec().hashCode());
        hashCode = 31 * hashCode + (this.getKeyUsage() == null ? 0 : this.getKeyUsage().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptionAlgorithms() == null ? 0 : this.getEncryptionAlgorithms().hashCode());
        hashCode = 31 * hashCode + (this.getSigningAlgorithms() == null ? 0 : this.getSigningAlgorithms().hashCode());
        return hashCode;
    }

    public GetPublicKeyResult clone() {
        try {
            return (GetPublicKeyResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

