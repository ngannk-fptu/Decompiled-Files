/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapKeySetCodec;
import com.hazelcast.client.impl.protocol.task.map.DefaultMapQueryMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.LocalMapStatsUtil;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.util.IterationType;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapKeySetMessageTask
extends DefaultMapQueryMessageTask<MapKeySetCodec.RequestParameters> {
    public MapKeySetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object reduce(Collection<QueryResultRow> result) {
        ArrayList<Data> keys = new ArrayList<Data>(result.size());
        for (QueryResultRow resultEntry : result) {
            keys.add(resultEntry.getKey());
        }
        LocalMapStatsUtil.incrementOtherOperationsCount((MapService)this.getService("hz:impl:mapService"), ((MapKeySetCodec.RequestParameters)this.parameters).name);
        return keys;
    }

    @Override
    protected Predicate getPredicate() {
        return TruePredicate.INSTANCE;
    }

    @Override
    protected IterationType getIterationType() {
        return IterationType.KEY;
    }

    @Override
    protected MapKeySetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapKeySetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapKeySetCodec.encodeResponse((List)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapKeySetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapKeySetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "keySet";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

