/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapProxy;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@ManagedDescription(value="ReplicatedMap")
public class ReplicatedMapMBean
extends HazelcastMBean<ReplicatedMapProxy> {
    protected ReplicatedMapMBean(ReplicatedMapProxy managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("ReplicatedMap", managedObject.getName());
    }

    @ManagedAnnotation(value="localOwnedEntryCount")
    @ManagedDescription(value="number of entries owned on this member")
    public long getLocalOwnedEntryCount() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getOwnedEntryCount();
    }

    @ManagedAnnotation(value="localCreationTime")
    @ManagedDescription(value="the creation time of this map on this member.")
    public long getLocalCreationTime() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getCreationTime();
    }

    @ManagedAnnotation(value="localLastAccessTime")
    @ManagedDescription(value="the last access (read) time of the locally owned entries.")
    public long getLocalLastAccessTime() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getLastAccessTime();
    }

    @ManagedAnnotation(value="localLastUpdateTime")
    @ManagedDescription(value="the last update time of the locally owned entries.")
    public long getLocalLastUpdateTime() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getLastUpdateTime();
    }

    @ManagedAnnotation(value="localHits")
    @ManagedDescription(value="the number of hits (reads) of the locally owned entries.")
    public long getLocalHits() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getHits();
    }

    @ManagedAnnotation(value="localPutOperationCount")
    @ManagedDescription(value="the number of put operations on this member")
    public long getLocalPutOperationCount() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getPutOperationCount();
    }

    @ManagedAnnotation(value="localGetOperationCount")
    @ManagedDescription(value="number of get operations on this member")
    public long getLocalGetOperationCount() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getGetOperationCount();
    }

    @ManagedAnnotation(value="localRemoveOperationCount")
    @ManagedDescription(value="number of remove operations on this member")
    public long getLocalRemoveOperationCount() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getRemoveOperationCount();
    }

    @ManagedAnnotation(value="localTotalPutLatency")
    @ManagedDescription(value="the total latency of put operations. To get the average latency, divide to number of puts")
    public long getLocalTotalPutLatency() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getTotalPutLatency();
    }

    @ManagedAnnotation(value="localTotalGetLatency")
    @ManagedDescription(value="the total latency of get operations. To get the average latency, divide to number of gets")
    public long getLocalTotalGetLatency() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getTotalGetLatency();
    }

    @ManagedAnnotation(value="localTotalRemoveLatency")
    @ManagedDescription(value="the total latency of remove operations. To get the average latency, divide to number of gets")
    public long getLocalTotalRemoveLatency() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getTotalRemoveLatency();
    }

    @ManagedAnnotation(value="localMaxPutLatency")
    @ManagedDescription(value="the maximum latency of put operations. To get the average latency, divide to number of puts")
    public long getLocalMaxPutLatency() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getMaxPutLatency();
    }

    @ManagedAnnotation(value="localMaxGetLatency")
    @ManagedDescription(value="the maximum latency of get operations. To get the average latency, divide to number of gets")
    public long getLocalMaxGetLatency() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getMaxGetLatency();
    }

    @ManagedAnnotation(value="localMaxRemoveLatency")
    @ManagedDescription(value="the maximum latency of remove operations. To get the average latency, divide to number of gets")
    public long getMaxRemoveLatency() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getMaxRemoveLatency();
    }

    @ManagedAnnotation(value="localEventOperationCount")
    @ManagedDescription(value="number of events received on this member")
    public long getLocalEventOperationCount() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getEventOperationCount();
    }

    @ManagedAnnotation(value="localOtherOperationCount")
    @ManagedDescription(value="the total number of other operations on this member")
    public long getLocalOtherOperationCount() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().getOtherOperationCount();
    }

    @ManagedAnnotation(value="localTotal")
    @ManagedDescription(value="the total number of operations on this member")
    public long localTotal() {
        return ((ReplicatedMapProxy)this.managedObject).getReplicatedMapStats().total();
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="name of the map")
    public String getName() {
        return ((ReplicatedMapProxy)this.managedObject).getName();
    }

    @ManagedAnnotation(value="size")
    @ManagedDescription(value="size of the map")
    public int getSize() {
        return ((ReplicatedMapProxy)this.managedObject).size();
    }

    @ManagedAnnotation(value="config")
    @ManagedDescription(value="ReplicatedMapConfig")
    public String getConfig() {
        return this.service.instance.getConfig().findReplicatedMapConfig(((ReplicatedMapProxy)this.managedObject).getName()).toString();
    }

    @ManagedAnnotation(value="clear", operation=true)
    @ManagedDescription(value="Clear Map")
    public void clear() {
        ((ReplicatedMapProxy)this.managedObject).clear();
    }

    @ManagedAnnotation(value="values", operation=true)
    public String values() {
        Collection coll = ((ReplicatedMapProxy)this.managedObject).values();
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
    public String entrySet() {
        Set entrySet = ((ReplicatedMapProxy)this.managedObject).entrySet();
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

