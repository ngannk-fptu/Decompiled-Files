/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class NewcodeSettings
implements Serializable {
    public static final String DEFAULT_THEME_VALUE = "Confluence";
    public static final String DEFAULT_LANGUAGE_VALUE = "Java";
    public static final String DEFAULT_THEME = "defaultTheme";
    public static final String DEFAULT_LANGUAGE = "defaultLanguage";
    private static final long serialVersionUID = 1L;
    private String defaultTheme;
    private String defaultLanguage;

    public String getDefaultTheme() {
        return this.defaultTheme;
    }

    public void setDefaultTheme(String defaultTheme) {
        this.defaultTheme = defaultTheme;
    }

    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public Map<String, Object> settingsToMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(DEFAULT_THEME, this.getDefaultTheme());
        map.put(DEFAULT_LANGUAGE, this.getDefaultLanguage());
        return map;
    }

    public void mapToSettings(Map<String, Object> map) {
        this.setDefaultTheme((String)map.get(DEFAULT_THEME));
        this.setDefaultLanguage((String)map.get(DEFAULT_LANGUAGE));
    }
}

