/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class GetKeyPolicyRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private String policyName;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GetKeyPolicyRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyName() {
        return this.policyName;
    }

    public GetKeyPolicyRequest withPolicyName(String policyName) {
        this.setPolicyName(policyName);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getPolicyName() != null) {
            sb.append("PolicyName: ").append(this.getPolicyName());
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
        if (!(obj instanceof GetKeyPolicyRequest)) {
            return false;
        }
        GetKeyPolicyRequest other = (GetKeyPolicyRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getPolicyName() == null ^ this.getPolicyName() == null) {
            return false;
        }
        return other.getPolicyName() == null || other.getPolicyName().equals(this.getPolicyName());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getPolicyName() == null ? 0 : this.getPolicyName().hashCode());
        return hashCode;
    }

    @Override
    public GetKeyPolicyRequest clone() {
        return (GetKeyPolicyRequest)super.clone();
    }
}

