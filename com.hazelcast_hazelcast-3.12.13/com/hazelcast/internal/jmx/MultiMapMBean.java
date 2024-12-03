/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.MultiMap;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.LocalStatsDelegate;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.jmx.suppliers.LocalMultiMapStatsSupplier;
import com.hazelcast.monitor.LocalMultiMapStats;

@ManagedDescription(value="MultiMap")
public class MultiMapMBean
extends HazelcastMBean<MultiMap> {
    private final LocalStatsDelegate<LocalMultiMapStats> localMultiMapStatsDelegate;

    protected MultiMapMBean(MultiMap managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("MultiMap", managedObject.getName());
        LocalMultiMapStatsSupplier localMultiMapStatsSupplier = new LocalMultiMapStatsSupplier(managedObject);
        this.localMultiMapStatsDelegate = new LocalStatsDelegate<LocalMultiMapStats>(localMultiMapStatsSupplier, this.updateIntervalSec);
    }

    @ManagedAnnotation(value="localOwnedEntryCount")
    @ManagedDescription(value="number of entries owned on this member")
    public long getLocalOwnedEntryCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getOwnedEntryCount();
    }

    @ManagedAnnotation(value="localBackupEntryCount")
    @ManagedDescription(value="the number of backup entries hold on this member")
    public long getLocalBackupEntryCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getBackupEntryCount();
    }

    @ManagedAnnotation(value="localBackupCount")
    @ManagedDescription(value="the number of backups per entry on this member")
    public int getLocalBackupCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getBackupCount();
    }

    @ManagedAnnotation(value="localOwnedEntryMemoryCost")
    @ManagedDescription(value="memory cost (number of bytes) of owned entries on this member")
    public long getLocalOwnedEntryMemoryCost() {
        return this.localMultiMapStatsDelegate.getLocalStats().getOwnedEntryMemoryCost();
    }

    @ManagedAnnotation(value="localBackupEntryMemoryCost")
    @ManagedDescription(value="memory cost (number of bytes) of backup entries on this member.")
    public long getLocalBackupEntryMemoryCost() {
        return this.localMultiMapStatsDelegate.getLocalStats().getBackupEntryMemoryCost();
    }

    @ManagedAnnotation(value="localCreationTime")
    @ManagedDescription(value="the creation time of this map on this member.")
    public long getLocalCreationTime() {
        return this.localMultiMapStatsDelegate.getLocalStats().getCreationTime();
    }

    @ManagedAnnotation(value="localLastAccessTime")
    @ManagedDescription(value="the last access (read) time of the locally owned entries.")
    public long getLocalLastAccessTime() {
        return this.localMultiMapStatsDelegate.getLocalStats().getLastAccessTime();
    }

    @ManagedAnnotation(value="localLastUpdateTime")
    @ManagedDescription(value="the last update time of the locally owned entries.")
    public long getLocalLastUpdateTime() {
        return this.localMultiMapStatsDelegate.getLocalStats().getLastUpdateTime();
    }

    @ManagedAnnotation(value="localHits")
    @ManagedDescription(value="the number of hits (reads) of the locally owned entries.")
    public long getLocalHits() {
        return this.localMultiMapStatsDelegate.getLocalStats().getHits();
    }

    @ManagedAnnotation(value="localLockedEntryCount")
    @ManagedDescription(value="the number of currently locked locally owned keys.")
    public long getLocalLockedEntryCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getLockedEntryCount();
    }

    @ManagedAnnotation(value="localDirtyEntryCount")
    @ManagedDescription(value="the number of entries that the member owns and are dirty on this member")
    public long getLocalDirtyEntryCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getDirtyEntryCount();
    }

    @ManagedAnnotation(value="localPutOperationCount")
    @ManagedDescription(value="the number of put operations on this member")
    public long getLocalPutOperationCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getPutOperationCount();
    }

    @ManagedAnnotation(value="localGetOperationCount")
    @ManagedDescription(value="number of get operations on this member")
    public long getLocalGetOperationCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getGetOperationCount();
    }

    @ManagedAnnotation(value="localRemoveOperationCount")
    @ManagedDescription(value="number of remove operations on this member")
    public long getLocalRemoveOperationCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getRemoveOperationCount();
    }

    @ManagedAnnotation(value="localTotalPutLatency")
    @ManagedDescription(value="the total latency of put operations. To get the average latency, divide to number of puts")
    public long getLocalTotalPutLatency() {
        return this.localMultiMapStatsDelegate.getLocalStats().getTotalPutLatency();
    }

    @ManagedAnnotation(value="localTotalGetLatency")
    @ManagedDescription(value="the total latency of get operations. To get the average latency, divide to number of gets")
    public long getLocalTotalGetLatency() {
        return this.localMultiMapStatsDelegate.getLocalStats().getTotalGetLatency();
    }

    @ManagedAnnotation(value="localTotalRemoveLatency")
    @ManagedDescription(value="the total latency of remove operations. To get the average latency, divide to number of gets")
    public long getLocalTotalRemoveLatency() {
        return this.localMultiMapStatsDelegate.getLocalStats().getTotalRemoveLatency();
    }

    @ManagedAnnotation(value="localMaxPutLatency")
    @ManagedDescription(value="the maximum latency of put operations. To get the average latency, divide to number of puts")
    public long getLocalMaxPutLatency() {
        return this.localMultiMapStatsDelegate.getLocalStats().getMaxPutLatency();
    }

    @ManagedAnnotation(value="localMaxGetLatency")
    @ManagedDescription(value="the maximum latency of get operations. To get the average latency, divide to number of gets")
    public long getLocalMaxGetLatency() {
        return this.localMultiMapStatsDelegate.getLocalStats().getMaxGetLatency();
    }

    @ManagedAnnotation(value="localMaxRemoveLatency")
    @ManagedDescription(value="the maximum latency of remove operations. To get the average latency, divide to number of gets")
    public long getMaxRemoveLatency() {
        return this.localMultiMapStatsDelegate.getLocalStats().getMaxRemoveLatency();
    }

    @ManagedAnnotation(value="localEventOperationCount")
    @ManagedDescription(value="number of events received on this member")
    public long getLocalEventOperationCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getEventOperationCount();
    }

    @ManagedAnnotation(value="localOtherOperationCount")
    @ManagedDescription(value="the total number of other operations on this member")
    public long getLocalOtherOperationCount() {
        return this.localMultiMapStatsDelegate.getLocalStats().getOtherOperationCount();
    }

    @ManagedAnnotation(value="localTotal")
    @ManagedDescription(value="the total number of operations on this member")
    public long localTotal() {
        return this.localMultiMapStatsDelegate.getLocalStats().total();
    }

    @ManagedAnnotation(value="name")
    public String getName() {
        return ((MultiMap)this.managedObject).getName();
    }

    @ManagedAnnotation(value="clear", operation=true)
    public void clear() {
        ((MultiMap)this.managedObject).clear();
    }

    @ManagedAnnotation(value="size")
    public int getSize() {
        return ((MultiMap)this.managedObject).size();
    }

    @ManagedAnnotation(value="config")
    @ManagedDescription(value="MultiMapConfig")
    public String getConfig() {
        return this.service.instance.getConfig().findMultiMapConfig(((MultiMap)this.managedObject).getName()).toString();
    }
}

