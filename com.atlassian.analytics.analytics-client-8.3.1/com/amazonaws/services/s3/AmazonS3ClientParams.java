/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.client.AwsSyncClientParams;
import com.amazonaws.services.s3.S3ClientOptions;

@SdkInternalApi
abstract class AmazonS3ClientParams {
    AmazonS3ClientParams() {
    }

    public abstract AwsSyncClientParams getClientParams();

    public abstract S3ClientOptions getS3ClientOptions();
}

