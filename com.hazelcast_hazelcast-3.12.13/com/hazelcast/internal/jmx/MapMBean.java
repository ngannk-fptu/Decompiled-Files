/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.IMap;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.LocalStatsDelegate;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.jmx.suppliers.LocalMapStatsSupplier;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.SqlPredicate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@ManagedDescription(value="IMap")
public class MapMBean
extends HazelcastMBean<IMap> {
    private final LocalStatsDelegate<LocalMapStats> localMapStatsDelegate;

    protected MapMBean(IMap managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("IMap", managedObject.getName());
        LocalMapStatsSupplier localMapStatsSupplier = new LocalMapStatsSupplier(managedObject);
        this.localMapStatsDelegate = new LocalStatsDelegate<LocalMapStats>(localMapStatsSupplier, this.updateIntervalSec);
    }

    @ManagedAnnotation(value="localOwnedEntryCount")
    @ManagedDescription(value="number of entries owned on this member")
    public long getLocalOwnedEntryCount() {
        return this.localMapStatsDelegate.getLocalStats().getOwnedEntryCount();
    }

    @ManagedAnnotation(value="localBackupEntryCount")
    @ManagedDescription(value="the number of backup entries hold on this member")
    public long getLocalBackupEntryCount() {
        return this.localMapStatsDelegate.getLocalStats().getBackupEntryCount();
    }

    @ManagedAnnotation(value="localBackupCount")
    @ManagedDescription(value="the number of backups per entry on this member")
    public int getLocalBackupCount() {
        return this.localMapStatsDelegate.getLocalStats().getBackupCount();
    }

    @ManagedAnnotation(value="localOwnedEntryMemoryCost")
    @ManagedDescription(value="memory cost (number of bytes) of owned entries on this member")
    public long getLocalOwnedEntryMemoryCost() {
        return this.localMapStatsDelegate.getLocalStats().getOwnedEntryMemoryCost();
    }

    @ManagedAnnotation(value="localBackupEntryMemoryCost")
    @ManagedDescription(value="memory cost (number of bytes) of backup entries on this member.")
    public long getLocalBackupEntryMemoryCost() {
        return this.localMapStatsDelegate.getLocalStats().getBackupEntryMemoryCost();
    }

    @ManagedAnnotation(value="localCreationTime")
    @ManagedDescription(value="the creation time of this map on this member.")
    public long getLocalCreationTime() {
        return this.localMapStatsDelegate.getLocalStats().getCreationTime();
    }

    @ManagedAnnotation(value="localLastAccessTime")
    @ManagedDescription(value="the last access (read) time of the locally owned entries.")
    public long getLocalLastAccessTime() {
        return this.localMapStatsDelegate.getLocalStats().getLastAccessTime();
    }

    @ManagedAnnotation(value="localLastUpdateTime")
    @ManagedDescription(value="the last update time of the locally owned entries.")
    public long getLocalLastUpdateTime() {
        return this.localMapStatsDelegate.getLocalStats().getLastUpdateTime();
    }

    @ManagedAnnotation(value="localHits")
    @ManagedDescription(value="the number of hits (reads) of the locally owned entries.")
    public long getLocalHits() {
        return this.localMapStatsDelegate.getLocalStats().getHits();
    }

    @ManagedAnnotation(value="localLockedEntryCount")
    @ManagedDescription(value="the number of currently locked locally owned keys.")
    public long getLocalLockedEntryCount() {
        return this.localMapStatsDelegate.getLocalStats().getLockedEntryCount();
    }

    @ManagedAnnotation(value="localDirtyEntryCount")
    @ManagedDescription(value="the number of entries that the member owns and are dirty on this member")
    public long getLocalDirtyEntryCount() {
        return this.localMapStatsDelegate.getLocalStats().getDirtyEntryCount();
    }

    @ManagedAnnotation(value="localPutOperationCount")
    @ManagedDescription(value="the number of put operations on this member")
    public long getLocalPutOperationCount() {
        return this.localMapStatsDelegate.getLocalStats().getPutOperationCount();
    }

    @ManagedAnnotation(value="localGetOperationCount")
    @ManagedDescription(value="number of get operations on this member")
    public long getLocalGetOperationCount() {
        return this.localMapStatsDelegate.getLocalStats().getGetOperationCount();
    }

    @ManagedAnnotation(value="localRemoveOperationCount")
    @ManagedDescription(value="number of remove operations on this member")
    public long getLocalRemoveOperationCount() {
        return this.localMapStatsDelegate.getLocalStats().getRemoveOperationCount();
    }

    @ManagedAnnotation(value="localTotalPutLatency")
    @ManagedDescription(value="the total latency of put operations. To get the average latency, divide to number of puts")
    public long getLocalTotalPutLatency() {
        return this.localMapStatsDelegate.getLocalStats().getTotalPutLatency();
    }

    @ManagedAnnotation(value="localTotalGetLatency")
    @ManagedDescription(value="the total latency of get operations. To get the average latency, divide to number of gets")
    public long getLocalTotalGetLatency() {
        return this.localMapStatsDelegate.getLocalStats().getTotalGetLatency();
    }

    @ManagedAnnotation(value="localTotalRemoveLatency")
    @ManagedDescription(value="the total latency of remove operations. To get the average latency, divide to number of gets")
    public long getLocalTotalRemoveLatency() {
        return this.localMapStatsDelegate.getLocalStats().getTotalRemoveLatency();
    }

    @ManagedAnnotation(value="localMaxPutLatency")
    @ManagedDescription(value="the maximum latency of put operations. To get the average latency, divide to number of puts")
    public long getLocalMaxPutLatency() {
        return this.localMapStatsDelegate.getLocalStats().getMaxPutLatency();
    }

    @ManagedAnnotation(value="localMaxGetLatency")
    @ManagedDescription(value="the maximum latency of get operations. To get the average latency, divide to number of gets")
    public long getLocalMaxGetLatency() {
        return this.localMapStatsDelegate.getLocalStats().getMaxGetLatency();
    }

    @ManagedAnnotation(value="localMaxRemoveLatency")
    @ManagedDescription(value="the maximum latency of remove operations. To get the average latency, divide to number of gets")
    public long getMaxRemoveLatency() {
        return this.localMapStatsDelegate.getLocalStats().getMaxRemoveLatency();
    }

    @ManagedAnnotation(value="localEventOperationCount")
    @ManagedDescription(value="number of events received on this member")
    public long getLocalEventOperationCount() {
        return this.localMapStatsDelegate.getLocalStats().getEventOperationCount();
    }

    @ManagedAnnotation(value="localOtherOperationCount")
    @ManagedDescription(value="the total number of other operations on this member")
    public long getLocalOtherOperationCount() {
        return this.localMapStatsDelegate.getLocalStats().getOtherOperationCount();
    }

    @ManagedAnnotation(value="localTotal")
    @ManagedDescription(value="the total number of operations on this member")
    public long localTotal() {
        return this.localMapStatsDelegate.getLocalStats().total();
    }

    @ManagedAnnotation(value="localHeapCost")
    @ManagedDescription(value="the total heap cost of map, Near Cache and heap cost")
    public long localHeapCost() {
        return this.localMapStatsDelegate.getLocalStats().getHeapCost();
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="name of the map")
    public String getName() {
        return ((IMap)this.managedObject).getName();
    }

    @ManagedAnnotation(value="size")
    @ManagedDescription(value="size of the map")
    public int getSize() {
        return ((IMap)this.managedObject).size();
    }

    @ManagedAnnotation(value="config")
    @ManagedDescription(value="MapConfig")
    public String getConfig() {
        return this.service.instance.getConfig().findMapConfig(((IMap)this.managedObject).getName()).toString();
    }

    @ManagedAnnotation(value="clear", operation=true)
    @ManagedDescription(value="Clear Map")
    public void clear() {
        ((IMap)this.managedObject).clear();
    }

    @ManagedAnnotation(value="values", operation=true)
    public String values(String query) {
        Collection coll;
        if (query != null && !query.isEmpty()) {
            SqlPredicate predicate = new SqlPredicate(query);
            coll = ((IMap)this.managedObject).values(predicate);
        } else {
            coll = ((IMap)this.managedObject).values();
        }
        StringBuilder buf = new StringBuilder();
        if (coll.size() == 0) {
            buf.append("Empty");
        } else {
            buf.append("[");
            for (Object obj : coll) {
                buf.append(obj);
                buf.append(", ");
            }
            buf.replace(buf.length() - 1, buf.length(), "]");
        }
        return buf.toString();
    }

    @ManagedAnnotation(value="entrySet", operation=true)
    public String entrySet(String query) {
        Set entrySet;
        if (query != null && !query.isEmpty()) {
            SqlPredicate predicate = new SqlPredicate(query);
            entrySet = ((IMap)this.managedObject).entrySet(predicate);
        } else {
            entrySet = ((IMap)this.managedObject).entrySet();
        }
        StringBuilder buf = new StringBuilder();
        if (entrySet.size() == 0) {
            buf.append("Empty");
        } else {
            buf.append("[");
            for (Map.Entry entry : entrySet) {
                buf.append("{key:");
                buf.append(entry.getKey());
                buf.append(", value:");
                buf.append(entry.getValue());
                buf.append("}, ");
            }
            buf.replace(buf.length() - 1, buf.length(), "]");
        }
        return buf.toString();
    }
}

