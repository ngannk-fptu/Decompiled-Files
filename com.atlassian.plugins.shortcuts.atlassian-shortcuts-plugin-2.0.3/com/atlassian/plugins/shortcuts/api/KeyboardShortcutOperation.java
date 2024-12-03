/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.util.Assertions
 */
package com.atlassian.plugins.shortcuts.api;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.util.Assertions;

public class KeyboardShortcutOperation {
    private final OperationType type;
    private final String param;

    public KeyboardShortcutOperation(String type, String param) {
        try {
            this.type = OperationType.valueOf(type);
        }
        catch (IllegalArgumentException e) {
            throw new PluginParseException("Invalid operation type '" + type + "' provided");
        }
        this.param = (String)Assertions.notNull((String)"param", (Object)param);
    }

    public OperationType getType() {
        return this.type;
    }

    public String getParam() {
        return this.param;
    }

    public boolean equals(Object other) {
        if (other instanceof KeyboardShortcutOperation) {
            KeyboardShortcutOperation otherShortcut = (KeyboardShortcutOperation)other;
            return otherShortcut.getType() == this.getType() && otherShortcut.getParam().equals(this.getParam());
        }
        return false;
    }

    public int hashCode() {
        return 43 * (37 + this.getParam().hashCode()) + this.getType().hashCode();
    }

    public static enum OperationType {
        click,
        goTo,
        followLink,
        moveToAndFocus,
        moveToAndClick,
        moveToNextItem,
        moveToPrevItem,
        execute,
        evaluate;

    }
}

