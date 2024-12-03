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

public class ContainersFetchResponse {
    private final List<AbstractContainer> containers;
    private final String nextId;

    @Generated
    public ContainersFetchResponse(List<AbstractContainer> containers, String nextId) {
        this.containers = containers;
        this.nextId = nextId;
    }

    @Generated
    public List<AbstractContainer> getContainers() {
        return this.containers;
    }

    @Generated
    public String getNextId() {
        return this.nextId;
    }
}

