/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Set;

public abstract class Offload
extends CallStatus {
    protected InternalOperationService operationService;
    protected NodeEngine nodeEngine;
    protected ExecutionService executionService;
    protected SerializationService serializationService;
    private final Operation offloadedOperation;
    private Set<Operation> asyncOperations;

    public Offload(Operation offloadedOperation) {
        super(3);
        this.offloadedOperation = offloadedOperation;
    }

    public final Operation offloadedOperation() {
        return this.offloadedOperation;
    }

    public final void init(NodeEngineImpl nodeEngine, Set<Operation> asyncOperations) {
        this.nodeEngine = nodeEngine;
        this.operationService = nodeEngine.getOperationService();
        this.serializationService = nodeEngine.getSerializationService();
        this.asyncOperations = asyncOperations;
        this.executionService = nodeEngine.getExecutionService();
        asyncOperations.add(this.offloadedOperation);
        this.offloadedOperation.setOperationResponseHandler(this.newOperationResponseHandler());
    }

    private OperationResponseHandler newOperationResponseHandler() {
        OperationResponseHandler delegate = this.offloadedOperation.getOperationResponseHandler();
        if (delegate instanceof OffloadedOperationResponseHandler) {
            delegate = ((OffloadedOperationResponseHandler)delegate).delegate;
        }
        return new OffloadedOperationResponseHandler(delegate);
    }

    public abstract void start() throws Exception;

    private class OffloadedOperationResponseHandler
    implements OperationResponseHandler {
        private final OperationResponseHandler delegate;

        OffloadedOperationResponseHandler(OperationResponseHandler delegate) {
            this.delegate = delegate;
        }

        public void sendResponse(Operation op, Object response) {
            Offload.this.asyncOperations.remove(Offload.this.offloadedOperation);
            this.delegate.sendResponse(Offload.this.offloadedOperation, response);
        }
    }
}

