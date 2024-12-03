/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import com.atlassian.migration.agent.service.catalogue.model.TransferStatusResponse;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import lombok.Generated;

public abstract class AbstractContainer
implements Serializable {
    private static final long serialVersionUID = 3422566105512963610L;
    private final Type type;
    private final String containerId;
    private final ContainerStatus status;
    private final List<TransferStatusResponse> transfers;
    private final String statusMessage;

    public AbstractContainer(Type type) {
        this.type = type;
        this.containerId = null;
        this.status = null;
        this.transfers = null;
        this.statusMessage = null;
    }

    public AbstractContainer(Type type, String containerId, ContainerStatus status, List<TransferStatusResponse> transfers, String statusMessage) {
        this.type = type;
        this.containerId = containerId;
        this.status = status;
        this.transfers = transfers;
        this.statusMessage = statusMessage;
    }

    @Generated
    public Type getType() {
        return this.type;
    }

    @Generated
    public String getContainerId() {
        return this.containerId;
    }

    @Generated
    public ContainerStatus getStatus() {
        return this.status;
    }

    @Generated
    public List<TransferStatusResponse> getTransfers() {
        return this.transfers;
    }

    @Generated
    public String getStatusMessage() {
        return this.statusMessage;
    }

    public static enum ContainerStatus {
        READY,
        IN_PROGRESS,
        SUCCESS,
        FAILED,
        INCOMPLETE,
        TIMED_OUT,
        SKIPPED,
        CANCELLING,
        CANCELLED;

        public static final Set<ContainerStatus> SETTLED_STATUSES;

        public static boolean isStatusSettled(ContainerStatus status) {
            return SETTLED_STATUSES.contains((Object)status);
        }

        static {
            SETTLED_STATUSES = Sets.immutableEnumSet((Enum)SUCCESS, (Enum[])new ContainerStatus[]{FAILED, INCOMPLETE, SKIPPED, TIMED_OUT, CANCELLED});
        }
    }

    public static enum Type {
        Site,
        ConfluenceSpace,
        App;

    }
}

