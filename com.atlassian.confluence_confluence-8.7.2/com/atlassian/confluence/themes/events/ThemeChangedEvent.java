/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes.events;

import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.themes.events.LookAndFeelEvent;

public class ThemeChangedEvent
extends LookAndFeelEvent
implements Updated {
    private static final long serialVersionUID = 7357773076498144489L;
    private final String oldThemeKey;
    private final String newThemeKey;

    public ThemeChangedEvent(Object src, String spaceKey, String oldThemeKey, String newThemeKey) {
        super(src, spaceKey);
        this.oldThemeKey = oldThemeKey;
        this.newThemeKey = newThemeKey;
    }

    public String getOldThemeKey() {
        return this.oldThemeKey;
    }

    public String getNewThemeKey() {
        return this.newThemeKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ThemeChangedEvent event = (ThemeChangedEvent)o;
        if (this.newThemeKey != null ? !this.newThemeKey.equals(event.newThemeKey) : event.newThemeKey != null) {
            return false;
        }
        return !(this.oldThemeKey != null ? !this.oldThemeKey.equals(event.oldThemeKey) : event.oldThemeKey != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.oldThemeKey != null ? this.oldThemeKey.hashCode() : 0);
        result = 31 * result + (this.newThemeKey != null ? this.newThemeKey.hashCode() : 0);
        return result;
    }
}

