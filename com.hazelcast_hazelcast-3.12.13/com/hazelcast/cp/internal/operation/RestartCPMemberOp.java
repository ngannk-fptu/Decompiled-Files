/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation;

import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.RaftSystemOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.Offload;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class RestartCPMemberOp
extends Operation
implements RaftSystemOperation,
IdentifiedDataSerializable {
    private long seed;

    public RestartCPMemberOp() {
    }

    public RestartCPMemberOp(long seed) {
        this.seed = seed;
    }

    @Override
    public CallStatus call() throws Exception {
        return new OffloadImpl();
    }

    @Override
    public final boolean validatesTarget() {
        return false;
    }

    @Override
    public final String getServiceName() {
        return "hz:core:raft";
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 40;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeLong(this.seed);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.seed = in.readLong();
    }

    private class RestartLocalTask
    implements Runnable {
        private RestartLocalTask() {
        }

        @Override
        public void run() {
            RaftService service = (RaftService)RestartCPMemberOp.this.getService();
            try {
                service.restartLocal(RestartCPMemberOp.this.seed);
                RestartCPMemberOp.this.sendResponse(null);
            }
            catch (Exception e) {
                RestartCPMemberOp.this.sendResponse(e);
            }
        }
    }

    private final class OffloadImpl
    extends Offload {
        private OffloadImpl() {
            super(RestartCPMemberOp.this);
        }

        @Override
        public void start() {
            RestartCPMemberOp.this.getNodeEngine().getExecutionService().execute("hz:system", new RestartLocalTask());
        }
    }
}

