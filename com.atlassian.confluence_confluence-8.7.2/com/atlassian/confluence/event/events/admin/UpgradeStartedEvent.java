/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;

public class UpgradeStartedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -6390782829352635789L;

    public UpgradeStartedEvent(Object src) {
        super(src);
    }
}

