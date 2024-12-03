/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

public class ResetHibernateIdRangeEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -3182848764760343068L;

    public ResetHibernateIdRangeEvent(Object src) {
        super(src);
    }
}

