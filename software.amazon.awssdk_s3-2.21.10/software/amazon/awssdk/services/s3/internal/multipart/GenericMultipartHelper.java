/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class GenericMultipartHelper<RequestT extends S3Request, ResponseT extends S3Response> {
    private static final Logger log = Logger.loggerFor(GenericMultipartHelper.class);
    private static final long MAX_UPLOAD_PARTS = 10000L;
    private final S3AsyncClient s3AsyncClient;
    private final Function<RequestT, AbortMultipartUploadRequest.Builder> abortMultipartUploadRequestConverter;
    private final Function<CompleteMultipartUploadResponse, ResponseT> responseConverter;

    public GenericMultipartHelper(S3AsyncClient s3AsyncClient, Function<RequestT, AbortMultipartUploadRequest.Builder> abortMultipartUploadRequestConverter, Function<CompleteMultipartUploadResponse, ResponseT> responseConverter) {
        this.s3AsyncClient = s3AsyncClient;
        this.abortMultipartUploadRequestConverter = abortMultipartUploadRequestConverter;
        this.responseConverter = responseConverter;
    }

    public void handleException(CompletableFuture<ResponseT> returnFuture, Supplier<String> message, Throwable throwable) {
        Throwable cause;
        Throwable throwable2 = cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;
        if (cause instanceof Error || cause instanceof SdkException) {
            cause.addSuppressed((Throwable)SdkClientException.create((String)message.get()));
            returnFuture.completeExceptionally(cause);
        } else {
            SdkClientException exception = SdkClientException.create((String)message.get(), (Throwable)cause);
            returnFuture.completeExceptionally((Throwable)exception);
        }
    }

    public long calculateOptimalPartSizeFor(long contentLengthOfSource, long partSizeInBytes) {
        double optimalPartSize = (double)contentLengthOfSource / 10000.0;
        optimalPartSize = Math.ceil(optimalPartSize);
        return (long)Math.max(optimalPartSize, (double)partSizeInBytes);
    }

    public int determinePartCount(long contentLength, long partSize) {
        return (int)Math.ceil((double)contentLength / (double)partSize);
    }

    public CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(RequestT request, String uploadId, CompletedPart[] parts) {
        log.debug(() -> String.format("Sending completeMultipartUploadRequest, uploadId: %s", uploadId));
        CompleteMultipartUploadRequest completeMultipartUploadRequest = (CompleteMultipartUploadRequest)((Object)CompleteMultipartUploadRequest.builder().bucket((String)request.getValueForField("Bucket", String.class).get()).key((String)request.getValueForField("Key", String.class).get()).uploadId(uploadId).multipartUpload((CompletedMultipartUpload)CompletedMultipartUpload.builder().parts(parts).build()).build());
        return this.s3AsyncClient.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(RequestT request, String uploadId, AtomicReferenceArray<CompletedPart> completedParts) {
        CompletedPart[] parts = (CompletedPart[])IntStream.range(0, completedParts.length()).mapToObj(completedParts::get).toArray(CompletedPart[]::new);
        return this.completeMultipartUpload(request, uploadId, parts);
    }

    public BiFunction<CompleteMultipartUploadResponse, Throwable, Void> handleExceptionOrResponse(RequestT request, CompletableFuture<ResponseT> returnFuture, String uploadId) {
        return (completeMultipartUploadResponse, throwable) -> {
            if (throwable != null) {
                this.cleanUpParts(uploadId, this.abortMultipartUploadRequestConverter.apply((RequestT)((Object)request)));
                this.handleException(returnFuture, () -> "Failed to send multipart requests", (Throwable)throwable);
            } else {
                returnFuture.complete(this.responseConverter.apply((CompleteMultipartUploadResponse)((Object)completeMultipartUploadResponse)));
            }
            return null;
        };
    }

    public void cleanUpParts(String uploadId, AbortMultipartUploadRequest.Builder abortMultipartUploadRequest) {
        log.debug(() -> "Aborting multipart upload: " + uploadId);
        AbortMultipartUploadRequest request = (AbortMultipartUploadRequest)((Object)abortMultipartUploadRequest.uploadId(uploadId).build());
        this.s3AsyncClient.abortMultipartUpload(request).exceptionally(throwable -> {
            log.warn(() -> String.format("Failed to abort previous multipart upload (id: %s). You may need to call S3AsyncClient#abortMultiPartUpload to free all storage consumed by all parts. ", uploadId), throwable);
            return null;
        });
    }
}

