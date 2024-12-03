/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import java.io.Serializable;

public class EnableKeyRotationResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
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
        if (!(obj instanceof EnableKeyRotationResult)) {
            return false;
        }
        EnableKeyRotationResult other = (EnableKeyRotationResult)obj;
        return true;
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        return hashCode;
    }

    public EnableKeyRotationResult clone() {
        try {
            return (EnableKeyRotationResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

