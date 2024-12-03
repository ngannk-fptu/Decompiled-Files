/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.sync.RequestBody
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;

@SdkInternalApi
public class CreateMultipartUploadRequestInterceptor
implements ExecutionInterceptor {
    public Optional<RequestBody> modifyHttpContent(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (context.request() instanceof CreateMultipartUploadRequest) {
            return Optional.of(RequestBody.fromInputStream((InputStream)new ByteArrayInputStream(new byte[0]), (long)0L));
        }
        return context.requestBody();
    }

    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (context.request() instanceof CreateMultipartUploadRequest) {
            SdkHttpRequest.Builder builder = ((SdkHttpRequest.Builder)context.httpRequest().toBuilder()).putHeader("Content-Length", String.valueOf(0));
            if (!context.httpRequest().firstMatchingHeader("Content-Type").isPresent()) {
                builder.putHeader("Content-Type", "binary/octet-stream");
            }
            return (SdkHttpRequest)builder.build();
        }
        return context.httpRequest();
    }
}

