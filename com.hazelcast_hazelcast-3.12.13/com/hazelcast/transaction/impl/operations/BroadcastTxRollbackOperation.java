/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.operations;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.transaction.impl.operations.AbstractTxOperation;
import java.io.IOException;
import java.util.Collection;

public final class BroadcastTxRollbackOperation
extends AbstractTxOperation {
    private String txnId;

    public BroadcastTxRollbackOperation() {
    }

    public BroadcastTxRollbackOperation(String txnId) {
        this.txnId = txnId;
    }

    @Override
    public void run() throws Exception {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Collection<TransactionalService> services = nodeEngine.getServices(TransactionalService.class);
        for (TransactionalService service : services) {
            try {
                service.rollbackTransaction(this.txnId);
            }
            catch (Exception e) {
                this.getLogger().warning("Error while rolling back transaction: " + this.txnId, e);
            }
        }
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public int getId() {
        return 1;
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

