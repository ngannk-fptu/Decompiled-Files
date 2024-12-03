/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapKeySetWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.query.Predicate;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TransactionalMapKeySetWithPredicateMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapKeySetWithPredicateCodec.RequestParameters> {
    public TransactionalMapKeySetWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapKeySetWithPredicateCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapKeySetWithPredicateCodec.RequestParameters)this.parameters).name);
        Predicate predicate = (Predicate)this.serializationService.toObject(((TransactionalMapKeySetWithPredicateCodec.RequestParameters)this.parameters).predicate);
        Set keySet = map.keySet(predicate);
        ArrayList list = new ArrayList(keySet.size());
        for (Object o : keySet) {
            list.add(this.serializationService.toData(o));
        }
        return list;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapKeySetWithPredicateCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapKeySetWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapKeySetWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapKeySetWithPredicateCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapKeySetWithPredicateCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapKeySetWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "keySet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null};
    }
}

