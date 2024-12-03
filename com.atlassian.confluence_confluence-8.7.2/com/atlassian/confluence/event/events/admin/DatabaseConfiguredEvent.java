/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;

@Deprecated(forRemoval=true)
public class DatabaseConfiguredEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 7094198084139166394L;

    public DatabaseConfiguredEvent(Object src) {
        super(src);
    }
}

