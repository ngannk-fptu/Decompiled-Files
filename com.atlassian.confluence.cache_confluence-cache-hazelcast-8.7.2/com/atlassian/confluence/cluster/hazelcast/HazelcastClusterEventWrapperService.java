/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapperService
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.hazelcast.HazelcastClusterEventWrapper;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapperService;
import com.atlassian.event.Event;

@Internal
public class HazelcastClusterEventWrapperService
implements ClusterEventWrapperService {
    public ConfluenceEvent wrap(Object src, Event event) {
        return new HazelcastClusterEventWrapper(src, event);
    }
}

