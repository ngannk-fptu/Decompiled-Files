/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.internal.endpoints;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.internal.ConfiguredS3SdkHttpRequest;
import software.amazon.awssdk.services.s3.internal.endpoints.S3EndpointResolverContext;

@SdkInternalApi
public interface S3EndpointResolver {
    public ConfiguredS3SdkHttpRequest applyEndpointConfiguration(S3EndpointResolverContext var1);
}

