/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import java.io.Serializable;

public class CustomFontRemovedEvent
extends ConfluenceEvent
implements Serializable,
ClusterEvent {
    private final String fontName;

    public CustomFontRemovedEvent(Object src, String fontName) {
        super(src);
        this.fontName = fontName;
    }

    public String getFontName() {
        return this.fontName;
    }
}

