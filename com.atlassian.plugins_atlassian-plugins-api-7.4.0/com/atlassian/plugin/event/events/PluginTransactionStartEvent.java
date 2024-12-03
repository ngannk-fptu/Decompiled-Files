/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugin.event.events;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class PluginTransactionStartEvent {
    private final long threadId = Thread.currentThread().getId();

    public long threadId() {
        return this.threadId;
    }
}

