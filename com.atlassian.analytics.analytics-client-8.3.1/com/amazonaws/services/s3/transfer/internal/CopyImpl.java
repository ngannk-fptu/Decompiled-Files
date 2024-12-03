/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressListenerChain;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.internal.AbstractTransfer;
import com.amazonaws.services.s3.transfer.internal.TransferStateChangeListener;
import com.amazonaws.services.s3.transfer.model.CopyResult;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CopyImpl
extends AbstractTransfer
implements Copy {
    public CopyImpl(String description, TransferProgress transferProgress, ProgressListenerChain progressListenerChain, TransferStateChangeListener stateChangeListener) {
        super(description, transferProgress, progressListenerChain, stateChangeListener);
    }

    @Override
    public CopyResult waitForCopyResult() throws AmazonClientException, AmazonServiceException, InterruptedException {
        try {
            CopyResult result = null;
            while (!this.monitor.isDone() || result == null) {
                Future<?> f = this.monitor.getFuture();
                result = (CopyResult)f.get();
            }
            return result;
        }
        catch (ExecutionException e) {
            this.rethrowExecutionException(e);
            return null;
        }
    }
}

