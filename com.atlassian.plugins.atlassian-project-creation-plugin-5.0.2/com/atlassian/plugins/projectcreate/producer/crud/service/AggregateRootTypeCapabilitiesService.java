/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.plugins.projectcreate.producer.crud.service;

import com.atlassian.plugins.projectcreate.spi.AggregateRootTypeCapability;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import java.util.List;

public class AggregateRootTypeCapabilitiesService {
    private final List<AggregateRootTypeCapability> aggregateRootTypeCapabilities;

    public AggregateRootTypeCapabilitiesService(List<AggregateRootTypeCapability> aggregateRootTypeCapabilities) {
        this.aggregateRootTypeCapabilities = aggregateRootTypeCapabilities;
    }

    public List<AggregateRootTypeCapability> getCapabilities() {
        return this.aggregateRootTypeCapabilities;
    }

    public Option<AggregateRootTypeCapability> getCapability(String entityType) {
        return Iterables.findFirst(this.aggregateRootTypeCapabilities, input -> input.getType().equals(entityType));
    }
}

