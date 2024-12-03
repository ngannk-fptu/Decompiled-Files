/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.IQueue;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.LocalStatsDelegate;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.jmx.suppliers.LocalQueueStatsSupplier;
import com.hazelcast.monitor.LocalQueueStats;

@ManagedDescription(value="IQueue")
public class QueueMBean
extends HazelcastMBean<IQueue> {
    private final LocalStatsDelegate<LocalQueueStats> localQueueStatsDelegate;

    protected QueueMBean(IQueue managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("IQueue", managedObject.getName());
        LocalQueueStatsSupplier localQueueStatsSupplier = new LocalQueueStatsSupplier(managedObject);
        this.localQueueStatsDelegate = new LocalStatsDelegate<LocalQueueStats>(localQueueStatsSupplier, this.updateIntervalSec);
    }

    @ManagedAnnotation(value="localOwnedItemCount")
    @ManagedDescription(value="the number of owned items in this member.")
    public long getLocalOwnedItemCount() {
        return this.localQueueStatsDelegate.getLocalStats().getOwnedItemCount();
    }

    @ManagedAnnotation(value="localBackupItemCount")
    @ManagedDescription(value="the number of backup items in this member.")
    public long getLocalBackupItemCount() {
        return this.localQueueStatsDelegate.getLocalStats().getBackupItemCount();
    }

    @ManagedAnnotation(value="localMinAge")
    @ManagedDescription(value="the min age of the items in this member.")
    public long getLocalMinAge() {
        return this.localQueueStatsDelegate.getLocalStats().getMinAge();
    }

    @ManagedAnnotation(value="localMaxAge")
    @ManagedDescription(value="the max age of the items in this member.")
    public long getLocalMaxAge() {
        return this.localQueueStatsDelegate.getLocalStats().getMaxAge();
    }

    @ManagedAnnotation(value="localAvgAge")
    @ManagedDescription(value="the average age of the items in this member.")
    public long getLocalAvgAge() {
        return this.localQueueStatsDelegate.getLocalStats().getAvgAge();
    }

    @ManagedAnnotation(value="localOfferOperationCount")
    @ManagedDescription(value="the number of offer/put/add operations in this member")
    public long getLocalOfferOperationCount() {
        return this.localQueueStatsDelegate.getLocalStats().getOfferOperationCount();
    }

    @ManagedAnnotation(value="localRejectedOfferOperationCount")
    @ManagedDescription(value="the number of rejected offers in this member")
    public long getLocalRejectedOfferOperationCount() {
        return this.localQueueStatsDelegate.getLocalStats().getRejectedOfferOperationCount();
    }

    @ManagedAnnotation(value="localPollOperationCount")
    @ManagedDescription(value="the number of poll/take/remove operations in this member")
    public long getLocalPollOperationCount() {
        return this.localQueueStatsDelegate.getLocalStats().getPollOperationCount();
    }

    @ManagedAnnotation(value="localEmptyPollOperationCount")
    @ManagedDescription(value="number of null returning poll operations in this member")
    public long getLocalEmptyPollOperationCount() {
        return this.localQueueStatsDelegate.getLocalStats().getEmptyPollOperationCount();
    }

    @ManagedAnnotation(value="localOtherOperationsCount")
    @ManagedDescription(value="number of other operations in this member")
    public long getLocalOtherOperationsCount() {
        return this.localQueueStatsDelegate.getLocalStats().getOtherOperationsCount();
    }

    @ManagedAnnotation(value="localEventOperationCount")
    @ManagedDescription(value="number of event operations in this member")
    public long getLocalEventOperationCount() {
        return this.localQueueStatsDelegate.getLocalStats().getEventOperationCount();
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the DistributedObject")
    public String getName() {
        return ((IQueue)this.managedObject).getName();
    }

    @ManagedAnnotation(value="partitionKey")
    @ManagedDescription(value="the partitionKey")
    public String getPartitionKey() {
        return ((IQueue)this.managedObject).getPartitionKey();
    }

    @ManagedAnnotation(value="config")
    @ManagedDescription(value="QueueConfig")
    public String getConfig() {
        String managedObjectName = ((IQueue)this.managedObject).getName();
        Config config = this.service.instance.getConfig();
        QueueConfig queueConfig = config.findQueueConfig(managedObjectName);
        return queueConfig.toString();
    }

    @ManagedAnnotation(value="clear", operation=true)
    @ManagedDescription(value="Clear Queue")
    public void clear() {
        ((IQueue)this.managedObject).clear();
    }
}

