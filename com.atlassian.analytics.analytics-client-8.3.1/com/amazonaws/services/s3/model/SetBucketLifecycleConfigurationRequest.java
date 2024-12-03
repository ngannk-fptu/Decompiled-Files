/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import java.io.Serializable;

public class SetBucketLifecycleConfigurationRequest
extends AmazonWebServiceRequest
implements Serializable,
ExpectedBucketOwnerRequest {
    private String bucketName;
    private BucketLifecycleConfiguration lifecycleConfiguration;
    private String expectedBucketOwner;

    public SetBucketLifecycleConfigurationRequest(String bucketName, BucketLifecycleConfiguration lifecycleConfiguration) {
        this.bucketName = bucketName;
        this.lifecycleConfiguration = lifecycleConfiguration;
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public SetBucketLifecycleConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
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

    public SetBucketLifecycleConfigurationRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    public BucketLifecycleConfiguration getLifecycleConfiguration() {
        return this.lifecycleConfiguration;
    }

    public void setLifecycleConfiguration(BucketLifecycleConfiguration lifecycleConfiguration) {
        this.lifecycleConfiguration = lifecycleConfiguration;
    }

    public SetBucketLifecycleConfigurationRequest withLifecycleConfiguration(BucketLifecycleConfiguration lifecycleConfiguration) {
        this.setLifecycleConfiguration(lifecycleConfiguration);
        return this;
    }
}

