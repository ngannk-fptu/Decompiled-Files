/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ConfigurationEvent;

public class PluginMacroRegisteredEvent
extends ConfigurationEvent {
    private static final long serialVersionUID = -39247368212800956L;
    private final String macroName;

    public PluginMacroRegisteredEvent(String macroName, Object src) {
        super(src);
        this.macroName = macroName;
    }

    public String getMacroName() {
        return this.macroName;
    }
}

