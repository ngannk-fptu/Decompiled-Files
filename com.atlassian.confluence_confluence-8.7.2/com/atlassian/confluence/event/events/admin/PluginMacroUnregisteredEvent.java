/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ConfigurationEvent;

public class PluginMacroUnregisteredEvent
extends ConfigurationEvent {
    private static final long serialVersionUID = -8889849415790016830L;
    private final String macroName;

    public PluginMacroUnregisteredEvent(String macroName, Object src) {
        super(src);
        this.macroName = macroName;
    }

    public String getMacroName() {
        return this.macroName;
    }
}

