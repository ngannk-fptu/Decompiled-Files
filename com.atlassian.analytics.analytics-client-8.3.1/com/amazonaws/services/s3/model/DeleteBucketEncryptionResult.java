/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import java.io.Serializable;

public class DeleteBucketEncryptionResult
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
        return obj instanceof DeleteBucketEncryptionResult;
    }

    public int hashCode() {
        int hashCode = 1;
        return hashCode;
    }

    public DeleteBucketEncryptionResult clone() {
        try {
            return (DeleteBucketEncryptionResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

