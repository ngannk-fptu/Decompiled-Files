/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import java.io.Serializable;

public class CreateCustomKeyStoreResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private String customKeyStoreId;

    public void setCustomKeyStoreId(String customKeyStoreId) {
        this.customKeyStoreId = customKeyStoreId;
    }

    public String getCustomKeyStoreId() {
        return this.customKeyStoreId;
    }

    public CreateCustomKeyStoreResult withCustomKeyStoreId(String customKeyStoreId) {
        this.setCustomKeyStoreId(customKeyStoreId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCustomKeyStoreId() != null) {
            sb.append("CustomKeyStoreId: ").append(this.getCustomKeyStoreId());
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
        if (!(obj instanceof CreateCustomKeyStoreResult)) {
            return false;
        }
        CreateCustomKeyStoreResult other = (CreateCustomKeyStoreResult)obj;
        if (other.getCustomKeyStoreId() == null ^ this.getCustomKeyStoreId() == null) {
            return false;
        }
        return other.getCustomKeyStoreId() == null || other.getCustomKeyStoreId().equals(this.getCustomKeyStoreId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCustomKeyStoreId() == null ? 0 : this.getCustomKeyStoreId().hashCode());
        return hashCode;
    }

    public CreateCustomKeyStoreResult clone() {
        try {
            return (CreateCustomKeyStoreResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

