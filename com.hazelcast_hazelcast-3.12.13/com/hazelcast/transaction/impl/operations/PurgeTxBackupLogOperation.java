/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.operations;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import com.hazelcast.transaction.impl.operations.AbstractTxOperation;
import java.io.IOException;

public class PurgeTxBackupLogOperation
extends AbstractTxOperation {
    private String txnId;

    public PurgeTxBackupLogOperation() {
    }

    public PurgeTxBackupLogOperation(String txnId) {
        this.txnId = txnId;
    }

    @Override
    public void run() throws Exception {
        TransactionManagerServiceImpl txManagerService = (TransactionManagerServiceImpl)this.getService();
        txManagerService.purgeBackupLog(this.txnId);
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.txnId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.txnId = in.readUTF();
    }
}

