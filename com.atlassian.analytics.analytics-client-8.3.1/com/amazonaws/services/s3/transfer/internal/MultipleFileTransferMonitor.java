/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.internal.AbstractTransfer;
import com.amazonaws.services.s3.transfer.internal.TransferMonitor;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MultipleFileTransferMonitor
implements TransferMonitor {
    private final Collection<? extends AbstractTransfer> subTransfers;
    private final AbstractTransfer transfer;
    private final Future<?> future;

    public MultipleFileTransferMonitor(AbstractTransfer transfer, Collection<? extends AbstractTransfer> subTransfers) {
        this.subTransfers = subTransfers;
        this.transfer = transfer;
        this.future = new Future<Object>(){

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return true;
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                Object result = null;
                for (AbstractTransfer download : MultipleFileTransferMonitor.this.subTransfers) {
                    result = download.getMonitor().getFuture().get();
                }
                return result;
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                Object result = null;
                for (AbstractTransfer subTransfer : MultipleFileTransferMonitor.this.subTransfers) {
                    result = subTransfer.getMonitor().getFuture().get(timeout, unit);
                }
                return result;
            }

            @Override
            public boolean isCancelled() {
                return MultipleFileTransferMonitor.this.transfer.getState() == Transfer.TransferState.Canceled;
            }

            @Override
            public boolean isDone() {
                return MultipleFileTransferMonitor.this.isDone();
            }
        };
    }

    @Override
    public Future<?> getFuture() {
        return this.future;
    }

    @Override
    public synchronized boolean isDone() {
        for (Transfer transfer : this.subTransfers) {
            if (transfer.isDone()) continue;
            return false;
        }
        return true;
    }
}

