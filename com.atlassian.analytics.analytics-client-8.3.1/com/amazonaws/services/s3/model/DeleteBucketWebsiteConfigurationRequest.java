/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import com.amazonaws.services.s3.model.GenericBucketRequest;
import java.io.Serializable;

public class DeleteBucketWebsiteConfigurationRequest
extends GenericBucketRequest
implements Serializable,
ExpectedBucketOwnerRequest {
    private String expectedBucketOwner;

    public DeleteBucketWebsiteConfigurationRequest(String bucketName) {
        super(bucketName);
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public DeleteBucketWebsiteConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
        this.expectedBucketOwner = expectedBucketOwner;
        return this;
    }

    @Override
    public void setExpectedBucketOwner(String expectedBucketOwner) {
        this.withExpectedBucketOwner(expectedBucketOwner);
    }
}

