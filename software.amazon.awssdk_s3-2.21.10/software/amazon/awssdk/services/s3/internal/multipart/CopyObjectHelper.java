/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.IntStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.crt.UploadPartCopyRequestIterable;
import software.amazon.awssdk.services.s3.internal.multipart.GenericMultipartHelper;
import software.amazon.awssdk.services.s3.internal.multipart.SdkPojoConversionUtils;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CopyPartResult;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyResponse;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class CopyObjectHelper {
    private static final Logger log = Logger.loggerFor(S3AsyncClient.class);
    private final S3AsyncClient s3AsyncClient;
    private final long partSizeInBytes;
    private final GenericMultipartHelper<CopyObjectRequest, CopyObjectResponse> genericMultipartHelper;
    private final long uploadThreshold;

    public CopyObjectHelper(S3AsyncClient s3AsyncClient, long partSizeInBytes, long uploadThreshold) {
        this.s3AsyncClient = s3AsyncClient;
        this.partSizeInBytes = partSizeInBytes;
        this.genericMultipartHelper = new GenericMultipartHelper<CopyObjectRequest, CopyObjectResponse>(s3AsyncClient, SdkPojoConversionUtils::toAbortMultipartUploadRequest, SdkPojoConversionUtils::toCopyObjectResponse);
        this.uploadThreshold = uploadThreshold;
    }

    public CompletableFuture<CopyObjectResponse> copyObject(CopyObjectRequest copyObjectRequest) {
        CompletableFuture<CopyObjectResponse> returnFuture = new CompletableFuture<CopyObjectResponse>();
        try {
            CompletableFuture<HeadObjectResponse> headFuture = this.s3AsyncClient.headObject(SdkPojoConversionUtils.toHeadObjectRequest(copyObjectRequest));
            CompletableFutureUtils.forwardExceptionTo(returnFuture, headFuture);
            headFuture.whenComplete((headObjectResponse, throwable) -> {
                if (throwable != null) {
                    this.genericMultipartHelper.handleException(returnFuture, () -> "Failed to retrieve metadata from the source object", (Throwable)throwable);
                } else {
                    this.doCopyObject(copyObjectRequest, returnFuture, (HeadObjectResponse)((Object)headObjectResponse));
                }
            });
        }
        catch (Throwable throwable2) {
            returnFuture.completeExceptionally(throwable2);
        }
        return returnFuture;
    }

    private void doCopyObject(CopyObjectRequest copyObjectRequest, CompletableFuture<CopyObjectResponse> returnFuture, HeadObjectResponse headObjectResponse) {
        Long contentLength = headObjectResponse.contentLength();
        if (contentLength <= this.partSizeInBytes || contentLength <= this.uploadThreshold) {
            log.debug(() -> "Starting the copy as a single copy part request");
            this.copyInOneChunk(copyObjectRequest, returnFuture);
        } else {
            log.debug(() -> "Starting the copy as multipart copy request");
            this.copyInParts(copyObjectRequest, contentLength, returnFuture);
        }
    }

    private void copyInParts(CopyObjectRequest copyObjectRequest, Long contentLength, CompletableFuture<CopyObjectResponse> returnFuture) {
        CreateMultipartUploadRequest request = SdkPojoConversionUtils.toCreateMultipartUploadRequest(copyObjectRequest);
        CompletableFuture<CreateMultipartUploadResponse> createMultipartUploadFuture = this.s3AsyncClient.createMultipartUpload(request);
        CompletableFutureUtils.forwardExceptionTo(returnFuture, createMultipartUploadFuture);
        createMultipartUploadFuture.whenComplete((createMultipartUploadResponse, throwable) -> {
            if (throwable != null) {
                this.genericMultipartHelper.handleException(returnFuture, () -> "Failed to initiate multipart upload", (Throwable)throwable);
            } else {
                log.debug(() -> "Initiated new multipart upload, uploadId: " + createMultipartUploadResponse.uploadId());
                this.doCopyInParts(copyObjectRequest, contentLength, returnFuture, createMultipartUploadResponse.uploadId());
            }
        });
    }

    private void doCopyInParts(CopyObjectRequest copyObjectRequest, Long contentLength, CompletableFuture<CopyObjectResponse> returnFuture, String uploadId) {
        long optimalPartSize = this.genericMultipartHelper.calculateOptimalPartSizeFor(contentLength, this.partSizeInBytes);
        int partCount = this.genericMultipartHelper.determinePartCount(contentLength, optimalPartSize);
        if (optimalPartSize > this.partSizeInBytes) {
            log.debug(() -> String.format("Configured partSize is %d, but using %d to prevent reaching maximum number of parts allowed", this.partSizeInBytes, optimalPartSize));
        }
        log.debug(() -> String.format("Starting multipart copy with partCount: %s, optimalPartSize: %s", partCount, optimalPartSize));
        AtomicReferenceArray<CompletedPart> completedParts = new AtomicReferenceArray<CompletedPart>(partCount);
        List<CompletableFuture<CompletedPart>> futures = this.sendUploadPartCopyRequests(copyObjectRequest, contentLength, uploadId, completedParts, optimalPartSize);
        ((CompletableFuture)((CompletableFuture)CompletableFutureUtils.allOfExceptionForwarded((CompletableFuture[])futures.toArray(new CompletableFuture[0])).thenCompose(ignore -> this.completeMultipartUpload(copyObjectRequest, uploadId, completedParts))).handle(this.genericMultipartHelper.handleExceptionOrResponse(copyObjectRequest, returnFuture, uploadId))).exceptionally(throwable -> {
            this.genericMultipartHelper.handleException(returnFuture, () -> "Unexpected exception occurred", (Throwable)throwable);
            return null;
        });
    }

    private CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(CopyObjectRequest copyObjectRequest, String uploadId, AtomicReferenceArray<CompletedPart> completedParts) {
        log.debug(() -> String.format("Sending completeMultipartUploadRequest, uploadId: %s", uploadId));
        CompletedPart[] parts = (CompletedPart[])IntStream.range(0, completedParts.length()).mapToObj(completedParts::get).toArray(CompletedPart[]::new);
        CompleteMultipartUploadRequest completeMultipartUploadRequest = (CompleteMultipartUploadRequest)((Object)CompleteMultipartUploadRequest.builder().bucket(copyObjectRequest.destinationBucket()).key(copyObjectRequest.destinationKey()).uploadId(uploadId).multipartUpload((CompletedMultipartUpload)CompletedMultipartUpload.builder().parts(parts).build()).sseCustomerAlgorithm(copyObjectRequest.sseCustomerAlgorithm()).sseCustomerKey(copyObjectRequest.sseCustomerKey()).sseCustomerKeyMD5(copyObjectRequest.sseCustomerKeyMD5()).build());
        return this.s3AsyncClient.completeMultipartUpload(completeMultipartUploadRequest);
    }

    private List<CompletableFuture<CompletedPart>> sendUploadPartCopyRequests(CopyObjectRequest copyObjectRequest, long contentLength, String uploadId, AtomicReferenceArray<CompletedPart> completedParts, long optimalPartSize) {
        ArrayList<CompletableFuture<CompletedPart>> futures = new ArrayList<CompletableFuture<CompletedPart>>();
        UploadPartCopyRequestIterable uploadPartCopyRequests = new UploadPartCopyRequestIterable(uploadId, optimalPartSize, copyObjectRequest, contentLength);
        uploadPartCopyRequests.forEach(uploadPartCopyRequest -> this.sendIndividualUploadPartCopy(uploadId, completedParts, (List<CompletableFuture<CompletedPart>>)futures, (UploadPartCopyRequest)((Object)uploadPartCopyRequest)));
        return futures;
    }

    private void sendIndividualUploadPartCopy(String uploadId, AtomicReferenceArray<CompletedPart> completedParts, List<CompletableFuture<CompletedPart>> futures, UploadPartCopyRequest uploadPartCopyRequest) {
        Integer partNumber = uploadPartCopyRequest.partNumber();
        log.debug(() -> "Sending uploadPartCopyRequest with range: " + uploadPartCopyRequest.copySourceRange() + " uploadId: " + uploadId);
        CompletableFuture<UploadPartCopyResponse> uploadPartCopyFuture = this.s3AsyncClient.uploadPartCopy(uploadPartCopyRequest);
        CompletionStage convertFuture = uploadPartCopyFuture.thenApply(uploadPartCopyResponse -> CopyObjectHelper.convertUploadPartCopyResponse(completedParts, partNumber, uploadPartCopyResponse));
        futures.add((CompletableFuture<CompletedPart>)convertFuture);
        CompletableFutureUtils.forwardExceptionTo((CompletableFuture)convertFuture, uploadPartCopyFuture);
    }

    private static CompletedPart convertUploadPartCopyResponse(AtomicReferenceArray<CompletedPart> completedParts, Integer partNumber, UploadPartCopyResponse uploadPartCopyResponse) {
        CopyPartResult copyPartResult = uploadPartCopyResponse.copyPartResult();
        CompletedPart completedPart = SdkPojoConversionUtils.toCompletedPart(copyPartResult, (int)partNumber);
        completedParts.set(partNumber - 1, completedPart);
        return completedPart;
    }

    private void copyInOneChunk(CopyObjectRequest copyObjectRequest, CompletableFuture<CopyObjectResponse> returnFuture) {
        CompletableFuture<CopyObjectResponse> copyObjectFuture = this.s3AsyncClient.copyObject(copyObjectRequest);
        CompletableFutureUtils.forwardExceptionTo(returnFuture, copyObjectFuture);
        CompletableFutureUtils.forwardResultTo(copyObjectFuture, returnFuture);
    }
}

