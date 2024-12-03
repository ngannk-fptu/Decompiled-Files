/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;

@EventName(value="confluence.no.use.permission")
public class NoConfluencePermissionEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 7575602976706744224L;

    public NoConfluencePermissionEvent(Object src) {
        super(src);
    }
}

