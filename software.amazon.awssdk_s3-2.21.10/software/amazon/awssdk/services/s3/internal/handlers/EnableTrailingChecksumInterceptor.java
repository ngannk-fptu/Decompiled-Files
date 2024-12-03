/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpRequest
 *  software.amazon.awssdk.core.interceptor.Context$ModifyResponse
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.http.SdkHttpHeaders
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.http.SdkHttpHeaders;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.checksums.ChecksumsEnabledValidator;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class EnableTrailingChecksumInterceptor
implements ExecutionInterceptor {
    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (ChecksumsEnabledValidator.getObjectChecksumEnabledPerRequest(context.request(), executionAttributes)) {
            return (SdkHttpRequest)((SdkHttpRequest.Builder)context.httpRequest().toBuilder()).putHeader("x-amz-te", "append-md5").build();
        }
        return context.httpRequest();
    }

    public SdkResponse modifyResponse(Context.ModifyResponse context, ExecutionAttributes executionAttributes) {
        SdkResponse response = context.response();
        SdkHttpResponse httpResponse = context.httpResponse();
        if (ChecksumsEnabledValidator.getObjectChecksumEnabledPerResponse(context.request(), (SdkHttpHeaders)httpResponse)) {
            GetObjectResponse getResponse = (GetObjectResponse)response;
            Long contentLength = getResponse.contentLength();
            Validate.notNull((Object)contentLength, (String)"Service returned null 'Content-Length'.", (Object[])new Object[0]);
            return (SdkResponse)getResponse.toBuilder().contentLength(contentLength - 16L).build();
        }
        return response;
    }
}

