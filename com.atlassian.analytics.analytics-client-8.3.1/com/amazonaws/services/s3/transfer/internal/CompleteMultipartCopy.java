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
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.transfer.internal.CopyMonitor;
import com.amazonaws.services.s3.transfer.model.CopyResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CompleteMultipartCopy
implements Callable<CopyResult> {
    private final String uploadId;
    private final AmazonS3 s3;
    private final CopyObjectRequest origReq;
    private final List<Future<PartETag>> futures;
    private final CopyMonitor monitor;
    private final ProgressListenerChain listener;

    public CompleteMultipartCopy(String uploadId, AmazonS3 s3, CopyObjectRequest copyObjectRequest, List<Future<PartETag>> futures, ProgressListenerChain progressListenerChain, CopyMonitor monitor) {
        this.uploadId = uploadId;
        this.s3 = s3;
        this.origReq = copyObjectRequest;
        this.futures = futures;
        this.listener = progressListenerChain;
        this.monitor = monitor;
    }

    @Override
    public CopyResult call() throws Exception {
        CompleteMultipartUploadResult res;
        try {
            CompleteMultipartUploadRequest req = (CompleteMultipartUploadRequest)((AmazonWebServiceRequest)((AmazonWebServiceRequest)new CompleteMultipartUploadRequest(this.origReq.getDestinationBucketName(), this.origReq.getDestinationKey(), this.uploadId, this.collectPartETags()).withRequesterPays(this.origReq.isRequesterPays()).withGeneralProgressListener(this.origReq.getGeneralProgressListener())).withRequestMetricCollector(this.origReq.getRequestMetricCollector())).withRequestCredentialsProvider(this.origReq.getRequestCredentialsProvider());
            res = this.s3.completeMultipartUpload(req);
        }
        catch (Exception e) {
            this.monitor.reportFailure();
            SDKProgressPublisher.publishProgress(this.listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw e;
        }
        CopyResult copyResult = new CopyResult();
        copyResult.setSourceBucketName(this.origReq.getSourceBucketName());
        copyResult.setSourceKey(this.origReq.getSourceKey());
        copyResult.setDestinationBucketName(res.getBucketName());
        copyResult.setDestinationKey(res.getKey());
        copyResult.setETag(res.getETag());
        copyResult.setVersionId(res.getVersionId());
        this.monitor.copyComplete();
        return copyResult;
    }

    private List<PartETag> collectPartETags() {
        ArrayList<PartETag> partETags = new ArrayList<PartETag>();
        for (Future<PartETag> future : this.futures) {
            try {
                partETags.add(future.get());
            }
            catch (Exception e) {
                throw new SdkClientException("Unable to copy part: " + e.getCause().getMessage(), e.getCause());
            }
        }
        return partETags;
    }
}

