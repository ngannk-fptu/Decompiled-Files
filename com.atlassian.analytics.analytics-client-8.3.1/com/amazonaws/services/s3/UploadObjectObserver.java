/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.OnFileDelete;
import com.amazonaws.services.s3.internal.PartCreationEvent;
import com.amazonaws.services.s3.internal.S3DirectSpi;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.EncryptedInitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadObjectRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.commons.logging.LogFactory;

public class UploadObjectObserver {
    private final List<Future<UploadPartResult>> futures = new ArrayList<Future<UploadPartResult>>();
    private UploadObjectRequest req;
    private String uploadId;
    private S3DirectSpi s3direct;
    private AmazonS3 s3;
    private ExecutorService es;

    public UploadObjectObserver init(UploadObjectRequest req, S3DirectSpi s3direct, AmazonS3 s3, ExecutorService es) {
        this.req = req;
        this.s3direct = s3direct;
        this.s3 = s3;
        this.es = es;
        return this;
    }

    protected InitiateMultipartUploadRequest newInitiateMultipartUploadRequest(UploadObjectRequest req) {
        return (InitiateMultipartUploadRequest)((AmazonWebServiceRequest)((AmazonWebServiceRequest)new EncryptedInitiateMultipartUploadRequest(req.getBucketName(), req.getKey(), req.getMetadata()).withMaterialsDescription(req.getMaterialsDescription()).withRedirectLocation(req.getRedirectLocation()).withSSEAwsKeyManagementParams(req.getSSEAwsKeyManagementParams()).withSSECustomerKey(req.getSSECustomerKey()).withStorageClass(req.getStorageClass()).withAccessControlList(req.getAccessControlList()).withCannedACL(req.getCannedAcl()).withGeneralProgressListener(req.getGeneralProgressListener())).withRequestMetricCollector(req.getRequestMetricCollector())).withRequestCredentialsProvider(req.getRequestCredentialsProvider());
    }

    public String onUploadInitiation(UploadObjectRequest req) {
        InitiateMultipartUploadResult res = this.s3.initiateMultipartUpload(this.newInitiateMultipartUploadRequest(req));
        this.uploadId = res.getUploadId();
        return this.uploadId;
    }

    public void onPartCreate(PartCreationEvent event) {
        final File part = event.getPart();
        final UploadPartRequest reqUploadPart = this.newUploadPartRequest(event, part);
        final OnFileDelete fileDeleteObserver = event.getFileDeleteObserver();
        this.futures.add(this.es.submit(new Callable<UploadPartResult>(){

            @Override
            public UploadPartResult call() {
                try {
                    UploadPartResult uploadPartResult = UploadObjectObserver.this.uploadPart(reqUploadPart);
                    return uploadPartResult;
                }
                finally {
                    if (!part.delete()) {
                        LogFactory.getLog(this.getClass()).debug((Object)("Ignoring failure to delete file " + part + " which has already been uploaded"));
                    } else if (fileDeleteObserver != null) {
                        fileDeleteObserver.onFileDelete(null);
                    }
                }
            }
        }));
    }

    public CompleteMultipartUploadResult onCompletion(List<PartETag> partETags) {
        return this.s3.completeMultipartUpload((CompleteMultipartUploadRequest)new CompleteMultipartUploadRequest(this.req.getBucketName(), this.req.getKey(), this.uploadId, partETags).withRequestCredentialsProvider(this.req.getRequestCredentialsProvider()));
    }

    public void onAbort() {
        for (Future<UploadPartResult> future : this.getFutures()) {
            future.cancel(true);
        }
        if (this.uploadId != null) {
            try {
                this.s3.abortMultipartUpload(new AbortMultipartUploadRequest(this.req.getBucketName(), this.req.getKey(), this.uploadId));
            }
            catch (Exception e) {
                LogFactory.getLog(this.getClass()).debug((Object)("Failed to abort multi-part upload: " + this.uploadId), (Throwable)e);
            }
        }
    }

    protected UploadPartRequest newUploadPartRequest(PartCreationEvent event, File part) {
        UploadPartRequest reqUploadPart = (UploadPartRequest)new UploadPartRequest().withBucketName(this.req.getBucketName()).withFile(part).withKey(this.req.getKey()).withPartNumber(event.getPartNumber()).withPartSize(part.length()).withLastPart(event.isLastPart()).withUploadId(this.uploadId).withObjectMetadata(this.req.getUploadPartMetadata()).withRequestCredentialsProvider(this.req.getRequestCredentialsProvider());
        return reqUploadPart;
    }

    protected UploadPartResult uploadPart(UploadPartRequest reqUploadPart) {
        return this.s3direct.uploadPart(reqUploadPart);
    }

    protected <X extends AmazonWebServiceRequest> X appendUserAgent(X request, String userAgent) {
        request.getRequestClientOptions().appendUserAgent(userAgent);
        return request;
    }

    public List<Future<UploadPartResult>> getFutures() {
        return this.futures;
    }

    protected UploadObjectRequest getRequest() {
        return this.req;
    }

    protected String getUploadId() {
        return this.uploadId;
    }

    protected S3DirectSpi getS3DirectSpi() {
        return this.s3direct;
    }

    protected AmazonS3 getAmazonS3() {
        return this.s3;
    }

    protected ExecutorService getExecutorService() {
        return this.es;
    }
}

