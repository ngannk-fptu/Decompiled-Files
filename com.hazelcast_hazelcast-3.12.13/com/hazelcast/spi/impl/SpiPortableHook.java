/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.internal.serialization.PortableHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.security.UsernamePasswordCredentials;
import com.hazelcast.spi.impl.PortableCachePartitionLostEvent;
import com.hazelcast.spi.impl.PortableCollection;
import com.hazelcast.spi.impl.PortableDistributedObjectEvent;
import com.hazelcast.spi.impl.PortableEntryEvent;
import com.hazelcast.spi.impl.PortableItemEvent;
import com.hazelcast.spi.impl.PortableMapPartitionLostEvent;
import com.hazelcast.spi.impl.PortablePartitionLostEvent;
import java.util.Collection;

public final class SpiPortableHook
implements PortableHook {
    public static final int ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.portable.spi", -1);
    public static final int USERNAME_PWD_CRED = 1;
    public static final int COLLECTION = 2;
    public static final int ITEM_EVENT = 3;
    public static final int ENTRY_EVENT = 4;
    public static final int DISTRIBUTED_OBJECT_EVENT = 5;
    public static final int MAP_PARTITION_LOST_EVENT = 6;
    public static final int PARTITION_LOST_EVENT = 7;
    public static final int CACHE_PARTITION_LOST_EVENT = 8;

    @Override
    public int getFactoryId() {
        return ID;
    }

    @Override
    public PortableFactory createFactory() {
        return new PortableFactory(){

            @Override
            public Portable create(int classId) {
                switch (classId) {
                    case 1: {
                        return new UsernamePasswordCredentials();
                    }
                    case 2: {
                        return new PortableCollection();
                    }
                    case 3: {
                        return new PortableItemEvent();
                    }
                    case 4: {
                        return new PortableEntryEvent();
                    }
                    case 5: {
                        return new PortableDistributedObjectEvent();
                    }
                    case 6: {
                        return new PortableMapPartitionLostEvent();
                    }
                    case 7: {
                        return new PortablePartitionLostEvent();
                    }
                    case 8: {
                        return new PortableCachePartitionLostEvent();
                    }
                }
                return null;
            }
        };
    }

    @Override
    public Collection<ClassDefinition> getBuiltinDefinitions() {
        return null;
    }
}

