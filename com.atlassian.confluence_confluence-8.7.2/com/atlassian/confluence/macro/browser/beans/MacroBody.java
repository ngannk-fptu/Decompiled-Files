/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.util.i18n.Message;

public class MacroBody {
    private final String pluginKey;
    private final String macroName;
    private final boolean hidden;
    private String bodyType;

    public MacroBody(String pluginKey, String macroName) {
        this(pluginKey, macroName, false);
    }

    public MacroBody(String pluginKey, String macroName, boolean hidden) {
        this.pluginKey = pluginKey;
        this.macroName = macroName;
        this.hidden = hidden;
    }

    public Message getLabel() {
        return Message.getInstance(this.pluginKey + "." + this.macroName + ".body.label");
    }

    public Message getDescription() {
        return Message.getInstance(this.pluginKey + "." + this.macroName + ".body.desc");
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public String getBodyType() {
        return this.bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }
}

