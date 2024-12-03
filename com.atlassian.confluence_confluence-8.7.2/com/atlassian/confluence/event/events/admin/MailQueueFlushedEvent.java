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
public class MailQueueFlushedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 7284205128234580119L;

    public MailQueueFlushedEvent(Object src) {
        super(src);
    }
}

