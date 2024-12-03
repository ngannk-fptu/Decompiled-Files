/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import com.amazonaws.services.s3.model.ServerSideEncryptionConfiguration;
import java.io.Serializable;

public class SetBucketEncryptionRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable,
ExpectedBucketOwnerRequest {
    private String bucketName;
    private ServerSideEncryptionConfiguration serverSideEncryptionConfiguration;
    private String expectedBucketOwner;

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public SetBucketEncryptionRequest withExpectedBucketOwner(String expectedBucketOwner) {
        this.expectedBucketOwner = expectedBucketOwner;
        return this;
    }

    @Override
    public void setExpectedBucketOwner(String expectedBucketOwner) {
        this.withExpectedBucketOwner(expectedBucketOwner);
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public SetBucketEncryptionRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    public ServerSideEncryptionConfiguration getServerSideEncryptionConfiguration() {
        return this.serverSideEncryptionConfiguration;
    }

    public void setServerSideEncryptionConfiguration(ServerSideEncryptionConfiguration serverSideEncryptionConfiguration) {
        this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
    }

    public SetBucketEncryptionRequest withServerSideEncryptionConfiguration(ServerSideEncryptionConfiguration serverSideEncryptionConfiguration) {
        this.setServerSideEncryptionConfiguration(serverSideEncryptionConfiguration);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getBucketName() != null) {
            sb.append("BucketName: ").append(this.getBucketName()).append(",");
        }
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
        if (!(obj instanceof SetBucketEncryptionRequest)) {
            return false;
        }
        SetBucketEncryptionRequest other = (SetBucketEncryptionRequest)obj;
        if (other.getBucketName() == null ^ this.getBucketName() == null) {
            return false;
        }
        if (other.getBucketName() != null && !other.getBucketName().equals(this.getBucketName())) {
            return false;
        }
        if (other.getServerSideEncryptionConfiguration() == null ^ this.getServerSideEncryptionConfiguration() == null) {
            return false;
        }
        return other.getServerSideEncryptionConfiguration() == null || other.getServerSideEncryptionConfiguration().equals(this.getServerSideEncryptionConfiguration());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getBucketName() == null ? 0 : this.getBucketName().hashCode());
        hashCode = 31 * hashCode + (this.getServerSideEncryptionConfiguration() == null ? 0 : this.getServerSideEncryptionConfiguration().hashCode());
        return hashCode;
    }

    @Override
    public SetBucketEncryptionRequest clone() {
        return (SetBucketEncryptionRequest)super.clone();
    }
}

