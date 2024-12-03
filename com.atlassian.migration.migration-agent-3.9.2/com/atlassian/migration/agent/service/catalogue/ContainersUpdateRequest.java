/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import lombok.Generated;

public class ContainersUpdateRequest {
    private final AbstractContainer.ContainerStatus status;
    private final String statusMessage;

    @Generated
    public ContainersUpdateRequest(AbstractContainer.ContainerStatus status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    @Generated
    public AbstractContainer.ContainerStatus getStatus() {
        return this.status;
    }

    @Generated
    public String getStatusMessage() {
        return this.statusMessage;
    }
}

