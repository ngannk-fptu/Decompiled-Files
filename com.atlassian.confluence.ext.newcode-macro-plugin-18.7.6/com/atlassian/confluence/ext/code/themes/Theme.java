/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.themes;

import java.util.Map;

public interface Theme {
    public String getName();

    public String getStyleSheetUrl();

    public boolean isBuiltIn();

    public String getWebResource();

    public Map<String, String> getDefaultLayout();
}

