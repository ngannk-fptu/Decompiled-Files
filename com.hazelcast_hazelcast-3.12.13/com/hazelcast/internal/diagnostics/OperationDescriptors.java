/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.impl.operations.Backup;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionIteratingOperation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class OperationDescriptors {
    private static final ConcurrentMap<String, String> DESCRIPTORS = new ConcurrentHashMap<String, String>();

    private OperationDescriptors() {
    }

    public static String toOperationDesc(Operation op) {
        Class<?> operationClass = op.getClass();
        if (PartitionIteratingOperation.class.isAssignableFrom(operationClass)) {
            PartitionIteratingOperation partitionIteratingOperation = (PartitionIteratingOperation)op;
            OperationFactory operationFactory = partitionIteratingOperation.getOperationFactory();
            String desc = (String)DESCRIPTORS.get(operationFactory.getClass().getName());
            if (desc == null) {
                desc = PartitionIteratingOperation.class.getSimpleName() + "(" + operationFactory.getClass().getName() + ")";
                DESCRIPTORS.put(operationFactory.getClass().getName(), desc);
            }
            return desc;
        }
        if (Backup.class.isAssignableFrom(operationClass)) {
            Backup backup = (Backup)op;
            Operation backupOperation = backup.getBackupOp();
            String desc = (String)DESCRIPTORS.get(backupOperation.getClass().getName());
            if (desc == null) {
                desc = Backup.class.getSimpleName() + "(" + backup.getBackupOp().getClass().getName() + ")";
                DESCRIPTORS.put(backupOperation.getClass().getName(), desc);
            }
            return desc;
        }
        return operationClass.getName();
    }
}

