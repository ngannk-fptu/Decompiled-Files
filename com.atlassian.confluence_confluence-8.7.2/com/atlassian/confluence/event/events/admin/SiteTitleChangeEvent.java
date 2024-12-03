/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;

@EventName(value="confluence.admin.change.site.title")
public class SiteTitleChangeEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -6959167315339186124L;

    public SiteTitleChangeEvent(Object src) {
        super(src);
    }
}

