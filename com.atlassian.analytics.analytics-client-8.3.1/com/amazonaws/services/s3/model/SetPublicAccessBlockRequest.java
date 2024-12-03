/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import java.io.Serializable;

public class SetPublicAccessBlockRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable,
ExpectedBucketOwnerRequest {
    private String bucketName;
    private PublicAccessBlockConfiguration publicAccessBlockConfiguration;
    private String expectedBucketOwner;

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public SetPublicAccessBlockRequest withExpectedBucketOwner(String expectedBucketOwner) {
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

    public SetPublicAccessBlockRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    public PublicAccessBlockConfiguration getPublicAccessBlockConfiguration() {
        return this.publicAccessBlockConfiguration;
    }

    public void setPublicAccessBlockConfiguration(PublicAccessBlockConfiguration publicAccessBlockConfiguration) {
        this.publicAccessBlockConfiguration = publicAccessBlockConfiguration;
    }

    public SetPublicAccessBlockRequest withPublicAccessBlockConfiguration(PublicAccessBlockConfiguration publicAccessBlockConfiguration) {
        this.setPublicAccessBlockConfiguration(publicAccessBlockConfiguration);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SetPublicAccessBlockRequest that = (SetPublicAccessBlockRequest)o;
        if (this.bucketName != null ? !this.bucketName.equals(that.bucketName) : that.bucketName != null) {
            return false;
        }
        return this.publicAccessBlockConfiguration != null ? this.publicAccessBlockConfiguration.equals(that.publicAccessBlockConfiguration) : that.publicAccessBlockConfiguration == null;
    }

    public int hashCode() {
        int result = this.bucketName != null ? this.bucketName.hashCode() : 0;
        result = 31 * result + (this.publicAccessBlockConfiguration != null ? this.publicAccessBlockConfiguration.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getBucketName() != null) {
            sb.append("BucketName: ").append(this.getBucketName()).append(",");
        }
        if (this.getPublicAccessBlockConfiguration() != null) {
            sb.append("PublicAccessBlockConfiguration: ").append(this.getPublicAccessBlockConfiguration()).append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public SetPublicAccessBlockRequest clone() {
        return (SetPublicAccessBlockRequest)super.clone();
    }
}

