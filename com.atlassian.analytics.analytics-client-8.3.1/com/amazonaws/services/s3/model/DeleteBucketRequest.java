/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import com.amazonaws.services.s3.model.S3AccelerateUnsupported;
import java.io.Serializable;

public class DeleteBucketRequest
extends AmazonWebServiceRequest
implements Serializable,
S3AccelerateUnsupported,
ExpectedBucketOwnerRequest {
    private String bucketName;
    private String expectedBucketOwner;

    public DeleteBucketRequest(String bucketName) {
        this.setBucketName(bucketName);
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public DeleteBucketRequest withExpectedBucketOwner(String expectedBucketOwner) {
        this.expectedBucketOwner = expectedBucketOwner;
        return this;
    }

    @Override
    public void setExpectedBucketOwner(String expectedBucketOwner) {
        this.withExpectedBucketOwner(expectedBucketOwner);
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return this.bucketName;
    }
}

