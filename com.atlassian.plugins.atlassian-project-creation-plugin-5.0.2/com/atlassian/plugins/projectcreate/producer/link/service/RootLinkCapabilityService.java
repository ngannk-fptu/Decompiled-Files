/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.projectcreate.linking.spi.AggregateRootLinkType
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugins.projectcreate.producer.link.service;

import com.atlassian.plugins.projectcreate.linking.spi.AggregateRootLinkType;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RootLinkCapabilityService {
    private final List<AggregateRootLinkType> aggregateRootLinkCapabilities;

    public RootLinkCapabilityService(List<AggregateRootLinkType> aggregateRootLinkCapabilities) {
        this.aggregateRootLinkCapabilities = Lists.newArrayList(aggregateRootLinkCapabilities);
        Collections.sort(this.aggregateRootLinkCapabilities, new Comparator<AggregateRootLinkType>(){

            @Override
            public int compare(AggregateRootLinkType o1, AggregateRootLinkType o2) {
                return new Integer(o2.getWeight()).compareTo(o1.getWeight());
            }
        });
    }

    public Iterable<AggregateRootLinkType> getSortedLinkers() {
        return Collections.unmodifiableList(this.aggregateRootLinkCapabilities);
    }
}

