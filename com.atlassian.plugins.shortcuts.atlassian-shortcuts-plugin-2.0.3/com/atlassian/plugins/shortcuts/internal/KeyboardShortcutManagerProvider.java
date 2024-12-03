/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.plugins.shortcuts.internal;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcutManager;
import java.util.Map;

public class KeyboardShortcutManagerProvider
implements ContextProvider {
    private KeyboardShortcutManager keyboardShortcutManager;

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> stringObjectMap) {
        stringObjectMap.put("keyboardShortcutManager", this.keyboardShortcutManager);
        return stringObjectMap;
    }

    public void setKeyboardShortcutManager(KeyboardShortcutManager keyboardShortcutManager) {
        this.keyboardShortcutManager = keyboardShortcutManager;
    }
}

