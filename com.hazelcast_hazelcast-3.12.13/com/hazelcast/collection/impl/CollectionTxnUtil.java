/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl;

import com.hazelcast.collection.impl.txncollection.CollectionTxnOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class CollectionTxnUtil {
    private CollectionTxnUtil() {
    }

    public static long getItemId(CollectionTxnOperation operation) {
        int pollOperation = operation.isRemoveOperation() ? 1 : -1;
        return (long)pollOperation * operation.getItemId();
    }

    public static boolean isRemove(long itemId) {
        return itemId > 0L;
    }

    public static void before(List<Operation> operationList, Operation wrapper) throws Exception {
        for (Operation operation : operationList) {
            operation.setService(wrapper.getService());
            operation.setServiceName(wrapper.getServiceName());
            operation.setCallerUuid(wrapper.getCallerUuid());
            operation.setNodeEngine(wrapper.getNodeEngine());
            operation.setPartitionId(wrapper.getPartitionId());
            operation.beforeRun();
        }
    }

    public static List<Operation> run(List<Operation> operationList) throws Exception {
        LinkedList<Operation> backupList = new LinkedList<Operation>();
        for (Operation operation : operationList) {
            BackupAwareOperation backupAwareOperation;
            operation.run();
            if (!(operation instanceof BackupAwareOperation) || !(backupAwareOperation = (BackupAwareOperation)((Object)operation)).shouldBackup()) continue;
            backupList.add(backupAwareOperation.getBackupOperation());
        }
        return backupList;
    }

    public static void after(List<Operation> operationList) throws Exception {
        for (Operation operation : operationList) {
            operation.afterRun();
        }
    }

    public static void write(ObjectDataOutput out, List<Operation> operationList) throws IOException {
        out.writeInt(operationList.size());
        for (Operation operation : operationList) {
            out.writeObject(operation);
        }
    }

    public static List<Operation> read(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        ArrayList<Operation> operationList = new ArrayList<Operation>(size);
        for (int i = 0; i < size; ++i) {
            Operation operation = (Operation)in.readObject();
            operationList.add(operation);
        }
        return operationList;
    }
}

