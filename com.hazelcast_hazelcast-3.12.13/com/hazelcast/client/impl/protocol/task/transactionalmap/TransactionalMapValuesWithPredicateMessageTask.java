/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapValuesWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.query.Predicate;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransactionalMapValuesWithPredicateMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapValuesWithPredicateCodec.RequestParameters> {
    public TransactionalMapValuesWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapValuesWithPredicateCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapValuesWithPredicateCodec.RequestParameters)this.parameters).name);
        Predicate predicate = (Predicate)this.serializationService.toObject(((TransactionalMapValuesWithPredicateCodec.RequestParameters)this.parameters).predicate);
        Collection values = map.values(predicate);
        ArrayList list = new ArrayList(values.size());
        for (Object o : values) {
            list.add(this.serializationService.toData(o));
        }
        return list;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapValuesWithPredicateCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapValuesWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapValuesWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapValuesWithPredicateCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapValuesWithPredicateCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapValuesWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "values";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null};
    }
}

