/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.KeyMetadata;
import java.io.Serializable;

public class DescribeKeyResult
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

    public DescribeKeyResult withKeyMetadata(KeyMetadata keyMetadata) {
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
        if (!(obj instanceof DescribeKeyResult)) {
            return false;
        }
        DescribeKeyResult other = (DescribeKeyResult)obj;
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

    public DescribeKeyResult clone() {
        try {
            return (DescribeKeyResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

