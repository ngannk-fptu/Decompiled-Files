/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.async.SdkPublisher
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import java.util.Collection;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.multipart.GenericMultipartHelper;
import software.amazon.awssdk.services.s3.internal.multipart.MultipartUploadHelper;
import software.amazon.awssdk.services.s3.internal.multipart.SdkPojoConversionUtils;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public final class UploadWithUnknownContentLengthHelper {
    private static final Logger log = Logger.loggerFor(UploadWithUnknownContentLengthHelper.class);
    private final S3AsyncClient s3AsyncClient;
    private final long partSizeInBytes;
    private final GenericMultipartHelper<PutObjectRequest, PutObjectResponse> genericMultipartHelper;
    private final long maxMemoryUsageInBytes;
    private final long multipartUploadThresholdInBytes;
    private final MultipartUploadHelper multipartUploadHelper;

    public UploadWithUnknownContentLengthHelper(S3AsyncClient s3AsyncClient, long partSizeInBytes, long multipartUploadThresholdInBytes, long maxMemoryUsageInBytes) {
        this.s3AsyncClient = s3AsyncClient;
        this.partSizeInBytes = partSizeInBytes;
        this.genericMultipartHelper = new GenericMultipartHelper<PutObjectRequest, PutObjectResponse>(s3AsyncClient, SdkPojoConversionUtils::toAbortMultipartUploadRequest, SdkPojoConversionUtils::toPutObjectResponse);
        this.maxMemoryUsageInBytes = maxMemoryUsageInBytes;
        this.multipartUploadThresholdInBytes = multipartUploadThresholdInBytes;
        this.multipartUploadHelper = new MultipartUploadHelper(s3AsyncClient, partSizeInBytes, multipartUploadThresholdInBytes, maxMemoryUsageInBytes);
    }

    public CompletableFuture<PutObjectResponse> uploadObject(PutObjectRequest putObjectRequest, AsyncRequestBody asyncRequestBody) {
        CompletableFuture<PutObjectResponse> returnFuture = new CompletableFuture<PutObjectResponse>();
        SdkPublisher splitAsyncRequestBodyResponse = asyncRequestBody.split(b -> b.chunkSizeInBytes(Long.valueOf(this.partSizeInBytes)).bufferSizeInBytes(Long.valueOf(this.maxMemoryUsageInBytes)));
        splitAsyncRequestBodyResponse.subscribe((Subscriber)new UnknownContentLengthAsyncRequestBodySubscriber(this.partSizeInBytes, putObjectRequest, returnFuture));
        return returnFuture;
    }

    private class UnknownContentLengthAsyncRequestBodySubscriber
    implements Subscriber<AsyncRequestBody> {
        private final AtomicBoolean isFirstAsyncRequestBody = new AtomicBoolean(true);
        private final AtomicBoolean createMultipartUploadInitiated = new AtomicBoolean(false);
        private final AtomicBoolean completedMultipartInitiated = new AtomicBoolean(false);
        private final AtomicInteger asyncRequestBodyInFlight = new AtomicInteger(0);
        private final AtomicBoolean failureActionInitiated = new AtomicBoolean(false);
        private AtomicInteger partNumber = new AtomicInteger(1);
        private final Queue<CompletedPart> completedParts = new ConcurrentLinkedQueue<CompletedPart>();
        private final Collection<CompletableFuture<CompletedPart>> futures = new ConcurrentLinkedQueue<CompletableFuture<CompletedPart>>();
        private final CompletableFuture<String> uploadIdFuture = new CompletableFuture();
        private final long maximumChunkSizeInByte;
        private final PutObjectRequest putObjectRequest;
        private final CompletableFuture<PutObjectResponse> returnFuture;
        private Subscription subscription;
        private AsyncRequestBody firstRequestBody;
        private String uploadId;
        private volatile boolean isDone;

        UnknownContentLengthAsyncRequestBodySubscriber(long maximumChunkSizeInByte, PutObjectRequest putObjectRequest, CompletableFuture<PutObjectResponse> returnFuture) {
            this.maximumChunkSizeInByte = maximumChunkSizeInByte;
            this.putObjectRequest = putObjectRequest;
            this.returnFuture = returnFuture;
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
                    UploadWithUnknownContentLengthHelper.this.multipartUploadHelper;
                    MultipartUploadHelper.cancelingOtherOngoingRequests(this.futures, t);
                }
            });
        }

        public void onNext(AsyncRequestBody asyncRequestBody) {
            log.trace(() -> "Received asyncRequestBody " + asyncRequestBody.contentLength());
            this.asyncRequestBodyInFlight.incrementAndGet();
            if (this.isFirstAsyncRequestBody.compareAndSet(true, false)) {
                log.trace(() -> "Received first async request body");
                this.firstRequestBody = asyncRequestBody;
                this.subscription.request(1L);
                return;
            }
            if (this.createMultipartUploadInitiated.compareAndSet(false, true)) {
                log.debug(() -> "Starting the upload as multipart upload request");
                CompletableFuture<CreateMultipartUploadResponse> createMultipartUploadFuture = UploadWithUnknownContentLengthHelper.this.multipartUploadHelper.createMultipartUpload(this.putObjectRequest, this.returnFuture);
                createMultipartUploadFuture.whenComplete((createMultipartUploadResponse, throwable) -> {
                    if (throwable != null) {
                        UploadWithUnknownContentLengthHelper.this.genericMultipartHelper.handleException(this.returnFuture, () -> "Failed to initiate multipart upload", (Throwable)throwable);
                        this.subscription.cancel();
                    } else {
                        this.uploadId = createMultipartUploadResponse.uploadId();
                        log.debug(() -> "Initiated a new multipart upload, uploadId: " + this.uploadId);
                        this.sendUploadPartRequest(this.uploadId, this.firstRequestBody);
                        this.sendUploadPartRequest(this.uploadId, asyncRequestBody);
                        this.uploadIdFuture.complete(this.uploadId);
                    }
                });
                CompletableFutureUtils.forwardExceptionTo(this.returnFuture, createMultipartUploadFuture);
            } else {
                this.uploadIdFuture.whenComplete((r, t) -> this.sendUploadPartRequest(this.uploadId, asyncRequestBody));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void sendUploadPartRequest(String uploadId, AsyncRequestBody asyncRequestBody) {
            UploadWithUnknownContentLengthHelper.this.multipartUploadHelper.sendIndividualUploadPartRequest(uploadId, this.completedParts::add, this.futures, this.uploadPart(asyncRequestBody)).whenComplete((r, t) -> {
                if (t != null) {
                    if (this.failureActionInitiated.compareAndSet(false, true)) {
                        UploadWithUnknownContentLengthHelper.this.multipartUploadHelper.failRequestsElegantly(this.futures, (Throwable)t, uploadId, this.returnFuture, this.putObjectRequest);
                    }
                } else {
                    this.completeMultipartUploadIfFinish(this.asyncRequestBodyInFlight.decrementAndGet());
                }
            });
            UnknownContentLengthAsyncRequestBodySubscriber unknownContentLengthAsyncRequestBodySubscriber = this;
            synchronized (unknownContentLengthAsyncRequestBodySubscriber) {
                this.subscription.request(1L);
            }
        }

        private Pair<UploadPartRequest, AsyncRequestBody> uploadPart(AsyncRequestBody asyncRequestBody) {
            UploadPartRequest uploadRequest = SdkPojoConversionUtils.toUploadPartRequest(this.putObjectRequest, this.partNumber.getAndIncrement(), this.uploadId);
            return Pair.of((Object)((Object)uploadRequest), (Object)asyncRequestBody);
        }

        public void onError(Throwable t) {
            log.debug(() -> "Received onError() ", t);
            if (this.failureActionInitiated.compareAndSet(false, true)) {
                UploadWithUnknownContentLengthHelper.this.multipartUploadHelper.failRequestsElegantly(this.futures, t, this.uploadId, this.returnFuture, this.putObjectRequest);
            }
        }

        public void onComplete() {
            log.debug(() -> "Received onComplete()");
            if (!this.createMultipartUploadInitiated.get()) {
                log.debug(() -> "Starting the upload as a single object upload request");
                UploadWithUnknownContentLengthHelper.this.multipartUploadHelper.uploadInOneChunk(this.putObjectRequest, this.firstRequestBody, this.returnFuture);
            } else {
                this.isDone = true;
                this.completeMultipartUploadIfFinish(this.asyncRequestBodyInFlight.get());
            }
        }

        private void completeMultipartUploadIfFinish(int requestsInFlight) {
            if (this.isDone && requestsInFlight == 0 && this.completedMultipartInitiated.compareAndSet(false, true)) {
                CompletedPart[] parts = (CompletedPart[])this.completedParts.stream().sorted(Comparator.comparingInt(CompletedPart::partNumber)).toArray(CompletedPart[]::new);
                UploadWithUnknownContentLengthHelper.this.multipartUploadHelper.completeMultipartUpload(this.returnFuture, this.uploadId, parts, this.putObjectRequest);
            }
        }
    }
}

