/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes.events;

import com.atlassian.confluence.themes.events.LookAndFeelEvent;

public class SiteLogoChangedEvent
extends LookAndFeelEvent {
    private static final long serialVersionUID = 1767170644403976839L;

    public SiteLogoChangedEvent(Object src, String spaceKey) {
        super(src, spaceKey);
    }
}

