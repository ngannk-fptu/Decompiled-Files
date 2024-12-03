/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.TransferStatus;
import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public enum ExecutionStatus {
    CREATED,
    VALIDATING,
    RUNNING,
    STOPPING,
    DONE,
    STOPPED,
    INCOMPLETE,
    FAILED;

    public static final Set<ExecutionStatus> COMPLETE_STATUSES;
    public static final Set<ExecutionStatus> UNSUCCESSFUL_STATUSES;
    public static final Set<ExecutionStatus> TRIGGER_APP_MIGRATION_STATUSES;
    private static final Map<ExecutionStatus, Set<ExecutionStatus>> TRANSITION_TABLE;
    private static final Map<ExecutionStatus, AbstractContainer.ContainerStatus> CONTAINER_STATUS_MAPPING;
    private static final Map<ExecutionStatus, TransferStatus> TRANSFER_STATUS_MAPPING;
    private static final Set<ExecutionStatus> NONE;

    public boolean canGo(ExecutionStatus next2) {
        return TRANSITION_TABLE.getOrDefault((Object)next2, NONE).contains((Object)this);
    }

    public boolean isUnsuccessful() {
        return UNSUCCESSFUL_STATUSES.contains((Object)this);
    }

    public boolean isCompleted() {
        return COMPLETE_STATUSES.contains((Object)this);
    }

    public boolean canTriggerAppMigration() {
        return TRIGGER_APP_MIGRATION_STATUSES.contains((Object)this);
    }

    public AbstractContainer.ContainerStatus getContainerStatus() {
        return CONTAINER_STATUS_MAPPING.get((Object)this);
    }

    public TransferStatus getTransferStatus() {
        return TRANSFER_STATUS_MAPPING.get((Object)this);
    }

    public static ExecutionStatus mapToExecutionStatus(String input) {
        switch (input) {
            case "queued": {
                return CREATED;
            }
            case "running": {
                return RUNNING;
            }
            case "stopped": {
                return STOPPED;
            }
            case "failed": {
                return FAILED;
            }
            case "migrated": {
                return DONE;
            }
            case "notInAnyPlan": {
                return null;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + input);
    }

    static {
        COMPLETE_STATUSES = Sets.immutableEnumSet((Enum)DONE, (Enum[])new ExecutionStatus[]{STOPPED, FAILED, INCOMPLETE});
        UNSUCCESSFUL_STATUSES = Sets.immutableEnumSet((Enum)STOPPED, (Enum[])new ExecutionStatus[]{STOPPING, FAILED, INCOMPLETE});
        TRIGGER_APP_MIGRATION_STATUSES = Sets.immutableEnumSet((Enum)DONE, (Enum[])new ExecutionStatus[]{INCOMPLETE});
        NONE = Collections.emptySet();
        TRANSITION_TABLE = ImmutableMap.builder().put((Object)CREATED, (Object)Sets.immutableEnumSet((Enum)VALIDATING, (Enum[])new ExecutionStatus[0])).put((Object)VALIDATING, (Object)Sets.immutableEnumSet((Enum)CREATED, (Enum[])new ExecutionStatus[0])).put((Object)RUNNING, (Object)Sets.immutableEnumSet((Enum)CREATED, (Enum[])new ExecutionStatus[]{VALIDATING})).put((Object)STOPPING, (Object)Sets.immutableEnumSet((Enum)CREATED, (Enum[])new ExecutionStatus[]{RUNNING, VALIDATING})).put((Object)DONE, (Object)Sets.immutableEnumSet((Enum)RUNNING, (Enum[])new ExecutionStatus[]{STOPPING})).put((Object)INCOMPLETE, (Object)Sets.immutableEnumSet((Enum)RUNNING, (Enum[])new ExecutionStatus[]{STOPPING})).put((Object)STOPPED, (Object)Sets.immutableEnumSet((Enum)CREATED, (Enum[])new ExecutionStatus[]{RUNNING, STOPPING})).put((Object)FAILED, (Object)Sets.immutableEnumSet((Enum)RUNNING, (Enum[])new ExecutionStatus[]{STOPPING, CREATED, VALIDATING})).build();
        CONTAINER_STATUS_MAPPING = ImmutableMap.builder().put((Object)CREATED, (Object)AbstractContainer.ContainerStatus.READY).put((Object)RUNNING, (Object)AbstractContainer.ContainerStatus.IN_PROGRESS).put((Object)STOPPING, (Object)AbstractContainer.ContainerStatus.CANCELLING).put((Object)DONE, (Object)AbstractContainer.ContainerStatus.SUCCESS).put((Object)STOPPED, (Object)AbstractContainer.ContainerStatus.CANCELLED).put((Object)FAILED, (Object)AbstractContainer.ContainerStatus.FAILED).build();
        TRANSFER_STATUS_MAPPING = ImmutableMap.builder().put((Object)CREATED, (Object)TransferStatus.READY).put((Object)RUNNING, (Object)TransferStatus.IN_PROGRESS).put((Object)STOPPING, (Object)TransferStatus.CANCELLING).put((Object)DONE, (Object)TransferStatus.SUCCESS).put((Object)STOPPED, (Object)TransferStatus.CANCELLED).put((Object)FAILED, (Object)TransferStatus.FAILED).build();
    }
}

