/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.ClientType
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.checksums.Md5Checksum
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.interceptor.Context$AfterUnmarshalling
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpRequest
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpResponse
 *  software.amazon.awssdk.core.interceptor.ExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.http.SdkHttpHeaders
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.checksums.Md5Checksum;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.http.SdkHttpHeaders;
import software.amazon.awssdk.services.s3.checksums.ChecksumCalculatingAsyncRequestBody;
import software.amazon.awssdk.services.s3.checksums.ChecksumValidatingPublisher;
import software.amazon.awssdk.services.s3.checksums.ChecksumsEnabledValidator;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@SdkInternalApi
public final class AsyncChecksumValidationInterceptor
implements ExecutionInterceptor {
    private static ExecutionAttribute<Boolean> ASYNC_RECORDING_CHECKSUM = new ExecutionAttribute("asyncRecordingChecksum");

    public Optional<AsyncRequestBody> modifyAsyncHttpContent(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        boolean shouldRecordChecksum = ChecksumsEnabledValidator.shouldRecordChecksum(context.request(), ClientType.ASYNC, executionAttributes, context.httpRequest());
        if (shouldRecordChecksum && context.asyncRequestBody().isPresent()) {
            Md5Checksum checksum = new Md5Checksum();
            executionAttributes.putAttribute(ASYNC_RECORDING_CHECKSUM, (Object)true);
            executionAttributes.putAttribute(ChecksumsEnabledValidator.CHECKSUM, (Object)checksum);
            return Optional.of(new ChecksumCalculatingAsyncRequestBody(context.httpRequest(), (AsyncRequestBody)context.asyncRequestBody().get(), (SdkChecksum)checksum));
        }
        return context.asyncRequestBody();
    }

    public Optional<Publisher<ByteBuffer>> modifyAsyncHttpResponseContent(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        if (ChecksumsEnabledValidator.getObjectChecksumEnabledPerResponse(context.request(), (SdkHttpHeaders)context.httpResponse()) && context.responsePublisher().isPresent()) {
            long contentLength = context.httpResponse().firstMatchingHeader("Content-Length").map(Long::parseLong).orElse(0L);
            Md5Checksum checksum = new Md5Checksum();
            executionAttributes.putAttribute(ChecksumsEnabledValidator.CHECKSUM, (Object)checksum);
            if (contentLength > 0L) {
                return Optional.of(new ChecksumValidatingPublisher((Publisher<ByteBuffer>)((Publisher)context.responsePublisher().get()), (SdkChecksum)checksum, contentLength));
            }
        }
        return context.responsePublisher();
    }

    public void afterUnmarshalling(Context.AfterUnmarshalling context, ExecutionAttributes executionAttributes) {
        boolean recordingChecksum = Boolean.TRUE.equals(executionAttributes.getAttribute(ASYNC_RECORDING_CHECKSUM));
        boolean responseChecksumIsValid = ChecksumsEnabledValidator.responseChecksumIsValid(context.httpResponse());
        if (recordingChecksum && responseChecksumIsValid) {
            ChecksumsEnabledValidator.validatePutObjectChecksum((PutObjectResponse)context.response(), executionAttributes);
        }
    }
}

