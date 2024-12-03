/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.client;

import com.hazelcast.internal.serialization.PortableHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import java.util.Collection;

public class MapPortableHook
implements PortableHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.portable.map", -10);
    public static final int CREATE_ACCUMULATOR_INFO = 1;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public PortableFactory createFactory() {
        return new MapPortableFactory();
    }

    @Override
    public Collection<ClassDefinition> getBuiltinDefinitions() {
        return null;
    }

    private static class MapPortableFactory
    implements PortableFactory {
        private MapPortableFactory() {
        }

        @Override
        public Portable create(int classId) {
            if (classId == 1) {
                return new AccumulatorInfo();
            }
            throw new IndexOutOfBoundsException("No registered constructor exists with class ID: " + classId);
        }
    }
}

