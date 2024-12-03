/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface SpaceCategories {
    public static final String SCOPE_PREFIX = "conf_";
    public static final String ALL = "conf_all";
    public static final String FAVOURITES = "conf_favorites";
    public static final String GLOBAL = "conf_global";
    public static final String PERSONAL = "conf_personal";
    public static final Set<String> ALL_CATEGORIES = new HashSet<String>(Arrays.asList("conf_all", "conf_favorites", "conf_global", "conf_personal"));
}

