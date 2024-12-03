/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

import java.io.Serializable;

public class ShortcutLinkConfig
implements Serializable {
    private String expandedValue;
    private String defaultAlias;

    public ShortcutLinkConfig() {
    }

    public ShortcutLinkConfig(String expandedValue, String defaultAlias) {
        this.expandedValue = expandedValue;
        this.defaultAlias = defaultAlias;
    }

    public String getExpandedValue() {
        return this.expandedValue;
    }

    public void setExpandedValue(String expandedValue) {
        this.expandedValue = expandedValue;
    }

    public String getDefaultAlias() {
        return this.defaultAlias;
    }

    public void setDefaultAlias(String defaultAlias) {
        this.defaultAlias = defaultAlias;
    }
}

