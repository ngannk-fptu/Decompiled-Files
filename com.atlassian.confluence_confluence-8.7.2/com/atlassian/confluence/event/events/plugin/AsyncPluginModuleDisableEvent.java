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
public class AsyncPluginModuleDisableEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 8974035889681404766L;
    private final String completeModuleKey;

    public AsyncPluginModuleDisableEvent(Object src, String completeModuleKey) {
        super(src);
        this.completeModuleKey = completeModuleKey;
    }

    public String getCompleteModuleKey() {
        return this.completeModuleKey;
    }
}

