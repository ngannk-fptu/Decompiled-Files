/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import java.io.Serializable;

public class GetBucketEncryptionRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable,
ExpectedBucketOwnerRequest {
    private String bucketName;
    private String expectedBucketOwner;

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public GetBucketEncryptionRequest withExpectedBucketOwner(String expectedBucketOwner) {
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

    public GetBucketEncryptionRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getBucketName() != null) {
            sb.append("BucketName: ").append(this.getBucketName()).append(",");
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
        if (!(obj instanceof GetBucketEncryptionRequest)) {
            return false;
        }
        GetBucketEncryptionRequest other = (GetBucketEncryptionRequest)obj;
        if (other.getBucketName() == null ^ this.getBucketName() == null) {
            return false;
        }
        return other.getBucketName() == null || other.getBucketName().equals(this.getBucketName());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getBucketName() == null ? 0 : this.getBucketName().hashCode());
        return hashCode;
    }

    @Override
    public GetBucketEncryptionRequest clone() {
        return (GetBucketEncryptionRequest)super.clone();
    }
}

