/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.client;

import com.hazelcast.internal.serialization.PortableHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapEntries;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapKeys;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapPortableEntryEvent;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapValueCollection;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collection;

public class ReplicatedMapPortableHook
implements PortableHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.portable.replicated_map", -22);
    public static final int MAP_ENTRIES = 12;
    public static final int MAP_KEY_SET = 13;
    public static final int VALUES_COLLECTION = 14;
    public static final int MAP_ENTRY_EVENT = 18;
    private static final int LENGTH = 19;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public PortableFactory createFactory() {
        return new PortableFactory(){
            private final ConstructorFunction<Integer, Portable>[] constructors = new ConstructorFunction[19];
            {
                this.constructors[12] = new ConstructorFunction<Integer, Portable>(){

                    @Override
                    public Portable createNew(Integer arg) {
                        return new ReplicatedMapEntries();
                    }
                };
                this.constructors[13] = new ConstructorFunction<Integer, Portable>(){

                    @Override
                    public Portable createNew(Integer arg) {
                        return new ReplicatedMapKeys();
                    }
                };
                this.constructors[14] = new ConstructorFunction<Integer, Portable>(){

                    @Override
                    public Portable createNew(Integer arg) {
                        return new ReplicatedMapValueCollection();
                    }
                };
                this.constructors[18] = new ConstructorFunction<Integer, Portable>(){

                    @Override
                    public Portable createNew(Integer arg) {
                        return new ReplicatedMapPortableEntryEvent();
                    }
                };
            }

            @Override
            public Portable create(int classId) {
                return classId > 0 && classId <= this.constructors.length ? this.constructors[classId].createNew(classId) : null;
            }
        };
    }

    @Override
    public Collection<ClassDefinition> getBuiltinDefinitions() {
        return null;
    }
}

