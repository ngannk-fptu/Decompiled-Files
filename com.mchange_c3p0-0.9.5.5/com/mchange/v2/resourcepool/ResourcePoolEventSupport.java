/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.resourcepool.ResourcePool;
import com.mchange.v2.resourcepool.ResourcePoolEvent;
import com.mchange.v2.resourcepool.ResourcePoolListener;
import java.util.HashSet;
import java.util.Set;

public class ResourcePoolEventSupport {
    ResourcePool source;
    Set mlisteners = new HashSet();

    public ResourcePoolEventSupport(ResourcePool source) {
        this.source = source;
    }

    public synchronized void addResourcePoolListener(ResourcePoolListener mlistener) {
        this.mlisteners.add(mlistener);
    }

    public synchronized void removeResourcePoolListener(ResourcePoolListener mlistener) {
        this.mlisteners.remove(mlistener);
    }

    public synchronized void fireResourceAcquired(Object resc, int pool_size, int available_size, int removed_but_unreturned_size) {
        if (!this.mlisteners.isEmpty()) {
            ResourcePoolEvent evt = new ResourcePoolEvent(this.source, resc, false, pool_size, available_size, removed_but_unreturned_size);
            for (ResourcePoolListener rpl : this.mlisteners) {
                rpl.resourceAcquired(evt);
            }
        }
    }

    public synchronized void fireResourceCheckedIn(Object resc, int pool_size, int available_size, int removed_but_unreturned_size) {
        if (!this.mlisteners.isEmpty()) {
            ResourcePoolEvent evt = new ResourcePoolEvent(this.source, resc, false, pool_size, available_size, removed_but_unreturned_size);
            for (ResourcePoolListener rpl : this.mlisteners) {
                rpl.resourceCheckedIn(evt);
            }
        }
    }

    public synchronized void fireResourceCheckedOut(Object resc, int pool_size, int available_size, int removed_but_unreturned_size) {
        if (!this.mlisteners.isEmpty()) {
            ResourcePoolEvent evt = new ResourcePoolEvent(this.source, resc, true, pool_size, available_size, removed_but_unreturned_size);
            for (ResourcePoolListener rpl : this.mlisteners) {
                rpl.resourceCheckedOut(evt);
            }
        }
    }

    public synchronized void fireResourceRemoved(Object resc, boolean checked_out_resource, int pool_size, int available_size, int removed_but_unreturned_size) {
        if (!this.mlisteners.isEmpty()) {
            ResourcePoolEvent evt = new ResourcePoolEvent(this.source, resc, checked_out_resource, pool_size, available_size, removed_but_unreturned_size);
            for (ResourcePoolListener rpl : this.mlisteners) {
                rpl.resourceRemoved(evt);
            }
        }
    }
}

