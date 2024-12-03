/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.languages;

public class BrowserLanguageUtils {
    private static final String BROWSER_LANGUAGE_PROPERTY = "confluence.browser.language.enabled";
    private static Boolean enabled = null;
    public static final String LANGUAGE_COOKIE = "confluence-language";

    public static boolean isBrowserLanguageEnabled() {
        if (enabled == null) {
            String property = System.getProperty(BROWSER_LANGUAGE_PROPERTY);
            enabled = property != null ? Boolean.valueOf(Boolean.parseBoolean(property)) : Boolean.TRUE;
        }
        return enabled;
    }
}

