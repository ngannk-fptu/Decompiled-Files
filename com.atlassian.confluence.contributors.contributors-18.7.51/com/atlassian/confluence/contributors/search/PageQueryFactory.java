/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchQuery
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.search.v2.SearchQuery;

interface PageQueryFactory {
    public SearchQuery createPageQuery(MacroParameterModel var1);
}

