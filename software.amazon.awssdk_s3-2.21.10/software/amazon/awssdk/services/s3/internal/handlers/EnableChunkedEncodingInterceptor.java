/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute
 *  software.amazon.awssdk.auth.signer.S3SignerExecutionAttribute
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.interceptor.Context$ModifyRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.auth.signer.S3SignerExecutionAttribute;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

@SdkInternalApi
public final class EnableChunkedEncodingInterceptor
implements ExecutionInterceptor {
    public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        SdkRequest sdkRequest = context.request();
        if (sdkRequest instanceof PutObjectRequest || sdkRequest instanceof UploadPartRequest) {
            S3Configuration serviceConfiguration = (S3Configuration)executionAttributes.getAttribute(AwsSignerExecutionAttribute.SERVICE_CONFIG);
            boolean enableChunkedEncoding = serviceConfiguration != null ? serviceConfiguration.chunkedEncodingEnabled() : true;
            executionAttributes.putAttributeIfAbsent(S3SignerExecutionAttribute.ENABLE_CHUNKED_ENCODING, (Object)enableChunkedEncoding);
        }
        return sdkRequest;
    }
}

