/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.util.i18n.Message;

public class MacroPropertyPanelButton {
    private static final String SPACER_KEY = "__PROPERTY_PANEL_SPACER";
    public static final MacroPropertyPanelButton SPACER = new MacroPropertyPanelButton("__PROPERTY_PANEL_SPACER", null, null);
    private final String key;
    private final String label;
    private final String action;

    public MacroPropertyPanelButton(String key, String label, String action) {
        this.key = key;
        this.label = label;
        this.action = action;
    }

    public String getKey() {
        return this.key;
    }

    public Message getLabel() {
        return Message.getInstance(this.label);
    }

    public String getAction() {
        return this.action;
    }

    public boolean equals(Object obj) {
        return obj instanceof MacroPropertyPanelButton && this.key != null && this.key.equals(((MacroPropertyPanelButton)obj).getKey());
    }

    public int hashCode() {
        return this.key == null ? 13 : this.key.hashCode();
    }
}

