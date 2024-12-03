/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.internal.management.dto.MapConfigDTO;
import com.hazelcast.internal.management.dto.PermissionConfigDTO;
import com.hazelcast.internal.management.operation.AddWanConfigLegacyOperation;
import com.hazelcast.internal.management.operation.ScriptExecutorOperation;
import com.hazelcast.internal.management.operation.SetLicenseOperation;
import com.hazelcast.internal.management.operation.UpdateManagementCenterUrlOperation;
import com.hazelcast.internal.management.operation.UpdateMapConfigOperation;
import com.hazelcast.internal.management.operation.UpdatePermissionConfigOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public class ManagementDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.management", -36);
    public static final int SCRIPT_EXECUTOR = 0;
    public static final int UPDATE_MANAGEMENT_CENTER_URL = 1;
    public static final int UPDATE_MAP_CONFIG = 2;
    public static final int MAP_CONFIG_DTO = 3;
    public static final int ADD_WAN_CONFIG = 4;
    public static final int UPDATE_PERMISSION_CONFIG_OPERATION = 5;
    public static final int PERMISSION_CONFIG_DTO = 6;
    public static final int SET_LICENSE = 7;
    private static final int LEN = 8;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[]{new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ScriptExecutorOperation();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new UpdateManagementCenterUrlOperation();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new UpdateMapConfigOperation();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapConfigDTO();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AddWanConfigLegacyOperation();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new UpdatePermissionConfigOperation();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PermissionConfigDTO();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetLicenseOperation();
            }
        }};
        return new ArrayDataSerializableFactory(constructors);
    }
}

