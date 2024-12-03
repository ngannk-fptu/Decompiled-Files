/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.impl.operationservice.impl.operations;

import com.hazelcast.client.impl.operations.OperationFactoryWrapper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Offload;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.PartitionTaskFactory;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareFactoryAccessor;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class PartitionIteratingOperation
extends Operation
implements IdentifiedDataSerializable {
    private static final Object NULL = new Object(){

        public String toString() {
            return "null";
        }
    };
    private static final PartitionResponse EMPTY_RESPONSE = new PartitionResponse(new int[0], new Object[0]);
    private OperationFactory operationFactory;
    private int[] partitions;

    public PartitionIteratingOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public PartitionIteratingOperation(OperationFactory operationFactory, int[] partitions) {
        this.operationFactory = operationFactory;
        this.partitions = partitions;
    }

    public OperationFactory getOperationFactory() {
        return this.operationFactory;
    }

    @Override
    public CallStatus call() {
        return new OffloadImpl();
    }

    @Override
    public void onExecutionFailure(Throwable cause) {
        this.sendResponse(new ErrorResponse(cause, this.getCallId(), this.isUrgent()));
        this.getLogger().severe(cause);
    }

    private InternalOperationService getOperationService() {
        return (InternalOperationService)this.getNodeEngine().getOperationService();
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.operationFactory);
        out.writeIntArray(this.partitions);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.operationFactory = (OperationFactory)in.readObject();
        this.partitions = in.readIntArray();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", operationFactory=").append(this.operationFactory);
    }

    public static final class PartitionResponse
    implements IdentifiedDataSerializable {
        private int[] partitions;
        private Object[] results;

        public PartitionResponse() {
        }

        PartitionResponse(int[] partitions, Object[] results) {
            this.partitions = partitions;
            this.results = results;
        }

        public void addResults(Map<Integer, Object> partitionResults) {
            if (this.results == null) {
                return;
            }
            for (int i = 0; i < this.results.length; ++i) {
                partitionResults.put(this.partitions[i], this.results[i]);
            }
        }

        @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
        public Object[] getResults() {
            return this.results;
        }

        @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
        public int[] getPartitions() {
            return this.partitions;
        }

        @Override
        public int getFactoryId() {
            return SpiDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 4;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeIntArray(this.partitions);
            int resultLength = this.results != null ? this.results.length : 0;
            out.writeInt(resultLength);
            if (resultLength > 0) {
                for (Object result : this.results) {
                    out.writeObject(result);
                }
            }
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            this.partitions = in.readIntArray();
            int resultLength = in.readInt();
            if (resultLength > 0) {
                this.results = new Object[resultLength];
                for (int i = 0; i < resultLength; ++i) {
                    this.results[i] = in.readObject();
                }
            }
        }
    }

    private class OperationResponseHandlerImpl
    implements OperationResponseHandler {
        private final AtomicReferenceArray<Object> responseArray;
        private final AtomicInteger pendingOperations;
        private final int[] partitions;

        OperationResponseHandlerImpl(int[] partitions) {
            this.responseArray = new AtomicReferenceArray(PartitionIteratingOperation.this.getNodeEngine().getPartitionService().getPartitionCount());
            this.partitions = partitions;
            this.pendingOperations = new AtomicInteger(partitions.length);
        }

        public void sendResponse(Operation op, Object response) {
            if (response instanceof NormalResponse) {
                response = ((NormalResponse)response).getValue();
            } else if (response == null) {
                response = NULL;
            }
            if (!this.responseArray.compareAndSet(op.getPartitionId(), null, response)) {
                PartitionIteratingOperation.this.getLogger().warning("Duplicate response for " + op + " second response [" + response + "]first response [" + this.responseArray.get(op.getPartitionId()) + "]");
                return;
            }
            if (this.pendingOperations.decrementAndGet() == 0) {
                this.sendResponse();
            }
        }

        private void sendResponse() {
            Object[] results = new Object[this.partitions.length];
            for (int k = 0; k < this.partitions.length; ++k) {
                int partitionId = this.partitions[k];
                Object response = this.responseArray.get(partitionId);
                results[k] = response == NULL ? null : response;
            }
            PartitionIteratingOperation.this.sendResponse(new PartitionResponse(this.partitions, results));
        }
    }

    private final class OffloadImpl
    extends Offload {
        private OffloadImpl() {
            super(PartitionIteratingOperation.this);
        }

        @Override
        public void start() {
            if (PartitionIteratingOperation.this.partitions.length == 0) {
                PartitionIteratingOperation.this.sendResponse(EMPTY_RESPONSE);
                return;
            }
            PartitionAwareOperationFactory partitionAwareFactory = PartitionAwareFactoryAccessor.extractPartitionAware(PartitionIteratingOperation.this.operationFactory);
            if (partitionAwareFactory == null) {
                this.executeOperations();
            } else {
                this.executeOperations(partitionAwareFactory);
            }
        }

        private void executeOperations() {
            PartitionTaskFactory f = new PartitionTaskFactory(){
                private final NodeEngine nodeEngine;
                private final OperationResponseHandler responseHandler;
                private final Object service;
                {
                    this.nodeEngine = PartitionIteratingOperation.this.getNodeEngine();
                    this.responseHandler = new OperationResponseHandlerImpl(PartitionIteratingOperation.this.partitions);
                    this.service = PartitionIteratingOperation.this.getServiceName() == null ? null : PartitionIteratingOperation.this.getService();
                }

                public Operation create(int partitionId) {
                    Operation op = PartitionIteratingOperation.this.operationFactory.createOperation().setNodeEngine(this.nodeEngine).setPartitionId(partitionId).setReplicaIndex(PartitionIteratingOperation.this.getReplicaIndex()).setOperationResponseHandler(this.responseHandler).setServiceName(PartitionIteratingOperation.this.getServiceName()).setService(this.service).setCallerUuid(OffloadImpl.this.extractCallerUuid());
                    OperationAccessor.setCallerAddress(op, PartitionIteratingOperation.this.getCallerAddress());
                    return op;
                }
            };
            PartitionIteratingOperation.this.getOperationService().executeOnPartitions(f, this.toPartitionBitSet());
        }

        private void executeOperations(PartitionAwareOperationFactory givenFactory) {
            final NodeEngine nodeEngine = PartitionIteratingOperation.this.getNodeEngine();
            final PartitionAwareOperationFactory factory = givenFactory.createFactoryOnRunner(nodeEngine, PartitionIteratingOperation.this.partitions);
            final OperationResponseHandlerImpl responseHandler = new OperationResponseHandlerImpl(PartitionIteratingOperation.this.partitions);
            final Object service = PartitionIteratingOperation.this.getServiceName() == null ? null : PartitionIteratingOperation.this.getService();
            PartitionTaskFactory f = new PartitionTaskFactory(){

                public Operation create(int partitionId) {
                    Operation op = factory.createPartitionOperation(partitionId).setNodeEngine(nodeEngine).setPartitionId(partitionId).setReplicaIndex(PartitionIteratingOperation.this.getReplicaIndex()).setOperationResponseHandler(responseHandler).setServiceName(PartitionIteratingOperation.this.getServiceName()).setService(service).setCallerUuid(OffloadImpl.this.extractCallerUuid());
                    OperationAccessor.setCallerAddress(op, PartitionIteratingOperation.this.getCallerAddress());
                    return op;
                }
            };
            PartitionIteratingOperation.this.getOperationService().executeOnPartitions(f, this.toPartitionBitSet());
        }

        private BitSet toPartitionBitSet() {
            BitSet bitSet = new BitSet(PartitionIteratingOperation.this.getNodeEngine().getPartitionService().getPartitionCount());
            for (int partition : PartitionIteratingOperation.this.partitions) {
                bitSet.set(partition);
            }
            return bitSet;
        }

        private String extractCallerUuid() {
            if (PartitionIteratingOperation.this.operationFactory instanceof OperationFactoryWrapper) {
                return ((OperationFactoryWrapper)PartitionIteratingOperation.this.operationFactory).getUuid();
            }
            return PartitionIteratingOperation.this.getCallerUuid();
        }
    }
}

