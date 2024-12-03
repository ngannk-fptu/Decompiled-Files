/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.shortcuts.api;

import com.atlassian.plugins.shortcuts.api.KeyboardShortcut;
import java.util.List;

public interface KeyboardShortcutManager {
    public static final String CONTEXT_GLOBAL = "global";

    public List<KeyboardShortcut> getAllShortcuts();

    public String getShortcutsHash();
}

