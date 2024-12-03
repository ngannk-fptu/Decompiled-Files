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
public class AsyncPluginModuleEnableEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 4662318089698586395L;
    private final String completeModuleKey;

    public AsyncPluginModuleEnableEvent(Object src, String completeModuleKey) {
        super(src);
        this.completeModuleKey = completeModuleKey;
    }

    public String getCompleteModuleKey() {
        return this.completeModuleKey;
    }
}

