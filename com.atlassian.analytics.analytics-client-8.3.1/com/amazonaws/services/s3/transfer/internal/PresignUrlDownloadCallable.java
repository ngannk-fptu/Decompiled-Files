/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.AmazonClientException;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.FileLocks;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.exception.FileLockException;
import com.amazonaws.services.s3.transfer.internal.AbstractDownloadCallable;
import com.amazonaws.services.s3.transfer.internal.DownloadMonitor;
import com.amazonaws.services.s3.transfer.internal.DownloadS3ObjectCallable;
import com.amazonaws.services.s3.transfer.internal.PresignedUrlDownloadImpl;
import com.amazonaws.services.s3.transfer.internal.PresignedUrlRetryableDownloadTaskImpl;
import java.io.File;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import javax.net.ssl.SSLProtocolException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public class PresignUrlDownloadCallable
extends AbstractDownloadCallable {
    private static final Log LOG = LogFactory.getLog(PresignUrlDownloadCallable.class);
    private final AmazonS3 s3;
    private final PresignedUrlDownloadRequest request;
    private final PresignedUrlDownloadImpl download;
    private final long perRequestDownloadSize;
    private final Long startByte;
    private final Long endByte;
    private final boolean resumeOnRetry;
    private long expectedFileLength;
    private static boolean testing;

    public PresignUrlDownloadCallable(ExecutorService executor, File dstfile, CountDownLatch latch, PresignedUrlDownloadImpl download, boolean isDownloadParallel, ScheduledExecutorService timedExecutor, long timeout, AmazonS3 s3, PresignedUrlDownloadRequest request, long perRequestDownloadSize, Long startByte, Long endByte, boolean resumeOnRetry) {
        super(PresignUrlDownloadCallable.constructCallableConfig(executor, dstfile, latch, download, isDownloadParallel, timedExecutor, timeout));
        if (s3 == null || request == null || download == null) {
            throw new IllegalArgumentException();
        }
        this.s3 = s3;
        this.request = request;
        this.download = download;
        this.perRequestDownloadSize = perRequestDownloadSize;
        this.startByte = startByte;
        this.endByte = endByte;
        this.resumeOnRetry = resumeOnRetry;
        this.expectedFileLength = 0L;
    }

    @Override
    protected void downloadAsSingleObject() {
        S3Object s3Object = this.retryableDownloadS3ObjectToFile(this.dstfile, new PresignedUrlRetryableDownloadTaskImpl(this.s3, this.download, this.request));
        this.updateDownloadStatus(s3Object);
    }

    @Override
    protected void downloadInParallel() throws Exception {
        this.downloadInParallelUsingRange();
    }

    @Override
    protected void setState(Transfer.TransferState transferState) {
        this.download.setState(transferState);
    }

    private void updateDownloadStatus(S3Object result) {
        if (result == null) {
            this.download.setState(Transfer.TransferState.Canceled);
            this.download.setMonitor(new DownloadMonitor(this.download, null));
        } else {
            this.download.setState(Transfer.TransferState.Completed);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private S3Object retryableDownloadS3ObjectToFile(File file, ServiceUtils.RetryableS3DownloadTask retryableS3DownloadTask) {
        boolean hasRetried = false;
        while (true) {
            S3Object s3Object;
            boolean appendData;
            boolean bl = appendData = this.resumeOnRetry && this.canResumeDownload() && hasRetried;
            if (appendData) {
                this.adjustRequest(this.request);
            }
            if ((s3Object = retryableS3DownloadTask.getS3ObjectStream()) == null) {
                return null;
            }
            try {
                if (testing && !hasRetried) {
                    throw new SdkClientException("testing");
                }
                ServiceUtils.downloadToFile(s3Object, file, retryableS3DownloadTask.needIntegrityCheck(), appendData, this.expectedFileLength);
                S3Object s3Object2 = s3Object;
                return s3Object2;
            }
            catch (AmazonClientException ace) {
                if (!ace.isRetryable()) {
                    throw ace;
                }
                Throwable cause = ace.getCause();
                if (cause instanceof SocketException && !cause.getMessage().equals("Connection reset") || cause instanceof SSLProtocolException) {
                    throw ace;
                }
                if (hasRetried) {
                    throw ace;
                }
                LOG.debug((Object)("Retry the download of object " + s3Object.getKey() + " (bucket " + s3Object.getBucketName() + ")"), (Throwable)ace);
                hasRetried = true;
                continue;
            }
            finally {
                s3Object.getObjectContent().abort();
                continue;
            }
            break;
        }
    }

    private boolean canResumeDownload() {
        return this.startByte != null && this.endByte != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void adjustRequest(PresignedUrlDownloadRequest request) {
        long start = -1L;
        long end = -1L;
        if (request.getRange() != null) {
            long[] range = request.getRange();
            start = range[0];
            end = range[1];
        } else {
            start = this.startByte;
            end = this.endByte;
        }
        if (this.dstfile.exists()) {
            if (!FileLocks.lock(this.dstfile)) {
                throw new FileLockException("Fail to lock " + this.dstfile + " for range adjustment");
            }
            try {
                this.expectedFileLength = this.dstfile.length();
                long newStart = start + this.expectedFileLength;
                LOG.debug((Object)("Adjusting request range from " + Arrays.toString(new long[]{start, end}) + " to " + Arrays.toString(new long[]{newStart, end}) + " for file " + this.dstfile));
                request.setRange(newStart, end);
                long totalBytesToDownload = end - newStart + 1L;
                if (totalBytesToDownload < 0L) {
                    throw new IllegalArgumentException("Unable to determine the range for download operation. lastByte=" + end + ", StartingByte=" + newStart + ", expectedFileLength=" + this.expectedFileLength + ", totalBytesToDownload=" + totalBytesToDownload);
                }
            }
            finally {
                FileLocks.unlock(this.dstfile);
            }
        }
    }

    private void downloadInParallelUsingRange() throws Exception {
        ServiceUtils.createParentDirectoryIfNecessary(this.dstfile);
        if (!FileLocks.lock(this.dstfile)) {
            throw new FileLockException("Fail to lock " + this.dstfile);
        }
        long currentStart = this.startByte;
        long currentEnd = 0L;
        long filePositionToWrite = 0L;
        while (currentStart <= this.endByte) {
            currentEnd = currentStart + this.perRequestDownloadSize - 1L;
            if (currentEnd > this.endByte) {
                currentEnd = this.endByte;
            }
            PresignedUrlDownloadRequest rangeRequest = this.request.clone();
            rangeRequest.setRange(currentStart, currentEnd);
            Callable<S3Object> s3Object = this.serviceCall(rangeRequest);
            this.futures.add(this.executor.submit(new DownloadS3ObjectCallable(s3Object, this.dstfile, filePositionToWrite)));
            filePositionToWrite += this.perRequestDownloadSize;
            currentStart = currentEnd + 1L;
        }
        Future<File> future = this.executor.submit(this.completeAllFutures());
        ((DownloadMonitor)this.download.getMonitor()).setFuture(future);
    }

    private Callable<S3Object> serviceCall(final PresignedUrlDownloadRequest presignedUrlDownloadRequest) {
        return new Callable<S3Object>(){

            @Override
            public S3Object call() throws Exception {
                return PresignUrlDownloadCallable.this.s3.download(presignedUrlDownloadRequest).getS3Object();
            }
        };
    }

    private Callable<File> completeAllFutures() {
        return new Callable<File>(){

            @Override
            public File call() throws Exception {
                try {
                    for (Future future : PresignUrlDownloadCallable.this.futures) {
                        future.get();
                    }
                    PresignUrlDownloadCallable.this.download.setState(Transfer.TransferState.Completed);
                }
                finally {
                    FileLocks.unlock(PresignUrlDownloadCallable.this.dstfile);
                }
                return PresignUrlDownloadCallable.this.dstfile;
            }
        };
    }

    @SdkTestInternalApi
    public static void setTesting(boolean b) {
        testing = b;
    }
}

