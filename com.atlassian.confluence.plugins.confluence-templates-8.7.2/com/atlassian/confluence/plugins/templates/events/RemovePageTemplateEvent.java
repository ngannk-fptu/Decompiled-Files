/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.plugins.templates.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;

@EventName(value="blueprint.page.template.remove")
public class RemovePageTemplateEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 5062381700150872048L;

    public RemovePageTemplateEvent(Object src) {
        super(src);
    }
}

