/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import com.amazonaws.services.s3.model.LegacyS3ProgressListener;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.amazonaws.services.s3.model.SSECustomerKey;
import com.amazonaws.services.s3.model.SSECustomerKeyProvider;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GetObjectRequest
extends AmazonWebServiceRequest
implements SSECustomerKeyProvider,
Serializable,
ExpectedBucketOwnerRequest {
    private S3ObjectIdBuilder s3ObjectIdBuilder = new S3ObjectIdBuilder();
    private long[] range;
    private List<String> matchingETagConstraints = new ArrayList<String>();
    private List<String> nonmatchingEtagConstraints = new ArrayList<String>();
    private Date unmodifiedSinceConstraint;
    private Date modifiedSinceConstraint;
    private ResponseHeaderOverrides responseHeaders;
    private boolean isRequesterPays;
    private SSECustomerKey sseCustomerKey;
    private Integer partNumber;
    private String expectedBucketOwner;

    public GetObjectRequest(String bucketName, String key) {
        this(bucketName, key, null);
    }

    public GetObjectRequest(String bucketName, String key, String versionId) {
        this.setBucketName(bucketName);
        this.setKey(key);
        this.setVersionId(versionId);
    }

    public GetObjectRequest(S3ObjectId s3ObjectId) {
        this.s3ObjectIdBuilder = new S3ObjectIdBuilder(s3ObjectId);
    }

    public GetObjectRequest(String bucketName, String key, boolean isRequesterPays) {
        this.s3ObjectIdBuilder.withBucket(bucketName).withKey(key);
        this.isRequesterPays = isRequesterPays;
    }

    @Override
    public String getExpectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
    public GetObjectRequest withExpectedBucketOwner(String expectedBucketOwner) {
        this.expectedBucketOwner = expectedBucketOwner;
        return this;
    }

    @Override
    public void setExpectedBucketOwner(String expectedBucketOwner) {
        this.withExpectedBucketOwner(expectedBucketOwner);
    }

    public String getBucketName() {
        return this.s3ObjectIdBuilder.getBucket();
    }

    public void setBucketName(String bucketName) {
        this.s3ObjectIdBuilder.setBucket(bucketName);
    }

    public GetObjectRequest withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    public String getKey() {
        return this.s3ObjectIdBuilder.getKey();
    }

    public void setKey(String key) {
        this.s3ObjectIdBuilder.setKey(key);
    }

    public GetObjectRequest withKey(String key) {
        this.setKey(key);
        return this;
    }

    public String getVersionId() {
        return this.s3ObjectIdBuilder.getVersionId();
    }

    public void setVersionId(String versionId) {
        this.s3ObjectIdBuilder.setVersionId(versionId);
    }

    public GetObjectRequest withVersionId(String versionId) {
        this.setVersionId(versionId);
        return this;
    }

    public long[] getRange() {
        return this.range == null ? null : (long[])this.range.clone();
    }

    public void setRange(long start, long end) {
        this.range = new long[]{start, end};
    }

    public void setRange(long start) {
        this.setRange(start, 0x7FFFFFFFFFFFFFFEL);
    }

    public GetObjectRequest withRange(long start, long end) {
        this.setRange(start, end);
        return this;
    }

    public GetObjectRequest withRange(long start) {
        this.setRange(start);
        return this;
    }

    public List<String> getMatchingETagConstraints() {
        return this.matchingETagConstraints;
    }

    public void setMatchingETagConstraints(List<String> eTagList) {
        this.matchingETagConstraints = eTagList;
    }

    public GetObjectRequest withMatchingETagConstraint(String eTag) {
        this.matchingETagConstraints.add(eTag);
        return this;
    }

    public List<String> getNonmatchingETagConstraints() {
        return this.nonmatchingEtagConstraints;
    }

    public void setNonmatchingETagConstraints(List<String> eTagList) {
        this.nonmatchingEtagConstraints = eTagList;
    }

    public GetObjectRequest withNonmatchingETagConstraint(String eTag) {
        this.nonmatchingEtagConstraints.add(eTag);
        return this;
    }

    public Date getUnmodifiedSinceConstraint() {
        return this.unmodifiedSinceConstraint;
    }

    public void setUnmodifiedSinceConstraint(Date date) {
        this.unmodifiedSinceConstraint = date;
    }

    public GetObjectRequest withUnmodifiedSinceConstraint(Date date) {
        this.setUnmodifiedSinceConstraint(date);
        return this;
    }

    public Date getModifiedSinceConstraint() {
        return this.modifiedSinceConstraint;
    }

    public void setModifiedSinceConstraint(Date date) {
        this.modifiedSinceConstraint = date;
    }

    public GetObjectRequest withModifiedSinceConstraint(Date date) {
        this.setModifiedSinceConstraint(date);
        return this;
    }

    public ResponseHeaderOverrides getResponseHeaders() {
        return this.responseHeaders;
    }

    public void setResponseHeaders(ResponseHeaderOverrides responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public GetObjectRequest withResponseHeaders(ResponseHeaderOverrides responseHeaders) {
        this.setResponseHeaders(responseHeaders);
        return this;
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
    public GetObjectRequest withProgressListener(ProgressListener progressListener) {
        this.setProgressListener(progressListener);
        return this;
    }

    public boolean isRequesterPays() {
        return this.isRequesterPays;
    }

    public void setRequesterPays(boolean isRequesterPays) {
        this.isRequesterPays = isRequesterPays;
    }

    public GetObjectRequest withRequesterPays(boolean isRequesterPays) {
        this.setRequesterPays(isRequesterPays);
        return this;
    }

    @Override
    public SSECustomerKey getSSECustomerKey() {
        return this.sseCustomerKey;
    }

    public void setSSECustomerKey(SSECustomerKey sseKey) {
        this.sseCustomerKey = sseKey;
    }

    public GetObjectRequest withSSECustomerKey(SSECustomerKey sseKey) {
        this.setSSECustomerKey(sseKey);
        return this;
    }

    public Integer getPartNumber() {
        return this.partNumber;
    }

    public void setPartNumber(Integer partNumber) {
        this.partNumber = partNumber;
    }

    public GetObjectRequest withPartNumber(Integer partNumber) {
        this.setPartNumber(partNumber);
        return this;
    }

    public S3ObjectId getS3ObjectId() {
        return this.s3ObjectIdBuilder.build();
    }

    public void setS3ObjectId(S3ObjectId s3ObjectId) {
        this.s3ObjectIdBuilder = new S3ObjectIdBuilder(s3ObjectId);
    }

    public GetObjectRequest withS3ObjectId(S3ObjectId s3ObjectId) {
        this.setS3ObjectId(s3ObjectId);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GetObjectRequest that = (GetObjectRequest)o;
        if (this.isRequesterPays != that.isRequesterPays) {
            return false;
        }
        if (this.s3ObjectIdBuilder != null ? !this.s3ObjectIdBuilder.equals(that.s3ObjectIdBuilder) : that.s3ObjectIdBuilder != null) {
            return false;
        }
        if (!Arrays.equals(this.range, that.range)) {
            return false;
        }
        if (this.matchingETagConstraints != null ? !this.matchingETagConstraints.equals(that.matchingETagConstraints) : that.matchingETagConstraints != null) {
            return false;
        }
        if (this.nonmatchingEtagConstraints != null ? !this.nonmatchingEtagConstraints.equals(that.nonmatchingEtagConstraints) : that.nonmatchingEtagConstraints != null) {
            return false;
        }
        if (this.unmodifiedSinceConstraint != null ? !this.unmodifiedSinceConstraint.equals(that.unmodifiedSinceConstraint) : that.unmodifiedSinceConstraint != null) {
            return false;
        }
        if (this.modifiedSinceConstraint != null ? !this.modifiedSinceConstraint.equals(that.modifiedSinceConstraint) : that.modifiedSinceConstraint != null) {
            return false;
        }
        if (this.responseHeaders != null ? !this.responseHeaders.equals(that.responseHeaders) : that.responseHeaders != null) {
            return false;
        }
        if (this.sseCustomerKey != null ? !this.sseCustomerKey.equals(that.sseCustomerKey) : that.sseCustomerKey != null) {
            return false;
        }
        return this.partNumber != null ? this.partNumber.equals(that.partNumber) : that.partNumber == null;
    }

    public int hashCode() {
        int result = this.s3ObjectIdBuilder != null ? this.s3ObjectIdBuilder.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(this.range);
        result = 31 * result + (this.matchingETagConstraints != null ? this.matchingETagConstraints.hashCode() : 0);
        result = 31 * result + (this.nonmatchingEtagConstraints != null ? this.nonmatchingEtagConstraints.hashCode() : 0);
        result = 31 * result + (this.unmodifiedSinceConstraint != null ? this.unmodifiedSinceConstraint.hashCode() : 0);
        result = 31 * result + (this.modifiedSinceConstraint != null ? this.modifiedSinceConstraint.hashCode() : 0);
        result = 31 * result + (this.responseHeaders != null ? this.responseHeaders.hashCode() : 0);
        result = 31 * result + (this.isRequesterPays ? 1 : 0);
        result = 31 * result + (this.sseCustomerKey != null ? this.sseCustomerKey.hashCode() : 0);
        result = 31 * result + (this.partNumber != null ? this.partNumber.hashCode() : 0);
        return result;
    }
}

