/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.scheduledexecutor.impl.NamedTaskDecorator;
import com.hazelcast.scheduledexecutor.impl.ScheduledRunnableAdapter;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskStatisticsImpl;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.scheduledexecutor.impl.operations.CancelTaskBackupOperation;
import com.hazelcast.scheduledexecutor.impl.operations.CancelTaskOperation;
import com.hazelcast.scheduledexecutor.impl.operations.DisposeBackupTaskOperation;
import com.hazelcast.scheduledexecutor.impl.operations.DisposeTaskOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetAllScheduledOnMemberOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetAllScheduledOnPartitionOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetAllScheduledOnPartitionOperationFactory;
import com.hazelcast.scheduledexecutor.impl.operations.GetDelayOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetResultOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetStatisticsOperation;
import com.hazelcast.scheduledexecutor.impl.operations.IsCanceledOperation;
import com.hazelcast.scheduledexecutor.impl.operations.IsDoneOperation;
import com.hazelcast.scheduledexecutor.impl.operations.MergeBackupOperation;
import com.hazelcast.scheduledexecutor.impl.operations.MergeOperation;
import com.hazelcast.scheduledexecutor.impl.operations.ReplicationOperation;
import com.hazelcast.scheduledexecutor.impl.operations.ResultReadyNotifyOperation;
import com.hazelcast.scheduledexecutor.impl.operations.ScheduleTaskBackupOperation;
import com.hazelcast.scheduledexecutor.impl.operations.ScheduleTaskOperation;
import com.hazelcast.scheduledexecutor.impl.operations.ShutdownOperation;
import com.hazelcast.scheduledexecutor.impl.operations.SyncBackupStateOperation;
import com.hazelcast.scheduledexecutor.impl.operations.SyncStateOperation;

public class ScheduledExecutorDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.scheduled.executor", -39);
    public static final int TASK_HANDLER = 1;
    public static final int TASK_DESCRIPTOR = 2;
    public static final int RUNNABLE_DEFINITION = 3;
    public static final int RUNNABLE_ADAPTER = 4;
    public static final int NAMED_TASK_DECORATOR = 5;
    public static final int SCHEDULE_OP = 6;
    public static final int SCHEDULE_BACKUP_OP = 7;
    public static final int CANCEL_OP = 8;
    public static final int CANCEL_BACKUP_OP = 9;
    public static final int GET_RESULT = 10;
    public static final int PUBLISH_RESULT = 11;
    public static final int GET_DELAY_OP = 12;
    public static final int IS_DONE_OP = 13;
    public static final int IS_CANCELED_OP = 14;
    public static final int GET_STATS_OP = 15;
    public static final int TASK_STATS = 16;
    public static final int SYNC_STATE_OP = 17;
    public static final int SYNC_BACKUP_STATE_OP = 18;
    public static final int REPLICATION = 19;
    public static final int DISPOSE_TASK_OP = 20;
    public static final int DISPOSE_BACKUP_TASK_OP = 21;
    public static final int GET_ALL_SCHEDULED_ON_MEMBER = 22;
    public static final int GET_ALL_SCHEDULED_ON_PARTITION = 25;
    public static final int GET_ALL_SCHEDULED_ON_PARTITION_OPERATION_FACTORY = 26;
    public static final int SHUTDOWN = 23;
    public static final int TASK_RESOLUTION = 24;
    public static final int MERGE = 27;
    public static final int MERGE_BACKUP = 28;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        return new DataSerializableFactory(){

            @Override
            public IdentifiedDataSerializable create(int typeId) {
                switch (typeId) {
                    case 1: {
                        return new ScheduledTaskHandlerImpl();
                    }
                    case 2: {
                        return new ScheduledTaskDescriptor();
                    }
                    case 3: {
                        return new TaskDefinition();
                    }
                    case 4: {
                        return new ScheduledRunnableAdapter();
                    }
                    case 5: {
                        return new NamedTaskDecorator();
                    }
                    case 12: {
                        return new GetDelayOperation();
                    }
                    case 8: {
                        return new CancelTaskOperation();
                    }
                    case 9: {
                        return new CancelTaskBackupOperation();
                    }
                    case 6: {
                        return new ScheduleTaskOperation();
                    }
                    case 20: {
                        return new DisposeTaskOperation();
                    }
                    case 21: {
                        return new DisposeBackupTaskOperation();
                    }
                    case 13: {
                        return new IsDoneOperation();
                    }
                    case 14: {
                        return new IsCanceledOperation();
                    }
                    case 16: {
                        return new ScheduledTaskStatisticsImpl();
                    }
                    case 15: {
                        return new GetStatisticsOperation();
                    }
                    case 7: {
                        return new ScheduleTaskBackupOperation();
                    }
                    case 17: {
                        return new SyncStateOperation();
                    }
                    case 18: {
                        return new SyncBackupStateOperation();
                    }
                    case 19: {
                        return new ReplicationOperation();
                    }
                    case 22: {
                        return new GetAllScheduledOnMemberOperation();
                    }
                    case 10: {
                        return new GetResultOperation();
                    }
                    case 11: {
                        return new ResultReadyNotifyOperation();
                    }
                    case 23: {
                        return new ShutdownOperation();
                    }
                    case 24: {
                        return new ScheduledTaskResult();
                    }
                    case 25: {
                        return new GetAllScheduledOnPartitionOperation();
                    }
                    case 26: {
                        return new GetAllScheduledOnPartitionOperationFactory();
                    }
                    case 27: {
                        return new MergeOperation();
                    }
                    case 28: {
                        return new MergeBackupOperation();
                    }
                }
                throw new IllegalArgumentException("Illegal Scheduled Executor serializer type ID: " + typeId);
            }
        };
    }
}

