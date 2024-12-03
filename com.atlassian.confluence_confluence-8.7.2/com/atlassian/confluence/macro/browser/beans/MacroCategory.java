/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.util.i18n.Message;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MacroCategory {
    FORMATTING("formatting"),
    CONFLUENCE_CONTENT("confluence-content"),
    MEDIA("media"),
    VISUALS("visuals"),
    NAVIGATION("navigation"),
    EXTERNAL_CONTENT("external-content"),
    COMMUNICATION("communication"),
    REPORTING("reporting"),
    ADMIN("admin"),
    DEVELOPMENT("development"),
    HIDDEN("hidden-macros");

    private static final String MACRO_BROWSER_CATEGORY_PREFIX = "macro.browser.category.";
    private final String name;
    private final Message displayName;
    private static final Map<String, MacroCategory> lookup;

    private MacroCategory(String name) {
        this.name = name;
        this.displayName = Message.getInstance(MACRO_BROWSER_CATEGORY_PREFIX + name);
    }

    public String getName() {
        return this.name;
    }

    public Message getDisplayName() {
        return this.displayName;
    }

    public static MacroCategory get(String name) {
        return lookup.get(name);
    }

    public String toString() {
        return this.name;
    }

    static {
        lookup = new HashMap<String, MacroCategory>(13);
        for (MacroCategory type : EnumSet.allOf(MacroCategory.class)) {
            lookup.put(type.getName(), type);
        }
    }
}

