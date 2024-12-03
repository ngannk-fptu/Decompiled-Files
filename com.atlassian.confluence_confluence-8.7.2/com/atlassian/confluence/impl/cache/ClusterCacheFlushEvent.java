/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

@Internal
public class ClusterCacheFlushEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -1168153821007042824L;

    public ClusterCacheFlushEvent(Object src) {
        super(src);
    }
}

