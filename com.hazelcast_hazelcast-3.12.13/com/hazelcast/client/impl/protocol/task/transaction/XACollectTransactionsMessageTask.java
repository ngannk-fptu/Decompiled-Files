/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transaction;

import com.hazelcast.client.impl.CollectRemoteTransactionsOperationSupplier;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.XATransactionCollectTransactionsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMultiTargetMessageTask;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.TransactionPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SerializableList;
import com.hazelcast.util.function.Supplier;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class XACollectTransactionsMessageTask
extends AbstractMultiTargetMessageTask<XATransactionCollectTransactionsCodec.RequestParameters> {
    public XACollectTransactionsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected XATransactionCollectTransactionsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return XATransactionCollectTransactionsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return XATransactionCollectTransactionsCodec.encodeResponse((List)response);
    }

    @Override
    protected Supplier<Operation> createOperationSupplier() {
        return new CollectRemoteTransactionsOperationSupplier();
    }

    @Override
    protected Object reduce(Map<Member, Object> map) throws Throwable {
        ArrayList<Data> list = new ArrayList<Data>();
        for (Object o : map.values()) {
            if (o instanceof Throwable) {
                if (o instanceof MemberLeftException) continue;
                throw (Throwable)o;
            }
            SerializableList xidSet = (SerializableList)o;
            list.addAll(xidSet.getCollection());
        }
        return list;
    }

    @Override
    public Collection<Member> getTargets() {
        return this.clientEngine.getClusterService().getMembers();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:xaService";
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new TransactionPermission();
    }
}

