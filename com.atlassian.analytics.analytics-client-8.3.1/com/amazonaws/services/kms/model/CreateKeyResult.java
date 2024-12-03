/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.KeyMetadata;
import java.io.Serializable;

public class CreateKeyResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private KeyMetadata keyMetadata;

    public void setKeyMetadata(KeyMetadata keyMetadata) {
        this.keyMetadata = keyMetadata;
    }

    public KeyMetadata getKeyMetadata() {
        return this.keyMetadata;
    }

    public CreateKeyResult withKeyMetadata(KeyMetadata keyMetadata) {
        this.setKeyMetadata(keyMetadata);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyMetadata() != null) {
            sb.append("KeyMetadata: ").append(this.getKeyMetadata());
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
        if (!(obj instanceof CreateKeyResult)) {
            return false;
        }
        CreateKeyResult other = (CreateKeyResult)obj;
        if (other.getKeyMetadata() == null ^ this.getKeyMetadata() == null) {
            return false;
        }
        return other.getKeyMetadata() == null || other.getKeyMetadata().equals(this.getKeyMetadata());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyMetadata() == null ? 0 : this.getKeyMetadata().hashCode());
        return hashCode;
    }

    public CreateKeyResult clone() {
        try {
            return (CreateKeyResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

