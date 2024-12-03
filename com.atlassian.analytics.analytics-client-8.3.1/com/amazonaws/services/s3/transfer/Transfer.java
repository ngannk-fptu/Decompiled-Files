/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.TransferProgress;

public interface Transfer {
    public boolean isDone();

    public void waitForCompletion() throws AmazonClientException, AmazonServiceException, InterruptedException;

    public AmazonClientException waitForException() throws InterruptedException;

    public String getDescription();

    public TransferState getState();

    public void addProgressListener(ProgressListener var1);

    public void removeProgressListener(ProgressListener var1);

    public TransferProgress getProgress();

    @Deprecated
    public void addProgressListener(com.amazonaws.services.s3.model.ProgressListener var1);

    @Deprecated
    public void removeProgressListener(com.amazonaws.services.s3.model.ProgressListener var1);

    public static enum TransferState {
        Waiting,
        InProgress,
        Completed,
        Canceled,
        Failed;

    }
}

