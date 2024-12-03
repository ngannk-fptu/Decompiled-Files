/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes.events;

import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.themes.events.LookAndFeelEvent;

public class ColourSchemeChangedEvent
extends LookAndFeelEvent
implements Updated {
    private static final long serialVersionUID = 136536819113981968L;
    private String oldColourSchemeType;
    private String newColourSchemeType;
    private boolean isCustomSchemeEdit;

    public ColourSchemeChangedEvent(Object src, String spaceKey) {
        this(src, null, null, spaceKey);
    }

    public ColourSchemeChangedEvent(Object src) {
        this(src, null);
    }

    public ColourSchemeChangedEvent(Object src, String oldColourSchemeType, String newColourSchemeType, String spaceKey) {
        super(src, spaceKey);
        this.oldColourSchemeType = oldColourSchemeType;
        this.newColourSchemeType = newColourSchemeType;
    }

    public ColourSchemeChangedEvent(Object src, Boolean isCustomSchemeEdit, String spaceKey) {
        super(src, spaceKey);
        this.isCustomSchemeEdit = isCustomSchemeEdit;
    }

    public String getNewColourSchemeType() {
        return this.newColourSchemeType;
    }

    public String getOldColourSchemeType() {
        return this.oldColourSchemeType;
    }

    public boolean isCustomSchemeEdit() {
        return this.isCustomSchemeEdit;
    }
}

