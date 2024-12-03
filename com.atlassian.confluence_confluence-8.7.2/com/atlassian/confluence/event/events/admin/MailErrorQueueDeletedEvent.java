/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;

@Internal
public class MailErrorQueueDeletedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 1325380291933767231L;

    public MailErrorQueueDeletedEvent(Object src) {
        super(src);
    }
}

