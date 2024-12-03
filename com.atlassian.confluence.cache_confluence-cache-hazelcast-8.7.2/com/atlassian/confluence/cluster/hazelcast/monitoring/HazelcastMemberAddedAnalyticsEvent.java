/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.cluster.hazelcast.monitoring;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.diagnostics.hazelcast.member-added")
public class HazelcastMemberAddedAnalyticsEvent {
    private final int memberCount;

    public HazelcastMemberAddedAnalyticsEvent(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getMemberCount() {
        return this.memberCount;
    }
}

