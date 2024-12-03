/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListenerChain;
import com.amazonaws.event.SDKProgressPublisher;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionV2;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.EncryptedInitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.EncryptedPutObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ListPartsRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.PartSummary;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.internal.S3ProgressPublisher;
import com.amazonaws.services.s3.transfer.internal.TransferManagerUtils;
import com.amazonaws.services.s3.transfer.internal.UploadImpl;
import com.amazonaws.services.s3.transfer.internal.UploadPartCallable;
import com.amazonaws.services.s3.transfer.internal.UploadPartRequestFactory;
import com.amazonaws.services.s3.transfer.internal.future.CompletedFuture;
import com.amazonaws.services.s3.transfer.internal.future.CompositeFuture;
import com.amazonaws.services.s3.transfer.internal.future.DelegatingFuture;
import com.amazonaws.services.s3.transfer.internal.future.FutureImpl;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UploadCallable
implements Callable<UploadResult> {
    private static final Log log = LogFactory.getLog(UploadCallable.class);
    private final AmazonS3 s3;
    private final ExecutorService threadPool;
    private final PutObjectRequest origReq;
    private final FutureImpl<String> multipartUploadId = new FutureImpl();
    private final UploadImpl upload;
    private final TransferManagerConfiguration configuration;
    private final DelegatingFuture<List<PartETag>> partsFuture = new DelegatingFuture();
    private final ProgressListenerChain listener;
    private final TransferProgress transferProgress;
    private final List<PartETag> eTagsToSkip = new ArrayList<PartETag>();
    private final int MULTIPART_UPLOAD_ID_RETRIEVAL_TIMEOUT_SECONDS = 30;
    private PersistableUpload persistableUpload;
    private final AtomicReference<State> state = new AtomicReference<State>(State.BEFORE_INITIATE);
    private final AtomicBoolean abortRequestSent = new AtomicBoolean(false);

    public UploadCallable(TransferManager transferManager, ExecutorService threadPool, UploadImpl upload, PutObjectRequest origReq, ProgressListenerChain progressListenerChain, String uploadId, TransferProgress transferProgress) {
        this.s3 = transferManager.getAmazonS3Client();
        this.configuration = transferManager.getConfiguration();
        this.threadPool = threadPool;
        this.origReq = origReq;
        this.listener = progressListenerChain;
        this.upload = upload;
        this.transferProgress = transferProgress;
        if (uploadId != null) {
            this.multipartUploadId.complete(uploadId);
        }
    }

    Future<List<PartETag>> getFutures() {
        return this.partsFuture;
    }

    List<PartETag> getETags() {
        return this.eTagsToSkip;
    }

    String getMultipartUploadId() {
        return this.multipartUploadId.getOrThrowUnchecked("Failed to retrieve multipart upload ID.");
    }

    public boolean isMultipartUpload() {
        return TransferManagerUtils.shouldUseMultipartUpload(this.origReq, this.configuration);
    }

    @Override
    public UploadResult call() throws Exception {
        try {
            this.upload.setState(Transfer.TransferState.InProgress);
            if (this.isMultipartUpload()) {
                SDKProgressPublisher.publishProgress(this.listener, ProgressEventType.TRANSFER_STARTED_EVENT);
                UploadResult uploadResult = this.uploadInParts();
                return uploadResult;
            }
            UploadResult uploadResult = this.uploadInOneChunk();
            return uploadResult;
        }
        finally {
            this.partsFuture.setDelegateIfUnset(new CompletedFuture(Collections.emptyList()));
            this.multipartUploadId.complete(null);
        }
    }

    private UploadResult uploadInOneChunk() {
        this.multipartUploadId.complete(null);
        PutObjectResult putObjectResult = this.s3.putObject(this.origReq);
        UploadResult uploadResult = new UploadResult();
        uploadResult.setBucketName(this.origReq.getBucketName());
        uploadResult.setKey(this.origReq.getKey());
        uploadResult.setETag(putObjectResult.getETag());
        uploadResult.setVersionId(putObjectResult.getVersionId());
        return uploadResult;
    }

    private void captureUploadStateIfPossible(String multipartUploadId) {
        if (this.origReq.getSSECustomerKey() == null) {
            this.persistableUpload = new PersistableUpload(this.origReq.getBucketName(), this.origReq.getKey(), this.origReq.getFile().getAbsolutePath(), multipartUploadId, this.configuration.getMinimumUploadPartSize(), this.configuration.getMultipartUploadThreshold());
            this.notifyPersistableTransferAvailability();
        }
    }

    public PersistableUpload getPersistableUpload() {
        return this.persistableUpload;
    }

    private void notifyPersistableTransferAvailability() {
        S3ProgressPublisher.publishTransferPersistable(this.listener, this.persistableUpload);
    }

    private UploadResult uploadInParts() throws Exception {
        boolean isUsingEncryption = this.s3 instanceof AmazonS3Encryption || this.s3 instanceof AmazonS3EncryptionV2;
        long optimalPartSize = this.getOptimalPartSize(isUsingEncryption);
        try {
            String uploadId = this.multipartUploadId.isDone() ? this.multipartUploadId.get() : this.initiateMultipartUpload(this.origReq, isUsingEncryption);
            UploadPartRequestFactory requestFactory = new UploadPartRequestFactory(this.origReq, uploadId, optimalPartSize);
            if (TransferManagerUtils.isUploadParallelizable(this.origReq, isUsingEncryption)) {
                this.captureUploadStateIfPossible(uploadId);
                this.uploadPartsInParallel(requestFactory, uploadId);
                UploadResult uploadResult = null;
                return uploadResult;
            }
            UploadResult uploadResult = this.uploadPartsInSeries(requestFactory, uploadId);
            return uploadResult;
        }
        catch (Exception e) {
            SDKProgressPublisher.publishProgress(this.listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            if (this.multipartUploadId.isDone()) {
                this.abortMultipartUpload(this.multipartUploadId.get());
            }
            throw e;
        }
        finally {
            if (this.origReq.getInputStream() != null) {
                try {
                    this.origReq.getInputStream().close();
                }
                catch (Exception e) {
                    log.warn((Object)("Unable to cleanly close input stream: " + e.getMessage()), (Throwable)e);
                }
            }
        }
    }

    void safelyAbortMultipartUpload(Future<?> future) {
        if (this.multipartUploadId.isDone()) {
            this.state.set(State.ABORTED);
            this.abortMultipartUpload(this.getUploadIdOrTimeout());
        } else if (!this.state.compareAndSet(State.BEFORE_INITIATE, State.ABORTED) && this.state.compareAndSet(State.INITIATED, State.ABORTED)) {
            this.abortMultipartUpload(this.getUploadIdOrTimeout());
        }
        future.cancel(true);
    }

    private String getUploadIdOrTimeout() {
        try {
            return this.multipartUploadId.get(30L, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to retrieve an upload ID after 30 seconds.");
        }
    }

    private void abortMultipartUpload(String multipartUploadId) {
        if (multipartUploadId == null) {
            return;
        }
        if (!this.abortRequestSent.compareAndSet(false, true)) {
            return;
        }
        try {
            AbortMultipartUploadRequest abortRequest = (AbortMultipartUploadRequest)new AbortMultipartUploadRequest(this.origReq.getBucketName(), this.origReq.getKey(), multipartUploadId).withRequesterPays(this.origReq.isRequesterPays()).withRequestCredentialsProvider(this.origReq.getRequestCredentialsProvider());
            this.s3.abortMultipartUpload(abortRequest);
        }
        catch (Exception e2) {
            log.info((Object)("Unable to abort multipart upload, you may need to manually remove uploaded parts: " + e2.getMessage()), (Throwable)e2);
        }
    }

    private long getOptimalPartSize(boolean isUsingEncryption) {
        long optimalPartSize = TransferManagerUtils.calculateOptimalPartSize(this.origReq, this.configuration);
        if (isUsingEncryption && optimalPartSize % 32L > 0L) {
            optimalPartSize = optimalPartSize - optimalPartSize % 32L + 32L;
        }
        log.debug((Object)("Calculated optimal part size: " + optimalPartSize));
        return optimalPartSize;
    }

    private UploadResult uploadPartsInSeries(UploadPartRequestFactory requestFactory, String multipartUploadId) {
        ArrayList<PartETag> partETags = new ArrayList<PartETag>();
        while (requestFactory.hasMoreRequests()) {
            if (this.threadPool.isShutdown()) {
                throw new CancellationException("TransferManager has been shutdown");
            }
            UploadPartRequest uploadPartRequest = requestFactory.getNextUploadPartRequest();
            InputStream inputStream = uploadPartRequest.getInputStream();
            if (inputStream != null && inputStream.markSupported()) {
                if (uploadPartRequest.getPartSize() >= Integer.MAX_VALUE) {
                    inputStream.mark(Integer.MAX_VALUE);
                } else {
                    inputStream.mark((int)uploadPartRequest.getPartSize());
                }
            }
            partETags.add(this.s3.uploadPart(uploadPartRequest).getPartETag());
        }
        CompleteMultipartUploadRequest req = (CompleteMultipartUploadRequest)((AmazonWebServiceRequest)((AmazonWebServiceRequest)new CompleteMultipartUploadRequest(this.origReq.getBucketName(), this.origReq.getKey(), multipartUploadId, partETags).withRequesterPays(this.origReq.isRequesterPays()).withGeneralProgressListener(this.origReq.getGeneralProgressListener())).withRequestMetricCollector(this.origReq.getRequestMetricCollector())).withRequestCredentialsProvider(this.origReq.getRequestCredentialsProvider());
        CompleteMultipartUploadResult res = this.s3.completeMultipartUpload(req);
        UploadResult uploadResult = new UploadResult();
        uploadResult.setBucketName(res.getBucketName());
        uploadResult.setKey(res.getKey());
        uploadResult.setETag(res.getETag());
        uploadResult.setVersionId(res.getVersionId());
        return uploadResult;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void uploadPartsInParallel(UploadPartRequestFactory requestFactory, String uploadId) {
        Map<Integer, PartSummary> partNumbers = this.identifyExistingPartsForResume(uploadId);
        ArrayList<Future<PartETag>> futures = new ArrayList<Future<PartETag>>();
        try {
            while (requestFactory.hasMoreRequests()) {
                if (this.threadPool.isShutdown()) {
                    throw new CancellationException("TransferManager has been shutdown");
                }
                UploadPartRequest request = requestFactory.getNextUploadPartRequest();
                if (partNumbers.containsKey(request.getPartNumber())) {
                    PartSummary summary = partNumbers.get(request.getPartNumber());
                    this.eTagsToSkip.add(new PartETag(request.getPartNumber(), summary.getETag()));
                    this.transferProgress.updateProgress(summary.getSize());
                    continue;
                }
                futures.add(this.threadPool.submit(new UploadPartCallable(this.s3, request, this.shouldCalculatePartMd5())));
            }
        }
        finally {
            this.partsFuture.setDelegate(new CompositeFuture(futures));
        }
    }

    private Map<Integer, PartSummary> identifyExistingPartsForResume(String uploadId) {
        HashMap<Integer, PartSummary> partNumbers = new HashMap<Integer, PartSummary>();
        if (uploadId == null) {
            return partNumbers;
        }
        int partNumber = 0;
        while (true) {
            ListPartsRequest listPartsRequest = (ListPartsRequest)new ListPartsRequest(this.origReq.getBucketName(), this.origReq.getKey(), uploadId).withPartNumberMarker(partNumber).withRequesterPays(this.origReq.isRequesterPays()).withRequestCredentialsProvider(this.origReq.getRequestCredentialsProvider());
            PartListing parts = this.s3.listParts(listPartsRequest);
            for (PartSummary partSummary : parts.getParts()) {
                partNumbers.put(partSummary.getPartNumber(), partSummary);
            }
            if (!parts.isTruncated()) {
                return partNumbers;
            }
            partNumber = parts.getNextPartNumberMarker();
        }
    }

    private String initiateMultipartUpload(PutObjectRequest origReq, boolean isUsingEncryption) {
        String uploadId;
        InitiateMultipartUploadRequest req = null;
        if (isUsingEncryption && origReq instanceof EncryptedPutObjectRequest) {
            req = new EncryptedInitiateMultipartUploadRequest(origReq.getBucketName(), origReq.getKey()).withCannedACL(origReq.getCannedAcl()).withObjectMetadata(origReq.getMetadata());
            ((EncryptedInitiateMultipartUploadRequest)req).setMaterialsDescription(((EncryptedPutObjectRequest)origReq).getMaterialsDescription());
        } else {
            req = new InitiateMultipartUploadRequest(origReq.getBucketName(), origReq.getKey()).withCannedACL(origReq.getCannedAcl()).withObjectMetadata(origReq.getMetadata());
        }
        req.withTagging(origReq.getTagging());
        TransferManager.appendMultipartUserAgent(req);
        ((AmazonWebServiceRequest)req.withAccessControlList(origReq.getAccessControlList()).withRequesterPays(origReq.isRequesterPays()).withStorageClass(origReq.getStorageClass()).withRedirectLocation(origReq.getRedirectLocation()).withSSECustomerKey(origReq.getSSECustomerKey()).withSSEAwsKeyManagementParams(origReq.getSSEAwsKeyManagementParams()).withGeneralProgressListener(origReq.getGeneralProgressListener())).withRequestMetricCollector(origReq.getRequestMetricCollector());
        req.withObjectLockMode(origReq.getObjectLockMode()).withObjectLockRetainUntilDate(origReq.getObjectLockRetainUntilDate()).withObjectLockLegalHoldStatus(origReq.getObjectLockLegalHoldStatus());
        req.withRequestCredentialsProvider(origReq.getRequestCredentialsProvider());
        if (!this.state.compareAndSet(State.BEFORE_INITIATE, State.INITIATED)) {
            throw new IllegalStateException("Failed to update state to " + (Object)((Object)State.INITIATED) + " (State: " + (Object)((Object)this.state.get()) + ")");
        }
        try {
            uploadId = this.s3.initiateMultipartUpload(req).getUploadId();
            this.multipartUploadId.complete(uploadId);
        }
        catch (RuntimeException t) {
            this.multipartUploadId.complete(null);
            throw t;
        }
        catch (Error t) {
            this.multipartUploadId.complete(null);
            throw t;
        }
        log.debug((Object)("Initiated new multipart upload: " + uploadId));
        return uploadId;
    }

    private boolean shouldCalculatePartMd5() {
        return this.origReq.getObjectLockMode() != null || this.origReq.getObjectLockRetainUntilDate() != null || this.origReq.getObjectLockLegalHoldStatus() != null || this.configuration.isAlwaysCalculateMultipartMd5();
    }

    private static enum State {
        BEFORE_INITIATE,
        INITIATED,
        ABORTED;

    }
}

