/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.BucketAccelerateConfiguration;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;

public class SetBucketAccelerateConfigurationRequest
extends AmazonWebServiceRequest
implements ExpectedBucketOwnerRequest {
    private String bucketName;
    private BucketAccelerateConfiguration accelerateConfiguration;
    private String expectedBucketOwner;

    public SetBucketAccelerateConfigurationRequest(String bucketName, BucketAccelerateConfiguration configuration) {
        this.bucketName = bucketName;
        this.accelerateConfiguration = configuration;
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public SetBucketAccelerateConfigurationRequest withExpectedBucketOwner(String expectedBucketOwner) {
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

    public SetBucketAccelerateConfigurationRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    public BucketAccelerateConfiguration getAccelerateConfiguration() {
        return this.accelerateConfiguration;
    }

    public void setAccelerateConfiguration(BucketAccelerateConfiguration accelerateConfiguration) {
        this.accelerateConfiguration = accelerateConfiguration;
    }

    public SetBucketAccelerateConfigurationRequest withAccelerateConfiguration(BucketAccelerateConfiguration accelerateConfiguration) {
        this.setAccelerateConfiguration(accelerateConfiguration);
        return this;
    }
}

