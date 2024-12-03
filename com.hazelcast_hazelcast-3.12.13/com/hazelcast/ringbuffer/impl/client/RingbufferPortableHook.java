/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.client;

import com.hazelcast.internal.serialization.PortableHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.ringbuffer.impl.client.PortableReadResultSet;
import java.util.Collection;

public class RingbufferPortableHook
implements PortableHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.portable.ringbuffer", -29);
    public static final int READ_RESULT_SET = 10;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public PortableFactory createFactory() {
        return new PortableFactory(){

            @Override
            public Portable create(int classId) {
                switch (classId) {
                    case 10: {
                        return new PortableReadResultSet();
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

