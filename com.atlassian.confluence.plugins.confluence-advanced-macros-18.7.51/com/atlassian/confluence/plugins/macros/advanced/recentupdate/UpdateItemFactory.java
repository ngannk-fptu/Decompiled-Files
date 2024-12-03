/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchResult
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import com.atlassian.confluence.search.v2.SearchResult;

public interface UpdateItemFactory {
    public UpdateItem get(SearchResult var1);
}

