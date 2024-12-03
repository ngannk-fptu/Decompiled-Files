/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListenerChain;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.PersistableDownload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.exception.PauseException;
import com.amazonaws.services.s3.transfer.internal.AbstractTransfer;
import com.amazonaws.services.s3.transfer.internal.S3ProgressPublisher;
import com.amazonaws.services.s3.transfer.internal.TransferManagerUtils;
import com.amazonaws.services.s3.transfer.internal.TransferStateChangeListener;
import java.io.File;
import java.io.IOException;

public class DownloadImpl
extends AbstractTransfer
implements Download {
    private S3Object s3Object;
    private PersistableDownload persistableDownload;
    private Integer lastFullyDownloadedPartNumber;
    private Long lastFullyDownloadedFilePosition;
    private final GetObjectRequest getObjectRequest;
    private final File file;
    private final ObjectMetadata objectMetadata;
    private final ProgressListenerChain progressListenerChain;

    @Deprecated
    public DownloadImpl(String description, TransferProgress transferProgress, ProgressListenerChain progressListenerChain, S3Object s3Object, TransferStateChangeListener listener, GetObjectRequest getObjectRequest, File file) {
        this(description, transferProgress, progressListenerChain, s3Object, listener, getObjectRequest, file, null, false);
    }

    public DownloadImpl(String description, TransferProgress transferProgress, ProgressListenerChain progressListenerChain, S3Object s3Object, TransferStateChangeListener listener, GetObjectRequest getObjectRequest, File file, ObjectMetadata objectMetadata, boolean isDownloadParallel) {
        super(description, transferProgress, progressListenerChain, listener);
        this.s3Object = s3Object;
        this.objectMetadata = objectMetadata;
        this.getObjectRequest = getObjectRequest;
        this.file = file;
        this.progressListenerChain = progressListenerChain;
        this.persistableDownload = this.captureDownloadState(getObjectRequest, file);
        S3ProgressPublisher.publishTransferPersistable(progressListenerChain, this.persistableDownload);
    }

    @Override
    public synchronized ObjectMetadata getObjectMetadata() {
        if (this.s3Object != null) {
            return this.s3Object.getObjectMetadata();
        }
        return this.objectMetadata;
    }

    @Override
    public String getBucketName() {
        return this.getObjectRequest.getBucketName();
    }

    @Override
    public String getKey() {
        return this.getObjectRequest.getKey();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    void updatePersistableTransfer(Integer lastFullyDownloadedPartNumber) {
        DownloadImpl downloadImpl = this;
        synchronized (downloadImpl) {
            this.lastFullyDownloadedPartNumber = lastFullyDownloadedPartNumber;
        }
        this.persistableDownload = this.captureDownloadState(this.getObjectRequest, this.file);
        S3ProgressPublisher.publishTransferPersistable(this.progressListenerChain, this.persistableDownload);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    void updatePersistableTransfer(Integer lastFullyDownloadedPartNumber, Long lastFullyDownloadedFilePosition) {
        DownloadImpl downloadImpl = this;
        synchronized (downloadImpl) {
            this.lastFullyDownloadedPartNumber = lastFullyDownloadedPartNumber;
            this.lastFullyDownloadedFilePosition = lastFullyDownloadedFilePosition;
        }
        this.persistableDownload = this.captureDownloadState(this.getObjectRequest, this.file);
        S3ProgressPublisher.publishTransferPersistable(this.progressListenerChain, this.persistableDownload);
    }

    public synchronized Integer getLastFullyDownloadedPartNumber() {
        return this.lastFullyDownloadedPartNumber;
    }

    public synchronized Long getLastFullyDownloadedFilePosition() {
        return this.lastFullyDownloadedFilePosition;
    }

    @Override
    public synchronized void abort() throws IOException {
        this.monitor.getFuture().cancel(true);
        if (this.s3Object != null) {
            this.s3Object.getObjectContent().abort();
        }
        this.setState(Transfer.TransferState.Canceled);
    }

    public synchronized void abortWithoutNotifyingStateChangeListener() throws IOException {
        this.monitor.getFuture().cancel(true);
        this.state = Transfer.TransferState.Canceled;
    }

    public synchronized void setS3Object(S3Object s3Object) {
        this.s3Object = s3Object;
    }

    @Override
    public void setState(Transfer.TransferState state) {
        super.setState(state);
        switch (state) {
            case Completed: {
                this.fireProgressEvent(ProgressEventType.TRANSFER_COMPLETED_EVENT);
                break;
            }
            case Canceled: {
                this.fireProgressEvent(ProgressEventType.TRANSFER_CANCELED_EVENT);
                break;
            }
            case Failed: {
                this.fireProgressEvent(ProgressEventType.TRANSFER_FAILED_EVENT);
                break;
            }
        }
    }

    private PersistableDownload captureDownloadState(GetObjectRequest getObjectRequest, File file) {
        if (getObjectRequest.getSSECustomerKey() == null) {
            return new PersistableDownload(getObjectRequest.getBucketName(), getObjectRequest.getKey(), getObjectRequest.getVersionId(), getObjectRequest.getRange(), getObjectRequest.getResponseHeaders(), getObjectRequest.isRequesterPays(), file.getAbsolutePath(), this.getLastFullyDownloadedPartNumber(), this.getObjectMetadata().getLastModified().getTime(), this.getLastFullyDownloadedFilePosition());
        }
        return null;
    }

    @Override
    public PersistableDownload pause() throws PauseException {
        boolean forceCancel = true;
        Transfer.TransferState currentState = this.getState();
        this.monitor.getFuture().cancel(true);
        if (this.persistableDownload == null) {
            throw new PauseException(TransferManagerUtils.determinePauseStatus(currentState, forceCancel));
        }
        return this.persistableDownload;
    }
}

