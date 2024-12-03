/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MemoryStoreEvictionPolicy
implements Serializable {
    public static final MemoryStoreEvictionPolicy LRU = new MemoryStoreEvictionPolicy("LRU");
    public static final MemoryStoreEvictionPolicy LFU = new MemoryStoreEvictionPolicy("LFU");
    public static final MemoryStoreEvictionPolicy FIFO = new MemoryStoreEvictionPolicy("FIFO");
    public static final MemoryStoreEvictionPolicy CLOCK = new MemoryStoreEvictionPolicy("CLOCK");
    private static final Logger LOG = LoggerFactory.getLogger((String)MemoryStoreEvictionPolicy.class.getName());
    private final String myName;

    private MemoryStoreEvictionPolicy(String policy) {
        this.myName = policy;
    }

    public String toString() {
        return this.myName;
    }

    public static MemoryStoreEvictionPolicy fromString(String policy) {
        if (policy != null) {
            if (policy.equalsIgnoreCase("LRU")) {
                return LRU;
            }
            if (policy.equalsIgnoreCase("LFU")) {
                return LFU;
            }
            if (policy.equalsIgnoreCase("FIFO")) {
                return FIFO;
            }
            if (policy.equalsIgnoreCase("CLOCK")) {
                return CLOCK;
            }
        }
        LOG.warn("The memoryStoreEvictionPolicy of {} cannot be resolved. The policy will be set to LRU", (Object)policy);
        return LRU;
    }

    public static enum MemoryStoreEvictionPolicyEnum {
        LFU,
        LRU,
        FIFO;

    }
}

