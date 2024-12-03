/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.template;

import com.atlassian.confluence.event.events.ConfluenceEvent;

public abstract class TemplateEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -8891375577617285480L;

    public TemplateEvent(Object src) {
        super(src);
    }
}

