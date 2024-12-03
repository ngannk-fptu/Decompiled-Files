/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.themes.impl;

import com.atlassian.confluence.ext.code.themes.Theme;
import java.util.Map;

public final class BuiltinTheme
implements Theme {
    private String name;
    private String styleSheetUrl;
    private String webResource;
    private Map<String, String> defaultLayout;

    public BuiltinTheme(String name, String styleSheetUrl, Map<String, String> defaultLayout) {
        this.name = name;
        this.styleSheetUrl = styleSheetUrl;
        this.defaultLayout = defaultLayout;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getStyleSheetUrl() {
        return this.styleSheetUrl;
    }

    @Override
    public boolean isBuiltIn() {
        return true;
    }

    @Override
    public String getWebResource() {
        return this.webResource;
    }

    public void setWebResource(String webResource) {
        this.webResource = webResource;
    }

    @Override
    public Map<String, String> getDefaultLayout() {
        return this.defaultLayout;
    }
}

