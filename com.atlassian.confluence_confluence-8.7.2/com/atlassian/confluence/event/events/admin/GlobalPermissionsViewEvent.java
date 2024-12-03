/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;

public class GlobalPermissionsViewEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = -560611735941693857L;

    public GlobalPermissionsViewEvent(Object src) {
        super(src);
    }
}

