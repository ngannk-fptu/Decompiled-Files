/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import java.io.Serializable;

public class GetKeyPolicyResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String policy;

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getPolicy() {
        return this.policy;
    }

    public GetKeyPolicyResult withPolicy(String policy) {
        this.setPolicy(policy);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getPolicy() != null) {
            sb.append("Policy: ").append(this.getPolicy());
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
        if (!(obj instanceof GetKeyPolicyResult)) {
            return false;
        }
        GetKeyPolicyResult other = (GetKeyPolicyResult)obj;
        if (other.getPolicy() == null ^ this.getPolicy() == null) {
            return false;
        }
        return other.getPolicy() == null || other.getPolicy().equals(this.getPolicy());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getPolicy() == null ? 0 : this.getPolicy().hashCode());
        return hashCode;
    }

    public GetKeyPolicyResult clone() {
        try {
            return (GetKeyPolicyResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

