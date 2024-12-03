/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.types.Updated
 *  com.atlassian.confluence.themes.events.LookAndFeelEvent
 */
package com.atlassian.confluence.plugins.lookandfeel.events;

import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.themes.events.LookAndFeelEvent;

@Deprecated
public class SiteLogoChangedEvent
extends LookAndFeelEvent
implements Updated {
    private static final long serialVersionUID = -8552978190819396992L;

    public SiteLogoChangedEvent(Object src, String spaceKey) {
        super(src, spaceKey);
    }
}

