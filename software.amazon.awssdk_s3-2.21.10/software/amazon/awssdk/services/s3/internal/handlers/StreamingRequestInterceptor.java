/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

@SdkInternalApi
public final class StreamingRequestInterceptor
implements ExecutionInterceptor {
    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (context.request() instanceof PutObjectRequest || context.request() instanceof UploadPartRequest) {
            return (SdkHttpRequest)((SdkHttpRequest.Builder)context.httpRequest().toBuilder()).putHeader("Expect", "100-continue").build();
        }
        return context.httpRequest();
    }
}

