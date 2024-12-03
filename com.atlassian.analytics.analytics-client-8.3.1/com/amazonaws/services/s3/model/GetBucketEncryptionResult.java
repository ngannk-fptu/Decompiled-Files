/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ServerSideEncryptionConfiguration;
import java.io.Serializable;

public class GetBucketEncryptionResult
implements Serializable,
Cloneable {
    private ServerSideEncryptionConfiguration serverSideEncryptionConfiguration;

    public ServerSideEncryptionConfiguration getServerSideEncryptionConfiguration() {
        return this.serverSideEncryptionConfiguration;
    }

    public void setServerSideEncryptionConfiguration(ServerSideEncryptionConfiguration serverSideEncryptionConfiguration) {
        this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
    }

    public GetBucketEncryptionResult withServerSideEncryptionConfiguration(ServerSideEncryptionConfiguration serverSideEncryptionConfiguration) {
        this.setServerSideEncryptionConfiguration(serverSideEncryptionConfiguration);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getServerSideEncryptionConfiguration() != null) {
            sb.append("ServerSideEncryptionConfiguration: ").append(this.getServerSideEncryptionConfiguration()).append(",");
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
        if (!(obj instanceof GetBucketEncryptionResult)) {
            return false;
        }
        GetBucketEncryptionResult other = (GetBucketEncryptionResult)obj;
        if (other.getServerSideEncryptionConfiguration() == null ^ this.getServerSideEncryptionConfiguration() == null) {
            return false;
        }
        return other.getServerSideEncryptionConfiguration() == null || other.getServerSideEncryptionConfiguration().equals(this.getServerSideEncryptionConfiguration());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getServerSideEncryptionConfiguration() == null ? 0 : this.getServerSideEncryptionConfiguration().hashCode());
        return hashCode;
    }

    public GetBucketEncryptionResult clone() {
        try {
            return (GetBucketEncryptionResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

