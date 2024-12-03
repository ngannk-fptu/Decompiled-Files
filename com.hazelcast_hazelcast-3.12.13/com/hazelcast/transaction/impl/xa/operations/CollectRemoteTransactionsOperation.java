/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa.operations;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.SerializableList;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.XAService;
import com.hazelcast.transaction.impl.xa.operations.AbstractXAOperation;
import java.util.ArrayList;
import java.util.Set;

public class CollectRemoteTransactionsOperation
extends AbstractXAOperation {
    private transient SerializableList xidSet;

    @Override
    public void run() throws Exception {
        XAService xaService = (XAService)this.getService();
        NodeEngine nodeEngine = this.getNodeEngine();
        Set<SerializableXID> xids = xaService.getPreparedXids();
        ArrayList<Data> xidSet = new ArrayList<Data>();
        for (SerializableXID xid : xids) {
            xidSet.add(nodeEngine.toData(xid));
        }
        this.xidSet = new SerializableList(xidSet);
    }

    @Override
    public Object getResponse() {
        return this.xidSet;
    }

    @Override
    public int getId() {
        return 11;
    }
}

