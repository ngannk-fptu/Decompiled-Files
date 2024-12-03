/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.system.task;

import com.atlassian.confluence.impl.system.task.CreateIndexSnapshotMaintenanceTask;
import com.atlassian.confluence.impl.system.task.ReIndexMaintenanceTask;
import com.atlassian.confluence.impl.system.task.RestoreIndexSnapshotMaintenanceTask;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;
import java.util.Optional;

public enum SystemMaintenanceTaskType {
    CREATE_INDEX_SNAPSHOT(CreateIndexSnapshotMaintenanceTask.class),
    RESTORE_INDEX_SNAPSHOT(RestoreIndexSnapshotMaintenanceTask.class),
    REINDEX(ReIndexMaintenanceTask.class);

    private Class<? extends SystemMaintenanceTask> taskClazz;

    private SystemMaintenanceTaskType(Class<? extends SystemMaintenanceTask> taskClazz) {
        this.taskClazz = taskClazz;
    }

    public Class<? extends SystemMaintenanceTask> getTaskClazz() {
        return this.taskClazz;
    }

    public static Optional<SystemMaintenanceTaskType> forTask(SystemMaintenanceTask task) {
        for (SystemMaintenanceTaskType type : SystemMaintenanceTaskType.values()) {
            if (type.taskClazz != task.getClass()) continue;
            return Optional.of(type);
        }
        return Optional.empty();
    }
}

