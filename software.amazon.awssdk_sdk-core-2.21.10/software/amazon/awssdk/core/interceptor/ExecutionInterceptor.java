/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpResponse
 */
package software.amazon.awssdk.core.interceptor;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkPublicApi
public interface ExecutionInterceptor {
    default public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
    }

    default public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        return context.request();
    }

    default public void beforeMarshalling(Context.BeforeMarshalling context, ExecutionAttributes executionAttributes) {
    }

    default public void afterMarshalling(Context.AfterMarshalling context, ExecutionAttributes executionAttributes) {
    }

    default public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        return context.httpRequest();
    }

    default public Optional<RequestBody> modifyHttpContent(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        return context.requestBody();
    }

    default public Optional<AsyncRequestBody> modifyAsyncHttpContent(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        return context.asyncRequestBody();
    }

    default public void beforeTransmission(Context.BeforeTransmission context, ExecutionAttributes executionAttributes) {
    }

    default public void afterTransmission(Context.AfterTransmission context, ExecutionAttributes executionAttributes) {
    }

    default public SdkHttpResponse modifyHttpResponse(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        return context.httpResponse();
    }

    default public Optional<Publisher<ByteBuffer>> modifyAsyncHttpResponseContent(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        return context.responsePublisher();
    }

    default public Optional<InputStream> modifyHttpResponseContent(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        return context.responseBody();
    }

    default public void beforeUnmarshalling(Context.BeforeUnmarshalling context, ExecutionAttributes executionAttributes) {
    }

    default public void afterUnmarshalling(Context.AfterUnmarshalling context, ExecutionAttributes executionAttributes) {
    }

    default public SdkResponse modifyResponse(Context.ModifyResponse context, ExecutionAttributes executionAttributes) {
        return context.response();
    }

    default public void afterExecution(Context.AfterExecution context, ExecutionAttributes executionAttributes) {
    }

    default public Throwable modifyException(Context.FailedExecution context, ExecutionAttributes executionAttributes) {
        return context.exception();
    }

    default public void onExecutionFailure(Context.FailedExecution context, ExecutionAttributes executionAttributes) {
    }
}

