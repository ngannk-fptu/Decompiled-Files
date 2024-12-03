/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.EnumUtils
 */
package com.atlassian.confluence.search.v2.lucene;

import com.atlassian.confluence.search.v2.Index;
import org.apache.commons.lang3.EnumUtils;

@Deprecated(since="8.7.0", forRemoval=true)
public enum SearchIndex {
    CONTENT,
    CHANGE,
    CUSTOM;

    private static final long serialVersionUID = 2737527933237666517L;

    public static SearchIndex fromIndex(Index index) {
        return index.getType() == Index.Type.SYSTEM ? (SearchIndex)EnumUtils.getEnumIgnoreCase(SearchIndex.class, (String)index.getName()) : CUSTOM;
    }
}

