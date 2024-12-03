/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.ConfluenceEvent;

public class XStreamStateChangeEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 4504265035646720978L;

    public XStreamStateChangeEvent(Object src) {
        super(src);
    }
}

