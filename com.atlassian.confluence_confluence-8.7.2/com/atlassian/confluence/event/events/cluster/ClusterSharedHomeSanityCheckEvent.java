/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

@Internal
public class ClusterSharedHomeSanityCheckEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 1L;

    public ClusterSharedHomeSanityCheckEvent(Object src) {
        super(src);
    }
}

