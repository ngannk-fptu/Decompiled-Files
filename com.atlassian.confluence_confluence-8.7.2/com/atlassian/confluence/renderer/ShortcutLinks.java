/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

import java.util.HashMap;
import java.util.Map;

public class ShortcutLinks {
    Map shortcutLinks = new HashMap();

    public void addShortcut(String key, String expandedValue) {
        this.shortcutLinks.put(key, expandedValue);
    }

    public void removeShortcut(String key) {
        this.shortcutLinks.remove(key);
    }

    public Map getShortcuts() {
        return this.shortcutLinks;
    }
}

