/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.map.EntryBackupProcessor
 *  com.hazelcast.map.EntryProcessor
 *  com.hazelcast.nio.serialization.IdentifiedDataSerializable
 */
package com.hazelcast.hibernate.distributed;

import com.hazelcast.hibernate.serialization.Expirable;
import com.hazelcast.hibernate.serialization.HibernateDataSerializerHook;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.Map;

public abstract class AbstractRegionCacheEntryProcessor
implements EntryProcessor<Object, Expirable>,
EntryBackupProcessor<Object, Expirable>,
IdentifiedDataSerializable {
    public int getFactoryId() {
        return HibernateDataSerializerHook.F_ID;
    }

    public void processBackup(Map.Entry<Object, Expirable> entry) {
        this.process(entry);
    }

    public EntryBackupProcessor<Object, Expirable> getBackupProcessor() {
        return this;
    }
}

