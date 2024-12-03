/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.operations;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import com.hazelcast.transaction.impl.operations.AbstractTxOperation;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ReplicateTxBackupLogOperation
extends AbstractTxOperation {
    private final List<TransactionLogRecord> records = new LinkedList<TransactionLogRecord>();
    private String callerUuid;
    private String txnId;
    private long timeoutMillis;
    private long startTime;

    public ReplicateTxBackupLogOperation() {
    }

    public ReplicateTxBackupLogOperation(Collection<TransactionLogRecord> logs, String callerUuid, String txnId, long timeoutMillis, long startTime) {
        this.records.addAll(logs);
        this.callerUuid = callerUuid;
        this.txnId = txnId;
        this.timeoutMillis = timeoutMillis;
        this.startTime = startTime;
    }

    @Override
    public void run() throws Exception {
        TransactionManagerServiceImpl txManagerService = (TransactionManagerServiceImpl)this.getService();
        txManagerService.replicaBackupLog(this.records, this.callerUuid, this.txnId, this.timeoutMillis, this.startTime);
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
        return 3;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.callerUuid);
        out.writeUTF(this.txnId);
        out.writeLong(this.timeoutMillis);
        out.writeLong(this.startTime);
        int len = this.records.size();
        out.writeInt(len);
        if (len > 0) {
            for (TransactionLogRecord record : this.records) {
                out.writeObject(record);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.callerUuid = in.readUTF();
        this.txnId = in.readUTF();
        this.timeoutMillis = in.readLong();
        this.startTime = in.readLong();
        int len = in.readInt();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                TransactionLogRecord record = (TransactionLogRecord)in.readObject();
                this.records.add(record);
            }
        }
    }
}

