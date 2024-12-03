/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.SdkClientException;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListenerChain;
import com.amazonaws.event.SDKProgressPublisher;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.internal.UploadMonitor;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CompleteMultipartUpload
implements Callable<UploadResult> {
    private final String uploadId;
    private final AmazonS3 s3;
    private final PutObjectRequest origReq;
    private final Future<List<PartETag>> partFutures;
    private final List<PartETag> eTagsBeforeResume;
    private final UploadMonitor monitor;
    private final ProgressListenerChain listener;

    public CompleteMultipartUpload(String uploadId, AmazonS3 s3, PutObjectRequest putObjectRequest, Future<List<PartETag>> partFutures, List<PartETag> eTagsBeforeResume, ProgressListenerChain progressListenerChain, UploadMonitor monitor) {
        this.uploadId = uploadId;
        this.s3 = s3;
        this.origReq = putObjectRequest;
        this.partFutures = partFutures;
        this.eTagsBeforeResume = eTagsBeforeResume;
        this.listener = progressListenerChain;
        this.monitor = monitor;
    }

    @Override
    public UploadResult call() throws Exception {
        CompleteMultipartUploadResult res;
        try {
            CompleteMultipartUploadRequest req = (CompleteMultipartUploadRequest)((AmazonWebServiceRequest)((AmazonWebServiceRequest)new CompleteMultipartUploadRequest(this.origReq.getBucketName(), this.origReq.getKey(), this.uploadId, this.collectPartETags()).withRequesterPays(this.origReq.isRequesterPays()).withGeneralProgressListener(this.origReq.getGeneralProgressListener())).withRequestMetricCollector(this.origReq.getRequestMetricCollector())).withRequestCredentialsProvider(this.origReq.getRequestCredentialsProvider());
            res = this.s3.completeMultipartUpload(req);
        }
        catch (Exception e) {
            this.monitor.setTransferStateToFailed();
            SDKProgressPublisher.publishProgress(this.listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            this.partFutures.cancel(false);
            throw e;
        }
        UploadResult uploadResult = new UploadResult();
        uploadResult.setBucketName(this.origReq.getBucketName());
        uploadResult.setKey(this.origReq.getKey());
        uploadResult.setETag(res.getETag());
        uploadResult.setVersionId(res.getVersionId());
        this.monitor.setTransferStateToCompleted();
        return uploadResult;
    }

    private List<PartETag> collectPartETags() {
        ArrayList<PartETag> partETags = new ArrayList<PartETag>(this.eTagsBeforeResume);
        try {
            partETags.addAll((Collection<PartETag>)this.partFutures.get());
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to complete multi-part upload. Individual part upload failed: " + e.getCause().getMessage(), e.getCause());
        }
        return partETags;
    }
}

