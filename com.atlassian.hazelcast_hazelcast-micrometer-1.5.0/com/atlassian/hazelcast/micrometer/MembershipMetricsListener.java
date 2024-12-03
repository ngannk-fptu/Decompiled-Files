/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.MemberAttributeEvent
 *  com.hazelcast.core.MembershipEvent
 *  com.hazelcast.core.MembershipListener
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.hazelcast.micrometer;

import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Arrays;
import java.util.Collection;

final class MembershipMetricsListener
implements MembershipListener {
    private static final String METER_PREFIX = "hazelcast.membership.";
    private final MeterRegistry meterRegistry;

    MembershipMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void memberAdded(MembershipEvent event) {
        this.incrementCounter("memberAdded", this.tags(event));
        this.updateMemberCount(event);
    }

    public void memberRemoved(MembershipEvent event) {
        this.incrementCounter("memberRemoved", this.tags(event));
        this.updateMemberCount(event);
    }

    public void memberAttributeChanged(MemberAttributeEvent event) {
        this.incrementCounter("memberRemoved", this.tags(event));
    }

    private void incrementCounter(String meterName, Collection<Tag> tags) {
        this.meterRegistry.counter(METER_PREFIX + meterName, tags).increment();
    }

    private void updateMemberCount(MembershipEvent event) {
        this.meterRegistry.gauge("hazelcast.membership.memberCount", this.tags(event), (Number)event.getMembers().size());
    }

    private Collection<Tag> tags(MembershipEvent event) {
        return Arrays.asList(Tag.of((String)"memberAddress", (String)String.valueOf(event.getMember().getAddress())));
    }

    private Collection<Tag> tags(MemberAttributeEvent event) {
        return Arrays.asList(Tag.of((String)"memberAddress", (String)String.valueOf(event.getMember().getAddress())), Tag.of((String)"operationType", (String)String.valueOf(event.getOperationType())), Tag.of((String)"attributeKey", (String)String.valueOf(event.getKey())));
    }
}

