/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.internal.partition.ReplicaErrorLogger;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionReplicaManager;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class PartitionBackupReplicaAntiEntropyOperation
extends AbstractPartitionOperation
implements PartitionAwareOperation,
AllowedDuringPassiveState,
Versioned {
    private Map<ServiceNamespace, Long> versions;
    private boolean returnResponse;
    private boolean response = true;

    public PartitionBackupReplicaAntiEntropyOperation() {
    }

    public PartitionBackupReplicaAntiEntropyOperation(Map<ServiceNamespace, Long> versions, boolean returnResponse) {
        this.versions = versions;
        this.returnResponse = returnResponse;
    }

    @Override
    public void run() throws Exception {
        if (!this.isNodeStartCompleted()) {
            this.response = false;
            return;
        }
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        PartitionReplicaManager replicaManager = partitionService.getReplicaManager();
        replicaManager.retainNamespaces(partitionId, this.versions.keySet());
        Iterator<Map.Entry<ServiceNamespace, Long>> iter = this.versions.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<ServiceNamespace, Long> entry = iter.next();
            ServiceNamespace ns = entry.getKey();
            long primaryVersion = entry.getValue();
            long[] currentVersions = replicaManager.getPartitionReplicaVersions(partitionId, ns);
            long currentVersion = currentVersions[replicaIndex - 1];
            if (replicaManager.isPartitionReplicaVersionDirty(partitionId, ns) || currentVersion != primaryVersion) {
                this.logBackupVersionMismatch(ns, currentVersion, primaryVersion);
                continue;
            }
            iter.remove();
        }
        if (!this.versions.isEmpty()) {
            replicaManager.triggerPartitionReplicaSync(partitionId, this.versions.keySet(), replicaIndex);
            this.response = false;
        }
    }

    private boolean isNodeStartCompleted() {
        ILogger logger;
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        boolean startCompleted = nodeEngine.getNode().getNodeExtension().isStartCompleted();
        if (!startCompleted && (logger = this.getLogger()).isFinestEnabled()) {
            logger.finest("Anti-entropy operation for partitionId=" + this.getPartitionId() + ", replicaIndex=" + this.getReplicaIndex() + " is received before startup is completed.");
        }
        return startCompleted;
    }

    private void logBackupVersionMismatch(ServiceNamespace ns, long currentVersion, long primaryVersion) {
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            logger.finest("partitionId=" + this.getPartitionId() + ", replicaIndex=" + this.getReplicaIndex() + ", ns=" + ns + " version is not matching to version of the owner or replica is marked as dirty!  Expected-version=" + primaryVersion + ", Current-version=" + currentVersion);
        }
    }

    @Override
    public boolean returnsResponse() {
        return this.returnResponse;
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public boolean validatesTarget() {
        return false;
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public void logError(Throwable e) {
        ReplicaErrorLogger.log(e, this.getLogger());
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.versions.size());
        for (Map.Entry<ServiceNamespace, Long> entry : this.versions.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeLong(entry.getValue());
        }
        out.writeBoolean(this.returnResponse);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int len = in.readInt();
        this.versions = new HashMap<ServiceNamespace, Long>(len);
        for (int i = 0; i < len; ++i) {
            ServiceNamespace ns = (ServiceNamespace)in.readObject();
            long v = in.readLong();
            this.versions.put(ns, v);
        }
        this.returnResponse = in.readBoolean();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", versions=").append(this.versions);
    }

    @Override
    public int getId() {
        return 3;
    }
}

