/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientDeployClassesCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMultiTargetMessageTask;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.usercodedeployment.impl.operation.DeployClassesOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.UserCodeDeploymentPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Supplier;
import java.security.Permission;
import java.util.Collection;
import java.util.Map;

public class DeployClassesMessageTask
extends AbstractMultiTargetMessageTask<ClientDeployClassesCodec.RequestParameters>
implements Supplier<Operation> {
    public DeployClassesMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public Operation get() {
        return new DeployClassesOperation(((ClientDeployClassesCodec.RequestParameters)this.parameters).classDefinitions);
    }

    @Override
    protected Supplier<Operation> createOperationSupplier() {
        return this;
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
    protected ClientDeployClassesCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientDeployClassesCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientDeployClassesCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "user-code-deployment-service";
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
    public Permission getRequiredPermission() {
        return new UserCodeDeploymentPermission(new String[]{"deploy"});
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

