/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchIndexAction
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction;

import com.atlassian.confluence.search.v2.SearchIndexAction;

public interface SearchIndexActionWithNumberOfIds
extends SearchIndexAction {
    public int getNumberOfIds();
}

