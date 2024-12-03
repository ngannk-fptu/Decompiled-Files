/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ServerSideEncryptionByDefault;
import java.io.Serializable;

public class ServerSideEncryptionRule
implements Serializable,
Cloneable {
    private ServerSideEncryptionByDefault applyServerSideEncryptionByDefault;
    private Boolean bucketKeyEnabled;

    public ServerSideEncryptionByDefault getApplyServerSideEncryptionByDefault() {
        return this.applyServerSideEncryptionByDefault;
    }

    public void setApplyServerSideEncryptionByDefault(ServerSideEncryptionByDefault applyServerSideEncryptionByDefault) {
        this.applyServerSideEncryptionByDefault = applyServerSideEncryptionByDefault;
    }

    public ServerSideEncryptionRule withApplyServerSideEncryptionByDefault(ServerSideEncryptionByDefault applyServerSideEncryptionByDefault) {
        this.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
        return this;
    }

    public Boolean getBucketKeyEnabled() {
        return this.bucketKeyEnabled;
    }

    public void setBucketKeyEnabled(Boolean bucketKeyEnabled) {
        this.bucketKeyEnabled = bucketKeyEnabled;
    }

    public ServerSideEncryptionRule withBucketKeyEnabled(Boolean bucketKeyEnabled) {
        this.setBucketKeyEnabled(bucketKeyEnabled);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getApplyServerSideEncryptionByDefault() != null) {
            sb.append("ApplyServerSideEncryptionByDefault: ").append(this.getApplyServerSideEncryptionByDefault()).append(",");
            sb.append("BucketKeyEnabled: ").append(this.getBucketKeyEnabled()).append(",");
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
        if (!(obj instanceof ServerSideEncryptionRule)) {
            return false;
        }
        ServerSideEncryptionRule other = (ServerSideEncryptionRule)obj;
        if (other.getApplyServerSideEncryptionByDefault() == null ^ this.getApplyServerSideEncryptionByDefault() == null) {
            return false;
        }
        if (other.getApplyServerSideEncryptionByDefault() != null && !other.getApplyServerSideEncryptionByDefault().equals(this.getApplyServerSideEncryptionByDefault())) {
            return false;
        }
        if (other.getBucketKeyEnabled() == null ^ this.getBucketKeyEnabled() == null) {
            return false;
        }
        return other.getBucketKeyEnabled() == null || other.getBucketKeyEnabled().booleanValue() == this.getBucketKeyEnabled().booleanValue();
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getApplyServerSideEncryptionByDefault() == null ? 0 : this.getApplyServerSideEncryptionByDefault().hashCode());
        hashCode = 31 * hashCode + (this.getBucketKeyEnabled() == null ? 0 : (this.getBucketKeyEnabled() != false ? 1 : 2));
        return hashCode;
    }

    public ServerSideEncryptionRule clone() {
        try {
            return (ServerSideEncryptionRule)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

