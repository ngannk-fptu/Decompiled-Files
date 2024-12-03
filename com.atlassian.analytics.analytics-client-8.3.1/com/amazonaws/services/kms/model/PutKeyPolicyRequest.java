/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class PutKeyPolicyRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private String policyName;
    private String policy;
    private Boolean bypassPolicyLockoutSafetyCheck;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public PutKeyPolicyRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyName() {
        return this.policyName;
    }

    public PutKeyPolicyRequest withPolicyName(String policyName) {
        this.setPolicyName(policyName);
        return this;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getPolicy() {
        return this.policy;
    }

    public PutKeyPolicyRequest withPolicy(String policy) {
        this.setPolicy(policy);
        return this;
    }

    public void setBypassPolicyLockoutSafetyCheck(Boolean bypassPolicyLockoutSafetyCheck) {
        this.bypassPolicyLockoutSafetyCheck = bypassPolicyLockoutSafetyCheck;
    }

    public Boolean getBypassPolicyLockoutSafetyCheck() {
        return this.bypassPolicyLockoutSafetyCheck;
    }

    public PutKeyPolicyRequest withBypassPolicyLockoutSafetyCheck(Boolean bypassPolicyLockoutSafetyCheck) {
        this.setBypassPolicyLockoutSafetyCheck(bypassPolicyLockoutSafetyCheck);
        return this;
    }

    public Boolean isBypassPolicyLockoutSafetyCheck() {
        return this.bypassPolicyLockoutSafetyCheck;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getPolicyName() != null) {
            sb.append("PolicyName: ").append(this.getPolicyName()).append(",");
        }
        if (this.getPolicy() != null) {
            sb.append("Policy: ").append(this.getPolicy()).append(",");
        }
        if (this.getBypassPolicyLockoutSafetyCheck() != null) {
            sb.append("BypassPolicyLockoutSafetyCheck: ").append(this.getBypassPolicyLockoutSafetyCheck());
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
        if (!(obj instanceof PutKeyPolicyRequest)) {
            return false;
        }
        PutKeyPolicyRequest other = (PutKeyPolicyRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getPolicyName() == null ^ this.getPolicyName() == null) {
            return false;
        }
        if (other.getPolicyName() != null && !other.getPolicyName().equals(this.getPolicyName())) {
            return false;
        }
        if (other.getPolicy() == null ^ this.getPolicy() == null) {
            return false;
        }
        if (other.getPolicy() != null && !other.getPolicy().equals(this.getPolicy())) {
            return false;
        }
        if (other.getBypassPolicyLockoutSafetyCheck() == null ^ this.getBypassPolicyLockoutSafetyCheck() == null) {
            return false;
        }
        return other.getBypassPolicyLockoutSafetyCheck() == null || other.getBypassPolicyLockoutSafetyCheck().equals(this.getBypassPolicyLockoutSafetyCheck());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getPolicyName() == null ? 0 : this.getPolicyName().hashCode());
        hashCode = 31 * hashCode + (this.getPolicy() == null ? 0 : this.getPolicy().hashCode());
        hashCode = 31 * hashCode + (this.getBypassPolicyLockoutSafetyCheck() == null ? 0 : this.getBypassPolicyLockoutSafetyCheck().hashCode());
        return hashCode;
    }

    @Override
    public PutKeyPolicyRequest clone() {
        return (PutKeyPolicyRequest)super.clone();
    }
}

