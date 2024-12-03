/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes.events;

import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.themes.events.LookAndFeelEvent;

public class StylesheetChangedEvent
extends LookAndFeelEvent
implements Updated {
    private static final long serialVersionUID = -4321296596536412365L;
    private final StylesheetChangeType changeType;

    public StylesheetChangedEvent(Object src, String spaceKey) {
        this(src, spaceKey, null);
    }

    public StylesheetChangedEvent(Object src) {
        this(src, null);
    }

    public StylesheetChangedEvent(Object src, String spaceKey, StylesheetChangeType changeType) {
        super(src, spaceKey);
        this.changeType = changeType;
    }

    public StylesheetChangeType getChangeType() {
        return this.changeType;
    }

    public static enum StylesheetChangeType {
        ADDED,
        REMOVED;

    }
}

