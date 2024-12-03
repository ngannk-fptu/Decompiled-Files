/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.operations;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEngineImpl;
import com.hazelcast.client.impl.StubAuthenticationException;
import com.hazelcast.client.impl.client.ClientPrincipal;
import com.hazelcast.client.impl.operations.AbstractClientOperation;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import java.io.IOException;
import java.util.Set;

public class ClientReAuthOperation
extends AbstractClientOperation
implements UrgentSystemOperation,
AllowedDuringPassiveState {
    private String clientUuid;
    private long authCorrelationId;

    public ClientReAuthOperation() {
    }

    public ClientReAuthOperation(String clientUuid, long authCorrelationId) {
        this.clientUuid = clientUuid;
        this.authCorrelationId = authCorrelationId;
    }

    @Override
    public void run() throws Exception {
        ClientEngineImpl engine = (ClientEngineImpl)this.getService();
        engine.getClientManagementExecutor().execute(new ClientReauthTask());
    }

    private boolean doRun() throws Exception {
        ILogger logger = this.getLogger();
        ClientEngineImpl engine = (ClientEngineImpl)this.getService();
        String memberUuid = this.getCallerUuid();
        if (!engine.trySetLastAuthenticationCorrelationId(this.clientUuid, this.authCorrelationId)) {
            String message = "Server already processed a newer authentication from client with UUID " + this.clientUuid + ". Not applying requested ownership change to " + memberUuid;
            logger.info(message);
            throw new StubAuthenticationException(message);
        }
        Set<ClientEndpoint> endpoints = engine.getEndpointManager().getEndpoints(this.clientUuid);
        for (ClientEndpoint endpoint : endpoints) {
            ClientPrincipal principal = new ClientPrincipal(this.clientUuid, memberUuid);
            endpoint.authenticated(principal);
        }
        String previousMemberUuid = engine.addOwnershipMapping(this.clientUuid, memberUuid);
        if (logger.isFineEnabled()) {
            logger.fine("Client authenticated " + this.clientUuid + ", owner " + memberUuid);
        }
        return previousMemberUuid == null;
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public void logError(Throwable e) {
        if (!(e instanceof StubAuthenticationException)) {
            super.logError(e);
        }
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public String getServiceName() {
        return "hz:core:clientEngine";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.clientUuid);
        out.writeLong(this.authCorrelationId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.clientUuid = in.readUTF();
        this.authCorrelationId = in.readLong();
    }

    @Override
    public int getId() {
        return 1;
    }

    public class ClientReauthTask
    implements Runnable {
        @Override
        public void run() {
            try {
                ClientReAuthOperation.this.sendResponse(ClientReAuthOperation.this.doRun());
            }
            catch (Exception e) {
                ClientReAuthOperation.this.sendResponse(e);
            }
        }
    }
}

