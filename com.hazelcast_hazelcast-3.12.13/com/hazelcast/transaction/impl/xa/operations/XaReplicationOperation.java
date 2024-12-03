/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.TransactionDataSerializerHook;
import com.hazelcast.transaction.impl.xa.XAService;
import com.hazelcast.transaction.impl.xa.XATransaction;
import com.hazelcast.transaction.impl.xa.XATransactionDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XaReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private List<XATransactionDTO> migrationData;

    public XaReplicationOperation() {
    }

    public XaReplicationOperation(List<XATransactionDTO> migrationData, int partitionId, int replicaIndex) {
        this.setPartitionId(partitionId);
        this.setReplicaIndex(replicaIndex);
        this.migrationData = migrationData;
    }

    @Override
    public void run() throws Exception {
        XAService xaService = (XAService)this.getService();
        NodeEngine nodeEngine = this.getNodeEngine();
        for (XATransactionDTO transactionDTO : this.migrationData) {
            XATransaction transaction = new XATransaction(nodeEngine, transactionDTO.getRecords(), transactionDTO.getTxnId(), transactionDTO.getXid(), transactionDTO.getOwnerUuid(), transactionDTO.getTimeoutMilis(), transactionDTO.getStartTime());
            xaService.putTransaction(transaction);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.migrationData.size());
        for (XATransactionDTO transactionDTO : this.migrationData) {
            out.writeObject(transactionDTO);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.migrationData = new ArrayList<XATransactionDTO>(size);
        for (int i = 0; i < size; ++i) {
            XATransactionDTO transactionDTO = (XATransactionDTO)in.readObject();
            this.migrationData.add(transactionDTO);
        }
    }

    @Override
    public int getFactoryId() {
        return TransactionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 17;
    }
}

