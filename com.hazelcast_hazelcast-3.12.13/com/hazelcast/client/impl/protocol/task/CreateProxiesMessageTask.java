/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientCreateProxiesCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMultiTargetMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyInfo;
import com.hazelcast.spi.impl.proxyservice.impl.operations.PostJoinProxyOperation;
import com.hazelcast.util.function.Supplier;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CreateProxiesMessageTask
extends AbstractMultiTargetMessageTask<ClientCreateProxiesCodec.RequestParameters>
implements Supplier<Operation>,
BlockingMessageTask {
    public CreateProxiesMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Supplier<Operation> createOperationSupplier() {
        return this;
    }

    @Override
    public Operation get() {
        ArrayList<ProxyInfo> proxyInfos = new ArrayList<ProxyInfo>(((ClientCreateProxiesCodec.RequestParameters)this.parameters).proxies.size());
        for (Map.Entry<String, String> proxy : ((ClientCreateProxiesCodec.RequestParameters)this.parameters).proxies) {
            proxyInfos.add(new ProxyInfo(proxy.getValue(), proxy.getKey()));
        }
        return new PostJoinProxyOperation(proxyInfos);
    }

    @Override
    protected Object reduce(Map<Member, Object> map) throws Throwable {
        for (Object result : map.values()) {
            if (!(result instanceof Throwable) || result instanceof MemberLeftException) continue;
            throw (Throwable)result;
        }
        return null;
    }

    @Override
    public Collection<Member> getTargets() {
        return this.nodeEngine.getClusterService().getMembers();
    }

    @Override
    protected ClientCreateProxiesCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientCreateProxiesCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientCreateProxiesCodec.encodeResponse();
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getServiceName() {
        return null;
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
}

