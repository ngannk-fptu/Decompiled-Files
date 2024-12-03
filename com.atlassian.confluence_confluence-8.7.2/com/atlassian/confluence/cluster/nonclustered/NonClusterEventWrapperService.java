/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.cluster.nonclustered;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapperService;
import com.atlassian.event.Event;

@Internal
public class NonClusterEventWrapperService
implements ClusterEventWrapperService {
    @Override
    public ConfluenceEvent wrap(Object src, Event event) {
        throw new UnsupportedOperationException("Tried to wrap an event in a ClusterEventWrapper on a non-clustered instance");
    }
}

