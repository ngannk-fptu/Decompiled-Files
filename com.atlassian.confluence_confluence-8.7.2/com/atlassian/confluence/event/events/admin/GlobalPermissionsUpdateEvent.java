/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Updated;

public class GlobalPermissionsUpdateEvent
extends ConfluenceEvent
implements Updated {
    private static final long serialVersionUID = 8940991945132949851L;

    public GlobalPermissionsUpdateEvent(Object src) {
        super(src);
    }
}

