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

public class CustomFontInstalledEvent
extends ConfluenceEvent
implements Serializable,
ClusterEvent {
    private final String fontName;
    private final byte[] fontData;

    public CustomFontInstalledEvent(Object src, String fontName, byte[] fontData) {
        super(src);
        this.fontName = fontName;
        this.fontData = fontData;
    }

    public String getFontName() {
        return this.fontName;
    }

    public byte[] getFontData() {
        return this.fontData;
    }
}

