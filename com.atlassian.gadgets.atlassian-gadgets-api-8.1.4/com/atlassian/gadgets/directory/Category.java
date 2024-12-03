/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.directory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Category {
    JIRA("Jira", new String[0]),
    CONFLUENCE("Confluence", new String[0]),
    FE_CRU("Fisheye/Crucible", new String[0]),
    CROWD("Crowd", new String[0]),
    CLOVER("Clover", new String[0]),
    BAMBOO("Bamboo", new String[0]),
    ADMIN("Admin", new String[0]),
    CHARTS("Charts", "Chart"),
    WALLBOARD("Wallboard", "WallBoard"),
    OTHER("Other", new String[0]);

    private final String name;
    private final List<String> aliases;
    private static final Map<String, Category> categoryNameMap;

    private Category(String name, String ... aliases) {
        this.name = name;
        this.aliases = Collections.unmodifiableList(Arrays.asList(aliases));
    }

    public static Category named(String categoryName) {
        Category category = categoryNameMap.get(categoryName.toLowerCase());
        return category != null ? category : OTHER;
    }

    public String getName() {
        return this.name;
    }

    static {
        HashMap<String, Category> map = new HashMap<String, Category>();
        for (Category category : Category.values()) {
            map.put(category.getName().toLowerCase(), category);
            for (String alias : category.aliases) {
                map.put(alias.toLowerCase(), category);
            }
        }
        categoryNameMap = Collections.unmodifiableMap(map);
    }
}

