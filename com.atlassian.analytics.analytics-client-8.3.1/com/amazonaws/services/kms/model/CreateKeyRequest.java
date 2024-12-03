/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.CustomerMasterKeySpec;
import com.amazonaws.services.kms.model.KeySpec;
import com.amazonaws.services.kms.model.KeyUsageType;
import com.amazonaws.services.kms.model.OriginType;
import com.amazonaws.services.kms.model.Tag;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class CreateKeyRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String policy;
    private String description;
    private String keyUsage;
    @Deprecated
    private String customerMasterKeySpec;
    private String keySpec;
    private String origin;
    private String customKeyStoreId;
    private Boolean bypassPolicyLockoutSafetyCheck;
    private SdkInternalList<Tag> tags;
    private Boolean multiRegion;

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getPolicy() {
        return this.policy;
    }

    public CreateKeyRequest withPolicy(String policy) {
        this.setPolicy(policy);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public CreateKeyRequest withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setKeyUsage(String keyUsage) {
        this.keyUsage = keyUsage;
    }

    public String getKeyUsage() {
        return this.keyUsage;
    }

    public CreateKeyRequest withKeyUsage(String keyUsage) {
        this.setKeyUsage(keyUsage);
        return this;
    }

    public void setKeyUsage(KeyUsageType keyUsage) {
        this.withKeyUsage(keyUsage);
    }

    public CreateKeyRequest withKeyUsage(KeyUsageType keyUsage) {
        this.keyUsage = keyUsage.toString();
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
    public CreateKeyRequest withCustomerMasterKeySpec(String customerMasterKeySpec) {
        this.setCustomerMasterKeySpec(customerMasterKeySpec);
        return this;
    }

    @Deprecated
    public void setCustomerMasterKeySpec(CustomerMasterKeySpec customerMasterKeySpec) {
        this.withCustomerMasterKeySpec(customerMasterKeySpec);
    }

    @Deprecated
    public CreateKeyRequest withCustomerMasterKeySpec(CustomerMasterKeySpec customerMasterKeySpec) {
        this.customerMasterKeySpec = customerMasterKeySpec.toString();
        return this;
    }

    public void setKeySpec(String keySpec) {
        this.keySpec = keySpec;
    }

    public String getKeySpec() {
        return this.keySpec;
    }

    public CreateKeyRequest withKeySpec(String keySpec) {
        this.setKeySpec(keySpec);
        return this;
    }

    public void setKeySpec(KeySpec keySpec) {
        this.withKeySpec(keySpec);
    }

    public CreateKeyRequest withKeySpec(KeySpec keySpec) {
        this.keySpec = keySpec.toString();
        return this;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrigin() {
        return this.origin;
    }

    public CreateKeyRequest withOrigin(String origin) {
        this.setOrigin(origin);
        return this;
    }

    public void setOrigin(OriginType origin) {
        this.withOrigin(origin);
    }

    public CreateKeyRequest withOrigin(OriginType origin) {
        this.origin = origin.toString();
        return this;
    }

    public void setCustomKeyStoreId(String customKeyStoreId) {
        this.customKeyStoreId = customKeyStoreId;
    }

    public String getCustomKeyStoreId() {
        return this.customKeyStoreId;
    }

    public CreateKeyRequest withCustomKeyStoreId(String customKeyStoreId) {
        this.setCustomKeyStoreId(customKeyStoreId);
        return this;
    }

    public void setBypassPolicyLockoutSafetyCheck(Boolean bypassPolicyLockoutSafetyCheck) {
        this.bypassPolicyLockoutSafetyCheck = bypassPolicyLockoutSafetyCheck;
    }

    public Boolean getBypassPolicyLockoutSafetyCheck() {
        return this.bypassPolicyLockoutSafetyCheck;
    }

    public CreateKeyRequest withBypassPolicyLockoutSafetyCheck(Boolean bypassPolicyLockoutSafetyCheck) {
        this.setBypassPolicyLockoutSafetyCheck(bypassPolicyLockoutSafetyCheck);
        return this;
    }

    public Boolean isBypassPolicyLockoutSafetyCheck() {
        return this.bypassPolicyLockoutSafetyCheck;
    }

    public List<Tag> getTags() {
        if (this.tags == null) {
            this.tags = new SdkInternalList();
        }
        return this.tags;
    }

    public void setTags(Collection<Tag> tags) {
        if (tags == null) {
            this.tags = null;
            return;
        }
        this.tags = new SdkInternalList<Tag>(tags);
    }

    public CreateKeyRequest withTags(Tag ... tags) {
        if (this.tags == null) {
            this.setTags(new SdkInternalList<Tag>(tags.length));
        }
        for (Tag ele : tags) {
            this.tags.add(ele);
        }
        return this;
    }

    public CreateKeyRequest withTags(Collection<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public void setMultiRegion(Boolean multiRegion) {
        this.multiRegion = multiRegion;
    }

    public Boolean getMultiRegion() {
        return this.multiRegion;
    }

    public CreateKeyRequest withMultiRegion(Boolean multiRegion) {
        this.setMultiRegion(multiRegion);
        return this;
    }

    public Boolean isMultiRegion() {
        return this.multiRegion;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getPolicy() != null) {
            sb.append("Policy: ").append(this.getPolicy()).append(",");
        }
        if (this.getDescription() != null) {
            sb.append("Description: ").append(this.getDescription()).append(",");
        }
        if (this.getKeyUsage() != null) {
            sb.append("KeyUsage: ").append(this.getKeyUsage()).append(",");
        }
        if (this.getCustomerMasterKeySpec() != null) {
            sb.append("CustomerMasterKeySpec: ").append(this.getCustomerMasterKeySpec()).append(",");
        }
        if (this.getKeySpec() != null) {
            sb.append("KeySpec: ").append(this.getKeySpec()).append(",");
        }
        if (this.getOrigin() != null) {
            sb.append("Origin: ").append(this.getOrigin()).append(",");
        }
        if (this.getCustomKeyStoreId() != null) {
            sb.append("CustomKeyStoreId: ").append(this.getCustomKeyStoreId()).append(",");
        }
        if (this.getBypassPolicyLockoutSafetyCheck() != null) {
            sb.append("BypassPolicyLockoutSafetyCheck: ").append(this.getBypassPolicyLockoutSafetyCheck()).append(",");
        }
        if (this.getTags() != null) {
            sb.append("Tags: ").append(this.getTags()).append(",");
        }
        if (this.getMultiRegion() != null) {
            sb.append("MultiRegion: ").append(this.getMultiRegion());
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
        if (!(obj instanceof CreateKeyRequest)) {
            return false;
        }
        CreateKeyRequest other = (CreateKeyRequest)obj;
        if (other.getPolicy() == null ^ this.getPolicy() == null) {
            return false;
        }
        if (other.getPolicy() != null && !other.getPolicy().equals(this.getPolicy())) {
            return false;
        }
        if (other.getDescription() == null ^ this.getDescription() == null) {
            return false;
        }
        if (other.getDescription() != null && !other.getDescription().equals(this.getDescription())) {
            return false;
        }
        if (other.getKeyUsage() == null ^ this.getKeyUsage() == null) {
            return false;
        }
        if (other.getKeyUsage() != null && !other.getKeyUsage().equals(this.getKeyUsage())) {
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
        if (other.getOrigin() == null ^ this.getOrigin() == null) {
            return false;
        }
        if (other.getOrigin() != null && !other.getOrigin().equals(this.getOrigin())) {
            return false;
        }
        if (other.getCustomKeyStoreId() == null ^ this.getCustomKeyStoreId() == null) {
            return false;
        }
        if (other.getCustomKeyStoreId() != null && !other.getCustomKeyStoreId().equals(this.getCustomKeyStoreId())) {
            return false;
        }
        if (other.getBypassPolicyLockoutSafetyCheck() == null ^ this.getBypassPolicyLockoutSafetyCheck() == null) {
            return false;
        }
        if (other.getBypassPolicyLockoutSafetyCheck() != null && !other.getBypassPolicyLockoutSafetyCheck().equals(this.getBypassPolicyLockoutSafetyCheck())) {
            return false;
        }
        if (other.getTags() == null ^ this.getTags() == null) {
            return false;
        }
        if (other.getTags() != null && !other.getTags().equals(this.getTags())) {
            return false;
        }
        if (other.getMultiRegion() == null ^ this.getMultiRegion() == null) {
            return false;
        }
        return other.getMultiRegion() == null || other.getMultiRegion().equals(this.getMultiRegion());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getPolicy() == null ? 0 : this.getPolicy().hashCode());
        hashCode = 31 * hashCode + (this.getDescription() == null ? 0 : this.getDescription().hashCode());
        hashCode = 31 * hashCode + (this.getKeyUsage() == null ? 0 : this.getKeyUsage().hashCode());
        hashCode = 31 * hashCode + (this.getCustomerMasterKeySpec() == null ? 0 : this.getCustomerMasterKeySpec().hashCode());
        hashCode = 31 * hashCode + (this.getKeySpec() == null ? 0 : this.getKeySpec().hashCode());
        hashCode = 31 * hashCode + (this.getOrigin() == null ? 0 : this.getOrigin().hashCode());
        hashCode = 31 * hashCode + (this.getCustomKeyStoreId() == null ? 0 : this.getCustomKeyStoreId().hashCode());
        hashCode = 31 * hashCode + (this.getBypassPolicyLockoutSafetyCheck() == null ? 0 : this.getBypassPolicyLockoutSafetyCheck().hashCode());
        hashCode = 31 * hashCode + (this.getTags() == null ? 0 : this.getTags().hashCode());
        hashCode = 31 * hashCode + (this.getMultiRegion() == null ? 0 : this.getMultiRegion().hashCode());
        return hashCode;
    }

    @Override
    public CreateKeyRequest clone() {
        return (CreateKeyRequest)super.clone();
    }
}

