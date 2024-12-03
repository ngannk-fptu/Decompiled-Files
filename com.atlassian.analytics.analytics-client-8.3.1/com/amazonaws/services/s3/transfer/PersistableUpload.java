/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PersistableUpload
extends PersistableTransfer {
    static final String TYPE = "upload";
    @JsonProperty
    private final String pauseType = "upload";
    @JsonProperty
    private final String bucketName;
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String file;
    @JsonProperty
    private final String multipartUploadId;
    @JsonProperty
    private final long partSize;
    @JsonProperty
    private final long mutlipartUploadThreshold;

    public PersistableUpload() {
        this(null, null, null, null, -1L, -1L);
    }

    public PersistableUpload(@JsonProperty(value="bucketName") String bucketName, @JsonProperty(value="key") String key, @JsonProperty(value="file") String file, @JsonProperty(value="multipartUploadId") String multipartUploadId, @JsonProperty(value="partSize") long partSize, @JsonProperty(value="mutlipartUploadThreshold") long mutlipartUploadThreshold) {
        this.bucketName = bucketName;
        this.key = key;
        this.file = file;
        this.multipartUploadId = multipartUploadId;
        this.partSize = partSize;
        this.mutlipartUploadThreshold = mutlipartUploadThreshold;
    }

    String getBucketName() {
        return this.bucketName;
    }

    String getKey() {
        return this.key;
    }

    String getMultipartUploadId() {
        return this.multipartUploadId;
    }

    long getPartSize() {
        return this.partSize;
    }

    long getMutlipartUploadThreshold() {
        return this.mutlipartUploadThreshold;
    }

    String getFile() {
        return this.file;
    }

    String getPauseType() {
        return TYPE;
    }
}

