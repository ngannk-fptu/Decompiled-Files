/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapEntrySetCodec;
import com.hazelcast.client.impl.protocol.task.map.DefaultMapQueryMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.LocalMapStatsUtil;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.nio.Connection;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.util.IterationType;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapEntrySetMessageTask
extends DefaultMapQueryMessageTask<MapEntrySetCodec.RequestParameters> {
    public MapEntrySetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object reduce(Collection<QueryResultRow> result) {
        ArrayList<QueryResultRow> entries = new ArrayList<QueryResultRow>(result.size());
        for (QueryResultRow row : result) {
            entries.add(row);
        }
        LocalMapStatsUtil.incrementOtherOperationsCount((MapService)this.getService("hz:impl:mapService"), ((MapEntrySetCodec.RequestParameters)this.parameters).name);
        return entries;
    }

    @Override
    protected Predicate getPredicate() {
        return TruePredicate.INSTANCE;
    }

    @Override
    protected IterationType getIterationType() {
        return IterationType.ENTRY;
    }

    @Override
    protected MapEntrySetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapEntrySetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapEntrySetCodec.encodeResponse((List)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapEntrySetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapEntrySetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "entrySet";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

