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

public class CreateTxBackupLogOperation
extends AbstractTxOperation {
    private String callerUuid;
    private String txnId;

    public CreateTxBackupLogOperation() {
    }

    public CreateTxBackupLogOperation(String callerUuid, String txnId) {
        this.callerUuid = callerUuid;
        this.txnId = txnId;
    }

    @Override
    public void run() throws Exception {
        TransactionManagerServiceImpl txManagerService = (TransactionManagerServiceImpl)this.getService();
        txManagerService.createBackupLog(this.callerUuid, this.txnId);
    }

    @Override
    public Object getResponse() {
        return true;
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
        return 0;
    }

    @Override
    public String getCallerUuid() {
        return this.callerUuid;
    }

    public String getTxnId() {
        return this.txnId;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.callerUuid);
        out.writeUTF(this.txnId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.callerUuid = in.readUTF();
        this.txnId = in.readUTF();
    }
}

