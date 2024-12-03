/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.LegacyS3ProgressListener;
import com.amazonaws.services.s3.model.ObjectLockLegalHoldStatus;
import com.amazonaws.services.s3.model.ObjectLockMode;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3DataSource;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParamsProvider;
import com.amazonaws.services.s3.model.SSECustomerKey;
import com.amazonaws.services.s3.model.SSECustomerKeyProvider;
import com.amazonaws.services.s3.model.StorageClass;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

public abstract class AbstractPutObjectRequest
extends AmazonWebServiceRequest
implements Cloneable,
SSECustomerKeyProvider,
SSEAwsKeyManagementParamsProvider,
S3DataSource,
Serializable {
    private String bucketName;
    private String key;
    private File file;
    private transient InputStream inputStream;
    private ObjectMetadata metadata;
    private CannedAccessControlList cannedAcl;
    private AccessControlList accessControlList;
    private String storageClass;
    private String redirectLocation;
    private SSECustomerKey sseCustomerKey;
    private SSEAwsKeyManagementParams sseAwsKeyManagementParams;
    private ObjectTagging tagging;
    private String objectLockMode;
    private Date objectLockRetainUntilDate;
    private String objectLockLegalHoldStatus;
    private Boolean bucketKeyEnabled;

    public AbstractPutObjectRequest(String bucketName, String key, File file) {
        this.bucketName = bucketName;
        this.key = key;
        this.file = file;
    }

    public AbstractPutObjectRequest(String bucketName, String key, String redirectLocation) {
        this.bucketName = bucketName;
        this.key = key;
        this.redirectLocation = redirectLocation;
    }

    protected AbstractPutObjectRequest(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
        this.bucketName = bucketName;
        this.key = key;
        this.inputStream = input;
        this.metadata = metadata;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public <T extends AbstractPutObjectRequest> T withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public <T extends AbstractPutObjectRequest> T withKey(String key) {
        this.setKey(key);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public String getStorageClass() {
        return this.storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public <T extends AbstractPutObjectRequest> T withStorageClass(String storageClass) {
        this.setStorageClass(storageClass);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public void setStorageClass(StorageClass storageClass) {
        this.storageClass = storageClass.toString();
    }

    public <T extends AbstractPutObjectRequest> T withStorageClass(StorageClass storageClass) {
        this.setStorageClass(storageClass);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    public <T extends AbstractPutObjectRequest> T withFile(File file) {
        this.setFile(file);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public ObjectMetadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    public <T extends AbstractPutObjectRequest> T withMetadata(ObjectMetadata metadata) {
        this.setMetadata(metadata);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public CannedAccessControlList getCannedAcl() {
        return this.cannedAcl;
    }

    public void setCannedAcl(CannedAccessControlList cannedAcl) {
        this.cannedAcl = cannedAcl;
    }

    public <T extends AbstractPutObjectRequest> T withCannedAcl(CannedAccessControlList cannedAcl) {
        this.setCannedAcl(cannedAcl);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public AccessControlList getAccessControlList() {
        return this.accessControlList;
    }

    public void setAccessControlList(AccessControlList accessControlList) {
        this.accessControlList = accessControlList;
    }

    public <T extends AbstractPutObjectRequest> T withAccessControlList(AccessControlList accessControlList) {
        this.setAccessControlList(accessControlList);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public <T extends AbstractPutObjectRequest> T withInputStream(InputStream inputStream) {
        this.setInputStream(inputStream);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    public String getRedirectLocation() {
        return this.redirectLocation;
    }

    public <T extends AbstractPutObjectRequest> T withRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    @Override
    public SSECustomerKey getSSECustomerKey() {
        return this.sseCustomerKey;
    }

    public void setSSECustomerKey(SSECustomerKey sseKey) {
        if (sseKey != null && this.sseAwsKeyManagementParams != null) {
            throw new IllegalArgumentException("Either SSECustomerKey or SSEAwsKeyManagementParams must not be set at the same time.");
        }
        this.sseCustomerKey = sseKey;
    }

    public <T extends AbstractPutObjectRequest> T withSSECustomerKey(SSECustomerKey sseKey) {
        this.setSSECustomerKey(sseKey);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public ObjectTagging getTagging() {
        return this.tagging;
    }

    public void setTagging(ObjectTagging tagging) {
        this.tagging = tagging;
    }

    public <T extends PutObjectRequest> T withTagging(ObjectTagging tagSet) {
        this.setTagging(tagSet);
        PutObjectRequest t = (PutObjectRequest)this;
        return (T)t;
    }

    public String getObjectLockMode() {
        return this.objectLockMode;
    }

    public <T extends PutObjectRequest> T withObjectLockMode(String objectLockMode) {
        this.objectLockMode = objectLockMode;
        return (T)((PutObjectRequest)this);
    }

    public <T extends PutObjectRequest> T withObjectLockMode(ObjectLockMode objectLockMode) {
        return this.withObjectLockMode(objectLockMode.toString());
    }

    public void setObjectLockMode(String objectLockMode) {
        this.withObjectLockMode(objectLockMode);
    }

    public void setObjectLockMode(ObjectLockMode objectLockMode) {
        this.setObjectLockMode(objectLockMode.toString());
    }

    public Date getObjectLockRetainUntilDate() {
        return this.objectLockRetainUntilDate;
    }

    public <T extends PutObjectRequest> T withObjectLockRetainUntilDate(Date objectLockRetainUntilDate) {
        this.objectLockRetainUntilDate = objectLockRetainUntilDate;
        return (T)((PutObjectRequest)this);
    }

    public void setObjectLockRetainUntilDate(Date objectLockRetainUntilDate) {
        this.withObjectLockRetainUntilDate(objectLockRetainUntilDate);
    }

    public String getObjectLockLegalHoldStatus() {
        return this.objectLockLegalHoldStatus;
    }

    public <T extends PutObjectRequest> T withObjectLockLegalHoldStatus(String objectLockLegalHoldStatus) {
        this.objectLockLegalHoldStatus = objectLockLegalHoldStatus;
        return (T)((PutObjectRequest)this);
    }

    public <T extends PutObjectRequest> T withObjectLockLegalHoldStatus(ObjectLockLegalHoldStatus objectLockLegalHoldStatus) {
        return this.withObjectLockLegalHoldStatus(objectLockLegalHoldStatus.toString());
    }

    public void setObjectLockLegalHoldStatus(String objectLockLegalHoldStatus) {
        this.withObjectLockLegalHoldStatus(objectLockLegalHoldStatus);
    }

    public void setObjectLockLegalHoldStatus(ObjectLockLegalHoldStatus objectLockLegalHoldStatus) {
        this.setObjectLockLegalHoldStatus(objectLockLegalHoldStatus.toString());
    }

    @Deprecated
    public void setProgressListener(ProgressListener progressListener) {
        this.setGeneralProgressListener(new LegacyS3ProgressListener(progressListener));
    }

    @Deprecated
    public ProgressListener getProgressListener() {
        com.amazonaws.event.ProgressListener generalProgressListener = this.getGeneralProgressListener();
        if (generalProgressListener instanceof LegacyS3ProgressListener) {
            return ((LegacyS3ProgressListener)generalProgressListener).unwrap();
        }
        return null;
    }

    @Deprecated
    public <T extends AbstractPutObjectRequest> T withProgressListener(ProgressListener progressListener) {
        this.setProgressListener(progressListener);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    @Override
    public SSEAwsKeyManagementParams getSSEAwsKeyManagementParams() {
        return this.sseAwsKeyManagementParams;
    }

    public void setSSEAwsKeyManagementParams(SSEAwsKeyManagementParams params) {
        if (params != null && this.sseCustomerKey != null) {
            throw new IllegalArgumentException("Either SSECustomerKey or SSEAwsKeyManagementParams must not be set at the same time.");
        }
        this.sseAwsKeyManagementParams = params;
    }

    public <T extends AbstractPutObjectRequest> T withSSEAwsKeyManagementParams(SSEAwsKeyManagementParams sseAwsKeyManagementParams) {
        this.setSSEAwsKeyManagementParams(sseAwsKeyManagementParams);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    public Boolean getBucketKeyEnabled() {
        return this.bucketKeyEnabled;
    }

    public void setBucketKeyEnabled(Boolean bucketKeyEnabled) {
        this.bucketKeyEnabled = bucketKeyEnabled;
    }

    public <T extends AbstractPutObjectRequest> T withBucketKeyEnabled(Boolean bucketKeyEnabled) {
        this.setBucketKeyEnabled(bucketKeyEnabled);
        AbstractPutObjectRequest t = this;
        return (T)t;
    }

    @Override
    public AbstractPutObjectRequest clone() {
        return (AbstractPutObjectRequest)super.clone();
    }

    protected final <T extends AbstractPutObjectRequest> T copyPutObjectBaseTo(T target) {
        this.copyBaseTo(target);
        ObjectMetadata metadata = this.getMetadata();
        return ((AbstractPutObjectRequest)((AbstractPutObjectRequest)((AbstractPutObjectRequest)((AbstractPutObjectRequest)((AbstractPutObjectRequest)((AbstractPutObjectRequest)((AbstractPutObjectRequest)target.withAccessControlList(this.getAccessControlList())).withCannedAcl(this.getCannedAcl())).withInputStream(this.getInputStream())).withMetadata(metadata == null ? null : metadata.clone())).withRedirectLocation(this.getRedirectLocation())).withStorageClass(this.getStorageClass())).withSSEAwsKeyManagementParams(this.getSSEAwsKeyManagementParams())).withSSECustomerKey(this.getSSECustomerKey());
    }
}

