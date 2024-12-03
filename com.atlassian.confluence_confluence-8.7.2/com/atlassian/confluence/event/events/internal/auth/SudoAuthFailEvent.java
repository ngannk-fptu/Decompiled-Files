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
public class SudoAuthFailEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 6154098944500319093L;

    public SudoAuthFailEvent(Object src) {
        super(src);
    }
}

