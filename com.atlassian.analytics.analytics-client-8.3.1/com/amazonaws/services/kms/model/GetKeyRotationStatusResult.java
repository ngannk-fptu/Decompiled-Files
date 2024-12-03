/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import java.io.Serializable;

public class GetKeyRotationStatusResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private Boolean keyRotationEnabled;

    public void setKeyRotationEnabled(Boolean keyRotationEnabled) {
        this.keyRotationEnabled = keyRotationEnabled;
    }

    public Boolean getKeyRotationEnabled() {
        return this.keyRotationEnabled;
    }

    public GetKeyRotationStatusResult withKeyRotationEnabled(Boolean keyRotationEnabled) {
        this.setKeyRotationEnabled(keyRotationEnabled);
        return this;
    }

    public Boolean isKeyRotationEnabled() {
        return this.keyRotationEnabled;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyRotationEnabled() != null) {
            sb.append("KeyRotationEnabled: ").append(this.getKeyRotationEnabled());
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
        if (!(obj instanceof GetKeyRotationStatusResult)) {
            return false;
        }
        GetKeyRotationStatusResult other = (GetKeyRotationStatusResult)obj;
        if (other.getKeyRotationEnabled() == null ^ this.getKeyRotationEnabled() == null) {
            return false;
        }
        return other.getKeyRotationEnabled() == null || other.getKeyRotationEnabled().equals(this.getKeyRotationEnabled());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyRotationEnabled() == null ? 0 : this.getKeyRotationEnabled().hashCode());
        return hashCode;
    }

    public GetKeyRotationStatusResult clone() {
        try {
            return (GetKeyRotationStatusResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

