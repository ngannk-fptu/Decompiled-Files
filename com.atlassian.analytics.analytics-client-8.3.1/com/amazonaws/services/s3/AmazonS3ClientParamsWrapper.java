/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.client.AwsSyncClientParams;
import com.amazonaws.services.s3.AmazonS3ClientParams;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.util.ValidationUtils;

@Immutable
@SdkInternalApi
class AmazonS3ClientParamsWrapper
extends AmazonS3ClientParams {
    private final AwsSyncClientParams clientParams;
    private final S3ClientOptions s3ClientOptions;

    public AmazonS3ClientParamsWrapper(AwsSyncClientParams delegate, S3ClientOptions s3ClientOptions) {
        this.clientParams = ValidationUtils.assertNotNull(delegate, "delegate");
        this.s3ClientOptions = s3ClientOptions;
    }

    @Override
    public AwsSyncClientParams getClientParams() {
        return this.clientParams;
    }

    @Override
    public S3ClientOptions getS3ClientOptions() {
        return this.s3ClientOptions;
    }
}

