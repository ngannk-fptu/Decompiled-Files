/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import java.io.Serializable;

public class GetBucketAclRequest
extends AmazonWebServiceRequest
implements Serializable,
ExpectedBucketOwnerRequest {
    private String bucketName;
    private String expectedBucketOwner;

    public GetBucketAclRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public GetBucketAclRequest withExpectedBucketOwner(String expectedBucketOwner) {
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
}

