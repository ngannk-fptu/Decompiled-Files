/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import java.util.List;
import lombok.Generated;

public class ContainerCreateRequest {
    private final List<AbstractContainer> containers;

    @Generated
    public ContainerCreateRequest(List<AbstractContainer> containers) {
        this.containers = containers;
    }

    @Generated
    public List<AbstractContainer> getContainers() {
        return this.containers;
    }
}

