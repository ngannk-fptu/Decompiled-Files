/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.config.OnJoinPermissionOperationName;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.impl.operations.AbstractJoinOperation;
import com.hazelcast.internal.management.operation.UpdatePermissionConfigOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.OperationResponseHandlerFactory;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Arrays;

public class OnJoinOp
extends AbstractJoinOperation
implements UrgentSystemOperation,
TargetAware {
    private Operation[] operations;

    public OnJoinOp() {
    }

    public OnJoinOp(Operation ... ops) {
        for (Operation op : ops) {
            Preconditions.checkNotNull(op, "op can't be null");
            Preconditions.checkNegative(op.getPartitionId(), "Post join operation can not have a partition ID!");
        }
        this.operations = ops;
    }

    @Override
    public String getServiceName() {
        return "hz:core:clusterService";
    }

    @Override
    public void beforeRun() throws Exception {
        if (this.operations != null && this.operations.length > 0) {
            NodeEngine nodeEngine = this.getNodeEngine();
            int len = this.operations.length;
            OperationResponseHandler responseHandler = OperationResponseHandlerFactory.createErrorLoggingResponseHandler(this.getLogger());
            for (int i = 0; i < len; ++i) {
                Operation op = this.operations[i];
                op.setNodeEngine(nodeEngine);
                op.setOperationResponseHandler(responseHandler);
                OperationAccessor.setCallerAddress(op, this.getCallerAddress());
                OperationAccessor.setConnection(op, this.getConnection());
                this.operations[i] = op;
            }
        }
    }

    @Override
    public void run() throws Exception {
        if (this.operations != null && this.operations.length > 0) {
            SecurityConfig securityConfig = this.getNodeEngine().getConfig().getSecurityConfig();
            boolean runPermissionUpdates = securityConfig.getOnJoinPermissionOperation() == OnJoinPermissionOperationName.RECEIVE;
            for (Operation op : this.operations) {
                if (op instanceof UpdatePermissionConfigOperation && !runPermissionUpdates) continue;
                try {
                    op.beforeRun();
                    op.run();
                    op.afterRun();
                }
                catch (Exception e) {
                    this.getLogger().warning("Error while running post-join operation: " + op, e);
                }
            }
            ClusterService clusterService = (ClusterService)this.getService();
            if (clusterService.isMaster()) {
                OperationService operationService = this.getNodeEngine().getOperationService();
                for (Member member : clusterService.getMembers()) {
                    if (member.localMember() || member.getUuid().equals(this.getCallerUuid())) continue;
                    OnJoinOp operation = new OnJoinOp(this.operations);
                    operationService.invokeOnTarget(this.getServiceName(), operation, member.getAddress());
                }
            }
        }
    }

    @Override
    public void onExecutionFailure(Throwable e) {
        if (this.operations != null) {
            for (Operation op : this.operations) {
                this.onOperationFailure(op, e);
            }
        }
    }

    private void onOperationFailure(Operation op, Throwable e) {
        try {
            op.onExecutionFailure(e);
        }
        catch (Throwable t) {
            this.getLogger().warning("While calling operation.onFailure(). op: " + op, t);
        }
    }

    @Override
    public boolean validatesTarget() {
        return false;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        int len = this.operations != null ? this.operations.length : 0;
        out.writeInt(len);
        if (len > 0) {
            for (Operation op : this.operations) {
                out.writeObject(op);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int len = in.readInt();
        this.operations = new Operation[len];
        for (int i = 0; i < len; ++i) {
            this.operations[i] = (Operation)in.readObject();
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", operations=").append(Arrays.toString(this.operations));
    }

    @Override
    public int getId() {
        return 22;
    }

    @Override
    public void setTarget(Address address) {
        for (Operation op : this.operations) {
            if (!(op instanceof TargetAware)) continue;
            ((TargetAware)((Object)op)).setTarget(address);
        }
    }
}

