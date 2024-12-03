/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationutil;

import com.hazelcast.internal.cluster.impl.operations.JoinOperation;
import com.hazelcast.internal.cluster.impl.operations.WanReplicationOperation;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;

public final class Operations {
    private static final ClassLoader THIS_CLASS_LOADER = OperationAccessor.class.getClassLoader();

    private Operations() {
    }

    public static boolean isJoinOperation(Operation op) {
        return op instanceof JoinOperation && op.getClass().getClassLoader() == THIS_CLASS_LOADER;
    }

    public static boolean isMigrationOperation(Operation op) {
        return op instanceof MigrationCycleOperation && op.getClass().getClassLoader() == THIS_CLASS_LOADER;
    }

    public static boolean isWanReplicationOperation(Operation op) {
        return op instanceof WanReplicationOperation && op.getClass().getClassLoader() == THIS_CLASS_LOADER;
    }
}

