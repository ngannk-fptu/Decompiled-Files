/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.internal.auth;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;

@Internal
public class SudoLogoutEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 7846514566314105658L;

    public SudoLogoutEvent(Object src) {
        super(src);
    }
}

