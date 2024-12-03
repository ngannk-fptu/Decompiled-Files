/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.multipart.GenericMultipartHelper;
import software.amazon.awssdk.services.s3.internal.multipart.SdkPojoConversionUtils;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public final class MultipartUploadHelper {
    private static final Logger log = Logger.loggerFor(MultipartUploadHelper.class);
    private final S3AsyncClient s3AsyncClient;
    private final long partSizeInBytes;
    private final GenericMultipartHelper<PutObjectRequest, PutObjectResponse> genericMultipartHelper;
    private final long maxMemoryUsageInBytes;
    private final long multipartUploadThresholdInBytes;

    public MultipartUploadHelper(S3AsyncClient s3AsyncClient, long partSizeInBytes, long multipartUploadThresholdInBytes, long maxMemoryUsageInBytes) {
        this.s3AsyncClient = s3AsyncClient;
        this.partSizeInBytes = partSizeInBytes;
        this.genericMultipartHelper = new GenericMultipartHelper<PutObjectRequest, PutObjectResponse>(s3AsyncClient, SdkPojoConversionUtils::toAbortMultipartUploadRequest, SdkPojoConversionUtils::toPutObjectResponse);
        this.maxMemoryUsageInBytes = maxMemoryUsageInBytes;
        this.multipartUploadThresholdInBytes = multipartUploadThresholdInBytes;
    }

    CompletableFuture<CreateMultipartUploadResponse> createMultipartUpload(PutObjectRequest putObjectRequest, CompletableFuture<PutObjectResponse> returnFuture) {
        CreateMultipartUploadRequest request = SdkPojoConversionUtils.toCreateMultipartUploadRequest(putObjectRequest);
        CompletableFuture<CreateMultipartUploadResponse> createMultipartUploadFuture = this.s3AsyncClient.createMultipartUpload(request);
        CompletableFutureUtils.forwardExceptionTo(returnFuture, createMultipartUploadFuture);
        return createMultipartUploadFuture;
    }

    void completeMultipartUpload(CompletableFuture<PutObjectResponse> returnFuture, String uploadId, CompletedPart[] completedParts, PutObjectRequest putObjectRequest) {
        ((CompletableFuture)this.genericMultipartHelper.completeMultipartUpload(putObjectRequest, uploadId, completedParts).handle(this.genericMultipartHelper.handleExceptionOrResponse(putObjectRequest, returnFuture, uploadId))).exceptionally(throwable -> {
            this.genericMultipartHelper.handleException(returnFuture, () -> "Unexpected exception occurred", (Throwable)throwable);
            return null;
        });
    }

    CompletableFuture<CompletedPart> sendIndividualUploadPartRequest(String uploadId, Consumer<CompletedPart> completedPartsConsumer, Collection<CompletableFuture<CompletedPart>> futures, Pair<UploadPartRequest, AsyncRequestBody> requestPair) {
        UploadPartRequest uploadPartRequest = (UploadPartRequest)((Object)requestPair.left());
        Integer partNumber = uploadPartRequest.partNumber();
        log.debug(() -> "Sending uploadPartRequest: " + uploadPartRequest.partNumber() + " uploadId: " + uploadId + " contentLength " + ((AsyncRequestBody)requestPair.right()).contentLength());
        CompletableFuture<UploadPartResponse> uploadPartFuture = this.s3AsyncClient.uploadPart(uploadPartRequest, (AsyncRequestBody)requestPair.right());
        CompletionStage convertFuture = uploadPartFuture.thenApply(uploadPartResponse -> MultipartUploadHelper.convertUploadPartResponse(completedPartsConsumer, partNumber, uploadPartResponse));
        futures.add((CompletableFuture<CompletedPart>)convertFuture);
        CompletableFutureUtils.forwardExceptionTo((CompletableFuture)convertFuture, uploadPartFuture);
        return convertFuture;
    }

    void failRequestsElegantly(Collection<CompletableFuture<CompletedPart>> futures, Throwable t, String uploadId, CompletableFuture<PutObjectResponse> returnFuture, PutObjectRequest putObjectRequest) {
        this.genericMultipartHelper.handleException(returnFuture, () -> "Failed to send multipart upload requests", t);
        if (uploadId != null) {
            this.genericMultipartHelper.cleanUpParts(uploadId, SdkPojoConversionUtils.toAbortMultipartUploadRequest(putObjectRequest));
        }
        MultipartUploadHelper.cancelingOtherOngoingRequests(futures, t);
    }

    static void cancelingOtherOngoingRequests(Collection<CompletableFuture<CompletedPart>> futures, Throwable t) {
        log.trace(() -> "cancelling other ongoing requests " + futures.size());
        futures.forEach(f -> f.completeExceptionally(t));
    }

    static CompletedPart convertUploadPartResponse(Consumer<CompletedPart> consumer, Integer partNumber, UploadPartResponse uploadPartResponse) {
        CompletedPart completedPart = SdkPojoConversionUtils.toCompletedPart(uploadPartResponse, (int)partNumber);
        consumer.accept(completedPart);
        return completedPart;
    }

    void uploadInOneChunk(PutObjectRequest putObjectRequest, AsyncRequestBody asyncRequestBody, CompletableFuture<PutObjectResponse> returnFuture) {
        CompletableFuture<PutObjectResponse> putObjectResponseCompletableFuture = this.s3AsyncClient.putObject(putObjectRequest, asyncRequestBody);
        CompletableFutureUtils.forwardExceptionTo(returnFuture, putObjectResponseCompletableFuture);
        CompletableFutureUtils.forwardResultTo(putObjectResponseCompletableFuture, returnFuture);
    }
}

