/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.internal.crt;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3CrtAsyncClientBuilder;
import software.amazon.awssdk.services.s3.internal.crt.DefaultS3CrtAsyncClient;

@SdkInternalApi
public interface S3CrtAsyncClient
extends S3AsyncClient {
    public static S3CrtAsyncClientBuilder builder() {
        return new DefaultS3CrtAsyncClient.DefaultS3CrtClientBuilder();
    }
}

