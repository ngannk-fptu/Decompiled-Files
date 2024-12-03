/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.internal.MultipleFileTransfer;
import com.amazonaws.services.s3.transfer.internal.TransferStateChangeListener;
import java.util.concurrent.CountDownLatch;

final class MultipleFileTransferStateChangeListener
implements TransferStateChangeListener {
    private final CountDownLatch latch;
    private final MultipleFileTransfer<?> multipleFileTransfer;

    public MultipleFileTransferStateChangeListener(CountDownLatch latch, MultipleFileTransfer<?> multipleFileTransfer) {
        this.latch = latch;
        this.multipleFileTransfer = multipleFileTransfer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transferStateChanged(Transfer upload, Transfer.TransferState state) {
        try {
            this.latch.await();
        }
        catch (InterruptedException e) {
            throw new SdkClientException("Couldn't wait for all downloads to be queued");
        }
        MultipleFileTransfer<?> multipleFileTransfer = this.multipleFileTransfer;
        synchronized (multipleFileTransfer) {
            if (this.multipleFileTransfer.getState() == state || this.multipleFileTransfer.isDone()) {
                return;
            }
            if (state == Transfer.TransferState.InProgress) {
                this.multipleFileTransfer.setState(state);
            } else if (this.multipleFileTransfer.getMonitor().isDone()) {
                this.multipleFileTransfer.collateFinalState();
            } else {
                this.multipleFileTransfer.setState(Transfer.TransferState.InProgress);
            }
        }
    }
}

