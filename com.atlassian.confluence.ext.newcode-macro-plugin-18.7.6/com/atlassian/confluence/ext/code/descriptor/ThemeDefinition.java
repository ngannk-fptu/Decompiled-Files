/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.descriptor;

import java.util.Map;

public final class ThemeDefinition {
    private String location;
    private String webResourceId;
    private Map<String, String> panelLookAndFeel;

    public ThemeDefinition(String location, String webResourceId, Map<String, String> panelLookAndFeel) {
        this.location = location;
        this.webResourceId = webResourceId;
        this.panelLookAndFeel = panelLookAndFeel;
    }

    public String getLocation() {
        return this.location;
    }

    public String getWebResourceId() {
        return this.webResourceId;
    }

    public Map<String, String> getPanelLookAndFeel() {
        return this.panelLookAndFeel;
    }
}

