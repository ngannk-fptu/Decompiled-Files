/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapValuesCodec;
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

public class MapValuesMessageTask
extends DefaultMapQueryMessageTask<MapValuesCodec.RequestParameters> {
    public MapValuesMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object reduce(Collection<QueryResultRow> result) {
        ArrayList<Data> values = new ArrayList<Data>(result.size());
        for (QueryResultRow resultEntry : result) {
            values.add(resultEntry.getValue());
        }
        LocalMapStatsUtil.incrementOtherOperationsCount((MapService)this.getService("hz:impl:mapService"), ((MapValuesCodec.RequestParameters)this.parameters).name);
        return values;
    }

    @Override
    protected Predicate getPredicate() {
        return TruePredicate.INSTANCE;
    }

    @Override
    protected IterationType getIterationType() {
        return IterationType.VALUE;
    }

    @Override
    protected MapValuesCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapValuesCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapValuesCodec.encodeResponse((List)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapValuesCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapValuesCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "values";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

