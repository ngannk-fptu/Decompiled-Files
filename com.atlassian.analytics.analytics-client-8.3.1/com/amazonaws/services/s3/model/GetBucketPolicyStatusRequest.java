/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import java.io.Serializable;

public class GetBucketPolicyStatusRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable,
ExpectedBucketOwnerRequest {
    private String bucketName;
    private String expectedBucketOwner;

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public GetBucketPolicyStatusRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public GetBucketPolicyStatusRequest withExpectedBucketOwner(String expectedBucketOwner) {
        this.expectedBucketOwner = expectedBucketOwner;
        return this;
    }

    @Override
    public void setExpectedBucketOwner(String expectedBucketOwner) {
        this.withExpectedBucketOwner(expectedBucketOwner);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GetBucketPolicyStatusRequest that = (GetBucketPolicyStatusRequest)o;
        return this.bucketName != null ? this.bucketName.equals(that.bucketName) : that.bucketName == null;
    }

    public int hashCode() {
        return this.bucketName != null ? this.bucketName.hashCode() : 0;
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

    @Override
    public GetBucketPolicyStatusRequest clone() {
        return (GetBucketPolicyStatusRequest)super.clone();
    }
}

