/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.operations;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEndpointManagerImpl;
import com.hazelcast.client.impl.ClientEngineImpl;
import com.hazelcast.client.impl.operations.AbstractClientOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ClientAwareService;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class ClientDisconnectionOperation
extends AbstractClientOperation
implements UrgentSystemOperation {
    private String clientUuid;
    private String memberUuid;

    public ClientDisconnectionOperation() {
    }

    public ClientDisconnectionOperation(String clientUuid, String memberUuid) {
        this.clientUuid = clientUuid;
        this.memberUuid = memberUuid;
    }

    @Override
    public void run() throws Exception {
        ClientEngineImpl engine = (ClientEngineImpl)this.getService();
        engine.getClientManagementExecutor().execute(new ClientDisconnectedTask());
    }

    private boolean doRun() {
        ClientEngineImpl engine = (ClientEngineImpl)this.getService();
        ClientEndpointManagerImpl endpointManager = (ClientEndpointManagerImpl)engine.getEndpointManager();
        if (!engine.removeOwnershipMapping(this.clientUuid, this.memberUuid)) {
            return false;
        }
        Set<ClientEndpoint> endpoints = endpointManager.getEndpoints(this.clientUuid);
        for (ClientEndpoint endpoint : endpoints) {
            endpoint.getConnection().close("ClientDisconnectionOperation: Client disconnected from cluster", null);
        }
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Collection<ClientAwareService> services = nodeEngine.getServices(ClientAwareService.class);
        for (ClientAwareService service : services) {
            service.clientDisconnected(this.clientUuid);
        }
        return true;
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.clientUuid);
        out.writeUTF(this.memberUuid);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.clientUuid = in.readUTF();
        this.memberUuid = in.readUTF();
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String toString() {
        return "ClientDisconnectionOperation{clientUuid='" + this.clientUuid + '\'' + ", memberUuid='" + this.memberUuid + '\'' + "} " + super.toString();
    }

    public class ClientDisconnectedTask
    implements Runnable {
        @Override
        public void run() {
            try {
                ClientDisconnectionOperation.this.sendResponse(ClientDisconnectionOperation.this.doRun());
            }
            catch (Exception e) {
                ClientDisconnectionOperation.this.sendResponse(e);
            }
        }
    }
}

