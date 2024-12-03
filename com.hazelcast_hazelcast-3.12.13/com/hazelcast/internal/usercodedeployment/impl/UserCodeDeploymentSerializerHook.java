/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.internal.usercodedeployment.impl.ClassData;
import com.hazelcast.internal.usercodedeployment.impl.operation.ClassDataFinderOperation;
import com.hazelcast.internal.usercodedeployment.impl.operation.DeployClassesOperation;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public class UserCodeDeploymentSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.user.code.deployment", -40);
    public static final int CLASS_DATA = 0;
    public static final int CLASS_DATA_FINDER_OP = 1;
    public static final int DEPLOY_CLASSES_OP = 2;
    public static final int LEN = 3;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[]{new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClassData();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClassDataFinderOperation();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DeployClassesOperation();
            }
        }};
        return new ArrayDataSerializableFactory(constructors);
    }
}

