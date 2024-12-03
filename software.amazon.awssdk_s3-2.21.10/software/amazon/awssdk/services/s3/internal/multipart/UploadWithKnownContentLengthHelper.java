/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.multipart.GenericMultipartHelper;
import software.amazon.awssdk.services.s3.internal.multipart.MultipartUploadHelper;
import software.amazon.awssdk.services.s3.internal.multipart.SdkPojoConversionUtils;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public final class UploadWithKnownContentLengthHelper {
    private static final Logger log = Logger.loggerFor(UploadWithKnownContentLengthHelper.class);
    private final S3AsyncClient s3AsyncClient;
    private final long partSizeInBytes;
    private final GenericMultipartHelper<PutObjectRequest, PutObjectResponse> genericMultipartHelper;
    private final long maxMemoryUsageInBytes;
    private final long multipartUploadThresholdInBytes;
    private final MultipartUploadHelper multipartUploadHelper;

    public UploadWithKnownContentLengthHelper(S3AsyncClient s3AsyncClient, long partSizeInBytes, long multipartUploadThresholdInBytes, long maxMemoryUsageInBytes) {
        this.s3AsyncClient = s3AsyncClient;
        this.partSizeInBytes = partSizeInBytes;
        this.genericMultipartHelper = new GenericMultipartHelper<PutObjectRequest, PutObjectResponse>(s3AsyncClient, SdkPojoConversionUtils::toAbortMultipartUploadRequest, SdkPojoConversionUtils::toPutObjectResponse);
        this.maxMemoryUsageInBytes = maxMemoryUsageInBytes;
        this.multipartUploadThresholdInBytes = multipartUploadThresholdInBytes;
        this.multipartUploadHelper = new MultipartUploadHelper(s3AsyncClient, partSizeInBytes, multipartUploadThresholdInBytes, maxMemoryUsageInBytes);
    }

    public CompletableFuture<PutObjectResponse> uploadObject(PutObjectRequest putObjectRequest, AsyncRequestBody asyncRequestBody, long contentLength) {
        CompletableFuture<PutObjectResponse> returnFuture = new CompletableFuture<PutObjectResponse>();
        try {
            if (contentLength > this.multipartUploadThresholdInBytes && contentLength > this.partSizeInBytes) {
                log.debug(() -> "Starting the upload as multipart upload request");
                this.uploadInParts(putObjectRequest, contentLength, asyncRequestBody, returnFuture);
            } else {
                log.debug(() -> "Starting the upload as a single upload part request");
                this.multipartUploadHelper.uploadInOneChunk(putObjectRequest, asyncRequestBody, returnFuture);
            }
        }
        catch (Throwable throwable) {
            returnFuture.completeExceptionally(throwable);
        }
        return returnFuture;
    }

    private void uploadInParts(PutObjectRequest putObjectRequest, long contentLength, AsyncRequestBody asyncRequestBody, CompletableFuture<PutObjectResponse> returnFuture) {
        CompletableFuture<CreateMultipartUploadResponse> createMultipartUploadFuture = this.multipartUploadHelper.createMultipartUpload(putObjectRequest, returnFuture);
        createMultipartUploadFuture.whenComplete((createMultipartUploadResponse, throwable) -> {
            if (throwable != null) {
                this.genericMultipartHelper.handleException(returnFuture, () -> "Failed to initiate multipart upload", (Throwable)throwable);
            } else {
                log.debug(() -> "Initiated a new multipart upload, uploadId: " + createMultipartUploadResponse.uploadId());
                this.doUploadInParts((Pair<PutObjectRequest, AsyncRequestBody>)Pair.of((Object)((Object)putObjectRequest), (Object)asyncRequestBody), contentLength, returnFuture, createMultipartUploadResponse.uploadId());
            }
        });
    }

    private void doUploadInParts(Pair<PutObjectRequest, AsyncRequestBody> request, long contentLength, CompletableFuture<PutObjectResponse> returnFuture, String uploadId) {
        long optimalPartSize = this.genericMultipartHelper.calculateOptimalPartSizeFor(contentLength, this.partSizeInBytes);
        int partCount = this.genericMultipartHelper.determinePartCount(contentLength, optimalPartSize);
        if (optimalPartSize > this.partSizeInBytes) {
            log.debug(() -> String.format("Configured partSize is %d, but using %d to prevent reaching maximum number of parts allowed", this.partSizeInBytes, optimalPartSize));
        }
        log.debug(() -> String.format("Starting multipart upload with partCount: %d, optimalPartSize: %d", partCount, optimalPartSize));
        MpuRequestContext mpuRequestContext = new MpuRequestContext(request, contentLength, optimalPartSize, uploadId);
        ((AsyncRequestBody)request.right()).split(b -> b.chunkSizeInBytes(Long.valueOf(mpuRequestContext.partSize)).bufferSizeInBytes(Long.valueOf(this.maxMemoryUsageInBytes))).subscribe((Subscriber)new KnownContentLengthAsyncRequestBodySubscriber(mpuRequestContext, returnFuture));
    }

    private class KnownContentLengthAsyncRequestBodySubscriber
    implements Subscriber<AsyncRequestBody> {
        private final AtomicInteger asyncRequestBodyInFlight = new AtomicInteger(0);
        private final AtomicBoolean completedMultipartInitiated = new AtomicBoolean(false);
        private final AtomicBoolean failureActionInitiated = new AtomicBoolean(false);
        private final AtomicInteger partNumber = new AtomicInteger(1);
        private final AtomicReferenceArray<CompletedPart> completedParts;
        private final String uploadId;
        private final Collection<CompletableFuture<CompletedPart>> futures = new ConcurrentLinkedQueue<CompletableFuture<CompletedPart>>();
        private final PutObjectRequest putObjectRequest;
        private final CompletableFuture<PutObjectResponse> returnFuture;
        private Subscription subscription;
        private volatile boolean isDone;

        KnownContentLengthAsyncRequestBodySubscriber(MpuRequestContext mpuRequestContext, CompletableFuture<PutObjectResponse> returnFuture) {
            long optimalPartSize = UploadWithKnownContentLengthHelper.this.genericMultipartHelper.calculateOptimalPartSizeFor(mpuRequestContext.contentLength, UploadWithKnownContentLengthHelper.this.partSizeInBytes);
            int partCount = UploadWithKnownContentLengthHelper.this.genericMultipartHelper.determinePartCount(mpuRequestContext.contentLength, optimalPartSize);
            this.putObjectRequest = (PutObjectRequest)((Object)mpuRequestContext.request.left());
            this.returnFuture = returnFuture;
            this.completedParts = new AtomicReferenceArray(partCount);
            this.uploadId = mpuRequestContext.uploadId;
        }

        public void onSubscribe(Subscription s) {
            if (this.subscription != null) {
                log.warn(() -> "The subscriber has already been subscribed. Cancelling the incoming subscription");
                this.subscription.cancel();
                return;
            }
            this.subscription = s;
            s.request(1L);
            this.returnFuture.whenComplete((r, t) -> {
                if (t != null) {
                    s.cancel();
                    if (this.failureActionInitiated.compareAndSet(false, true)) {
                        UploadWithKnownContentLengthHelper.this.multipartUploadHelper.failRequestsElegantly(this.futures, (Throwable)t, this.uploadId, this.returnFuture, this.putObjectRequest);
                    }
                }
            });
        }

        public void onNext(AsyncRequestBody asyncRequestBody) {
            log.trace(() -> "Received asyncRequestBody " + asyncRequestBody.contentLength());
            this.asyncRequestBodyInFlight.incrementAndGet();
            UploadPartRequest uploadRequest = SdkPojoConversionUtils.toUploadPartRequest(this.putObjectRequest, this.partNumber.getAndIncrement(), this.uploadId);
            Consumer<CompletedPart> completedPartConsumer = completedPart -> this.completedParts.set(completedPart.partNumber() - 1, (CompletedPart)completedPart);
            UploadWithKnownContentLengthHelper.this.multipartUploadHelper.sendIndividualUploadPartRequest(this.uploadId, completedPartConsumer, this.futures, (Pair<UploadPartRequest, AsyncRequestBody>)Pair.of((Object)((Object)uploadRequest), (Object)asyncRequestBody)).whenComplete((r, t) -> {
                if (t != null) {
                    if (this.failureActionInitiated.compareAndSet(false, true)) {
                        UploadWithKnownContentLengthHelper.this.multipartUploadHelper.failRequestsElegantly(this.futures, (Throwable)t, this.uploadId, this.returnFuture, this.putObjectRequest);
                    }
                } else {
                    this.completeMultipartUploadIfFinish(this.asyncRequestBodyInFlight.decrementAndGet());
                }
            });
            this.subscription.request(1L);
        }

        public void onError(Throwable t) {
            log.debug(() -> "Received onError ", t);
            if (this.failureActionInitiated.compareAndSet(false, true)) {
                UploadWithKnownContentLengthHelper.this.multipartUploadHelper.failRequestsElegantly(this.futures, t, this.uploadId, this.returnFuture, this.putObjectRequest);
            }
        }

        public void onComplete() {
            log.debug(() -> "Received onComplete()");
            this.isDone = true;
            this.completeMultipartUploadIfFinish(this.asyncRequestBodyInFlight.get());
        }

        private void completeMultipartUploadIfFinish(int requestsInFlight) {
            if (this.isDone && requestsInFlight == 0 && this.completedMultipartInitiated.compareAndSet(false, true)) {
                CompletedPart[] parts = (CompletedPart[])IntStream.range(0, this.completedParts.length()).mapToObj(this.completedParts::get).toArray(CompletedPart[]::new);
                UploadWithKnownContentLengthHelper.this.multipartUploadHelper.completeMultipartUpload(this.returnFuture, this.uploadId, parts, this.putObjectRequest);
            }
        }
    }

    private static final class MpuRequestContext {
        private final Pair<PutObjectRequest, AsyncRequestBody> request;
        private final long contentLength;
        private final long partSize;
        private final String uploadId;

        private MpuRequestContext(Pair<PutObjectRequest, AsyncRequestBody> request, long contentLength, long partSize, String uploadId) {
            this.request = request;
            this.contentLength = contentLength;
            this.partSize = partSize;
            this.uploadId = uploadId;
        }
    }
}

