/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.event.events.ConfluenceEvent;

public class SetupCompleteEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 4298423973306175648L;

    public SetupCompleteEvent(Object src) {
        super(src);
    }
}

