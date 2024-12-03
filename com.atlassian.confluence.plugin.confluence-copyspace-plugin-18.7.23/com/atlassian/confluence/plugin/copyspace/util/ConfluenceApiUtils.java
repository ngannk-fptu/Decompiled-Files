/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.DefaultSearch
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 */
package com.atlassian.confluence.plugin.copyspace.util;

import com.atlassian.confluence.search.v2.DefaultSearch;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import java.lang.reflect.Constructor;
import java.util.EnumSet;

public class ConfluenceApiUtils {
    private ConfluenceApiUtils() {
    }

    public static boolean isLuceneMigrated() {
        Constructor<?>[] constructors;
        Class<?> searchFilterClass;
        try {
            searchFilterClass = Class.forName("com.atlassian.confluence.search.v2.SearchFilter");
        }
        catch (ClassNotFoundException e) {
            return true;
        }
        boolean isMigrated = true;
        for (Constructor<?> c : constructors = DefaultSearch.class.getConstructors()) {
            Class<?>[] constructorParameterTypes = c.getParameterTypes();
            if (constructorParameterTypes.length != 6 || !EnumSet.class.equals(constructorParameterTypes[0]) || !SearchQuery.class.equals(constructorParameterTypes[1]) || !SearchSort.class.equals(constructorParameterTypes[2]) || !searchFilterClass.equals(constructorParameterTypes[3])) continue;
            return false;
        }
        return isMigrated;
    }
}

