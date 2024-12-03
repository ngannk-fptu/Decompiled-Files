/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.exception.AwsErrorDetails
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.interceptor.Context$FailedExecution
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@SdkInternalApi
public final class ExceptionTranslationInterceptor
implements ExecutionInterceptor {
    public Throwable modifyException(Context.FailedExecution context, ExecutionAttributes executionAttributes) {
        if (!this.isS3Exception404(context.exception()) || !this.isHeadRequest(context.request())) {
            return context.exception();
        }
        String message = context.exception().getMessage();
        S3Exception exception = (S3Exception)((Object)context.exception());
        String requestIdFromHeader = exception.awsErrorDetails().sdkHttpResponse().firstMatchingHeader("x-amz-request-id").orElse(null);
        String requestId = Optional.ofNullable(exception.requestId()).orElse(requestIdFromHeader);
        AwsErrorDetails errorDetails = exception.awsErrorDetails();
        if (context.request() instanceof HeadObjectRequest) {
            return (Throwable)NoSuchKeyException.builder().awsErrorDetails(this.fillErrorDetails(errorDetails, "NoSuchKey", "The specified key does not exist.")).statusCode(404).requestId(requestId).message(message).build();
        }
        if (context.request() instanceof HeadBucketRequest) {
            return (Throwable)NoSuchBucketException.builder().awsErrorDetails(this.fillErrorDetails(errorDetails, "NoSuchBucket", "The specified bucket does not exist.")).statusCode(404).requestId(requestId).message(message).build();
        }
        return context.exception();
    }

    private AwsErrorDetails fillErrorDetails(AwsErrorDetails original, String errorCode, String errorMessage) {
        return original.toBuilder().errorMessage(errorMessage).errorCode(errorCode).build();
    }

    private boolean isHeadRequest(SdkRequest request) {
        return request instanceof HeadObjectRequest || request instanceof HeadBucketRequest;
    }

    private boolean isS3Exception404(Throwable thrown) {
        if (!(thrown instanceof S3Exception)) {
            return false;
        }
        return ((S3Exception)((Object)thrown)).statusCode() == 404;
    }
}

