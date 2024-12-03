/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class DeleteBucketIntelligentTieringConfigurationRequest
extends AmazonWebServiceRequest
implements Serializable {
    private String bucketName;
    private String id;

    public DeleteBucketIntelligentTieringConfigurationRequest() {
    }

    public DeleteBucketIntelligentTieringConfigurationRequest(String bucketName, String id) {
        this.bucketName = bucketName;
        this.id = id;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public DeleteBucketIntelligentTieringConfigurationRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeleteBucketIntelligentTieringConfigurationRequest withId(String id) {
        this.setId(id);
        return this;
    }
}

