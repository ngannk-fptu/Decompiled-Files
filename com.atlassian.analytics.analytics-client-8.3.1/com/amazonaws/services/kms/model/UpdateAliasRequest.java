/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class UpdateAliasRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String aliasName;
    private String targetKeyId;

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public UpdateAliasRequest withAliasName(String aliasName) {
        this.setAliasName(aliasName);
        return this;
    }

    public void setTargetKeyId(String targetKeyId) {
        this.targetKeyId = targetKeyId;
    }

    public String getTargetKeyId() {
        return this.targetKeyId;
    }

    public UpdateAliasRequest withTargetKeyId(String targetKeyId) {
        this.setTargetKeyId(targetKeyId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getAliasName() != null) {
            sb.append("AliasName: ").append(this.getAliasName()).append(",");
        }
        if (this.getTargetKeyId() != null) {
            sb.append("TargetKeyId: ").append(this.getTargetKeyId());
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
        if (!(obj instanceof UpdateAliasRequest)) {
            return false;
        }
        UpdateAliasRequest other = (UpdateAliasRequest)obj;
        if (other.getAliasName() == null ^ this.getAliasName() == null) {
            return false;
        }
        if (other.getAliasName() != null && !other.getAliasName().equals(this.getAliasName())) {
            return false;
        }
        if (other.getTargetKeyId() == null ^ this.getTargetKeyId() == null) {
            return false;
        }
        return other.getTargetKeyId() == null || other.getTargetKeyId().equals(this.getTargetKeyId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getAliasName() == null ? 0 : this.getAliasName().hashCode());
        hashCode = 31 * hashCode + (this.getTargetKeyId() == null ? 0 : this.getTargetKeyId().hashCode());
        return hashCode;
    }

    @Override
    public UpdateAliasRequest clone() {
        return (UpdateAliasRequest)super.clone();
    }
}

