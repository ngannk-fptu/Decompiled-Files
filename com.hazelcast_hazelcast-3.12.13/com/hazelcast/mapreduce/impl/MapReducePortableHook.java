/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.internal.serialization.PortableHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.mapreduce.impl.task.TransferableJobProcessInformation;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collection;

public class MapReducePortableHook
implements PortableHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.portable.map_reduce", -23);
    public static final int TRANSFERABLE_PROCESS_INFORMATION = 4;
    private static final int LENGTH = 5;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public PortableFactory createFactory() {
        return new PortableFactory(){
            private final ConstructorFunction<Integer, Portable>[] constructors = new ConstructorFunction[5];
            {
                this.constructors[4] = new ConstructorFunction<Integer, Portable>(){

                    @Override
                    public Portable createNew(Integer arg) {
                        return new TransferableJobProcessInformation();
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

