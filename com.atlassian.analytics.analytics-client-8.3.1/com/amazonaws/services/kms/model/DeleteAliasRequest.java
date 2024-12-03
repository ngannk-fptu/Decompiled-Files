/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class DeleteAliasRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String aliasName;

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public DeleteAliasRequest withAliasName(String aliasName) {
        this.setAliasName(aliasName);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getAliasName() != null) {
            sb.append("AliasName: ").append(this.getAliasName());
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
        if (!(obj instanceof DeleteAliasRequest)) {
            return false;
        }
        DeleteAliasRequest other = (DeleteAliasRequest)obj;
        if (other.getAliasName() == null ^ this.getAliasName() == null) {
            return false;
        }
        return other.getAliasName() == null || other.getAliasName().equals(this.getAliasName());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getAliasName() == null ? 0 : this.getAliasName().hashCode());
        return hashCode;
    }

    @Override
    public DeleteAliasRequest clone() {
        return (DeleteAliasRequest)super.clone();
    }
}

