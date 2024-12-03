/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.S3Object;

public final class PresignedUrlDownloadResult {
    private S3Object s3Object;

    public S3Object getS3Object() {
        return this.s3Object;
    }

    public void setS3Object(S3Object s3Object) {
        this.s3Object = s3Object;
    }

    public PresignedUrlDownloadResult withS3Object(S3Object s3Object) {
        this.setS3Object(s3Object);
        return this;
    }
}

