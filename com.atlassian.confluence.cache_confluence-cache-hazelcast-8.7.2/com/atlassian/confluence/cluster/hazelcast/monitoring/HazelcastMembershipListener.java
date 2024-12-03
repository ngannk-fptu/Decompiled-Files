/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertRequest$Builder
 *  com.atlassian.diagnostics.ComponentMonitor
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.ImmutableMap
 *  com.hazelcast.core.InitialMembershipEvent
 *  com.hazelcast.core.InitialMembershipListener
 *  com.hazelcast.core.Member
 *  com.hazelcast.core.MemberAttributeEvent
 *  com.hazelcast.core.MembershipEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast.monitoring;

import com.atlassian.confluence.cluster.hazelcast.monitoring.HazelcastMemberAddedAnalyticsEvent;
import com.atlassian.confluence.cluster.hazelcast.monitoring.HazelcastMemberRemovedAnalyticsEvent;
import com.atlassian.diagnostics.AlertRequest;
import com.atlassian.diagnostics.ComponentMonitor;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.ImmutableMap;
import com.hazelcast.core.InitialMembershipEvent;
import com.hazelcast.core.InitialMembershipListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import java.time.Instant;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HazelcastMembershipListener
implements InitialMembershipListener {
    private static final Logger log = LoggerFactory.getLogger(HazelcastMembershipListener.class);
    private final ComponentMonitor monitor;
    private final EventPublisher eventPublisher;

    HazelcastMembershipListener(ComponentMonitor monitor, EventPublisher eventPublisher) {
        this.monitor = Objects.requireNonNull(monitor);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void memberAdded(MembershipEvent event) {
        log.info("memberAdded: {}", (Object)event.getMember());
        for (Member member : event.getMembers()) {
            log.info("memberAdded: cluster contains {}", (Object)member.toString());
        }
        this.alertMembershipChange(1001, event);
        this.eventPublisher.publish((Object)new HazelcastMemberAddedAnalyticsEvent(event.getMembers().size()));
    }

    public void memberRemoved(MembershipEvent event) {
        log.info("memberRemoved: {}", (Object)event.getMember());
        for (Member member : event.getMembers()) {
            log.info("memberRemoved: cluster contains {}", (Object)member.toString());
        }
        this.alertMembershipChange(1002, event);
        this.eventPublisher.publish((Object)new HazelcastMemberRemovedAnalyticsEvent(event.getMembers().size()));
    }

    public void memberAttributeChanged(MemberAttributeEvent event) {
        log.info("memberAttributeChanged: member {}, key {}, value {}", new Object[]{event.getMember(), event.getKey(), event.getValue()});
    }

    public void init(InitialMembershipEvent event) {
        log.info("init: cluster {}", (Object)event.getCluster());
        for (Member member : event.getMembers()) {
            log.info("init: cluster contains {}", (Object)member.toString());
        }
    }

    private void alertMembershipChange(int issueId, MembershipEvent event) {
        if (this.monitor != null && this.monitor.isEnabled()) {
            this.monitor.getIssue(issueId).ifPresent(issue -> this.monitor.alert(new AlertRequest.Builder(issue).timestamp(Instant.now()).details(() -> ImmutableMap.of((Object)"member", (Object)event.getMember().toString())).build()));
        }
    }
}

