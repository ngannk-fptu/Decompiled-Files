/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.hipchat.emoticons;

import javax.annotation.Nonnull;

public class HipChatEmoticon {
    private final String shortcut;
    private final String url;

    public HipChatEmoticon(@Nonnull String shortcut) {
        this.shortcut = shortcut;
        this.url = null;
    }

    public HipChatEmoticon(@Nonnull String shortcut, String url) {
        this.shortcut = shortcut;
        this.url = url;
    }

    public String getShortcut() {
        return this.shortcut;
    }

    public String getUrl() {
        return this.url;
    }

    public String toString() {
        return "HipChatEmoticon{shortcut='" + this.shortcut + "'url='" + this.url + "'}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HipChatEmoticon that = (HipChatEmoticon)o;
        return this.shortcut.equals(that.shortcut);
    }

    public int hashCode() {
        return this.shortcut.hashCode();
    }
}

