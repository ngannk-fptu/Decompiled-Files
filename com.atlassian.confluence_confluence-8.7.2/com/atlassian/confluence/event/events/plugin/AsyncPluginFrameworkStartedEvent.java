/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class AsyncPluginFrameworkStartedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -2215094416770703184L;

    public AsyncPluginFrameworkStartedEvent(Object source) {
        super(source);
    }
}

