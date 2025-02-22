/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.AbstractPutObjectRequest;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.s3.model.SSECustomerKey;
import com.amazonaws.services.s3.model.StorageClass;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

public class PutObjectRequest
extends AbstractPutObjectRequest
implements Serializable,
ExpectedBucketOwnerRequest {
    private boolean isRequesterPays;
    private String expectedBucketOwner;

    public PutObjectRequest(String bucketName, String key, File file) {
        super(bucketName, key, file);
    }

    public PutObjectRequest(String bucketName, String key, String redirectLocation) {
        super(bucketName, key, redirectLocation);
    }

    public PutObjectRequest(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
        super(bucketName, key, input, metadata);
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public PutObjectRequest withExpectedBucketOwner(String expectedBucketOwner) {
        this.expectedBucketOwner = expectedBucketOwner;
        return this;
    }

    @Override
    public void setExpectedBucketOwner(String expectedBucketOwner) {
        this.withExpectedBucketOwner(expectedBucketOwner);
    }

    @Override
    public PutObjectRequest clone() {
        PutObjectRequest request = (PutObjectRequest)super.clone();
        return this.copyPutObjectBaseTo(request);
    }

    public PutObjectRequest withBucketName(String bucketName) {
        return (PutObjectRequest)super.withBucketName(bucketName);
    }

    public PutObjectRequest withKey(String key) {
        return (PutObjectRequest)super.withKey(key);
    }

    public PutObjectRequest withStorageClass(String storageClass) {
        return (PutObjectRequest)super.withStorageClass(storageClass);
    }

    public PutObjectRequest withStorageClass(StorageClass storageClass) {
        return (PutObjectRequest)super.withStorageClass(storageClass);
    }

    public PutObjectRequest withFile(File file) {
        return (PutObjectRequest)super.withFile(file);
    }

    public PutObjectRequest withMetadata(ObjectMetadata metadata) {
        return (PutObjectRequest)super.withMetadata(metadata);
    }

    public PutObjectRequest withCannedAcl(CannedAccessControlList cannedAcl) {
        return (PutObjectRequest)super.withCannedAcl(cannedAcl);
    }

    public PutObjectRequest withAccessControlList(AccessControlList accessControlList) {
        return (PutObjectRequest)super.withAccessControlList(accessControlList);
    }

    public PutObjectRequest withInputStream(InputStream inputStream) {
        return (PutObjectRequest)super.withInputStream(inputStream);
    }

    public PutObjectRequest withRedirectLocation(String redirectLocation) {
        return (PutObjectRequest)super.withRedirectLocation(redirectLocation);
    }

    public PutObjectRequest withSSECustomerKey(SSECustomerKey sseKey) {
        return (PutObjectRequest)super.withSSECustomerKey(sseKey);
    }

    public PutObjectRequest withTagging(ObjectTagging tagSet) {
        super.setTagging(tagSet);
        return this;
    }

    @Deprecated
    public PutObjectRequest withProgressListener(ProgressListener progressListener) {
        return (PutObjectRequest)super.withProgressListener(progressListener);
    }

    public PutObjectRequest withSSEAwsKeyManagementParams(SSEAwsKeyManagementParams sseAwsKeyManagementParams) {
        return (PutObjectRequest)super.withSSEAwsKeyManagementParams(sseAwsKeyManagementParams);
    }

    public boolean isRequesterPays() {
        return this.isRequesterPays;
    }

    public void setRequesterPays(boolean isRequesterPays) {
        this.isRequesterPays = isRequesterPays;
    }

    public PutObjectRequest withRequesterPays(boolean isRequesterPays) {
        this.setRequesterPays(isRequesterPays);
        return this;
    }
}

