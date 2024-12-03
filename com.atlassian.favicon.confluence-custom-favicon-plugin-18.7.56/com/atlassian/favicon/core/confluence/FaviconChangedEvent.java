/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.types.Updated
 *  com.atlassian.confluence.themes.events.LookAndFeelEvent
 */
package com.atlassian.favicon.core.confluence;

import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.themes.events.LookAndFeelEvent;

public class FaviconChangedEvent
extends LookAndFeelEvent
implements Updated {
    public FaviconChangedEvent(Object src) {
        super(src, null);
    }
}

