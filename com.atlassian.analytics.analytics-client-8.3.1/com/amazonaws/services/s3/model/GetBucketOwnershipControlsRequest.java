/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import java.io.Serializable;

public class GetBucketOwnershipControlsRequest
extends AmazonWebServiceRequest
implements Serializable,
ExpectedBucketOwnerRequest {
    private String bucketName;
    private String expectedBucketOwner;

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public GetBucketOwnershipControlsRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public GetBucketOwnershipControlsRequest withExpectedBucketOwner(String expectedBucketOwner) {
        this.expectedBucketOwner = expectedBucketOwner;
        return this;
    }

    @Override
    public void setExpectedBucketOwner(String expectedBucketOwner) {
        this.withExpectedBucketOwner(expectedBucketOwner);
    }
}

