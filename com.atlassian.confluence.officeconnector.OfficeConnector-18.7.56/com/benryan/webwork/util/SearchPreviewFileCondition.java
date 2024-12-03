/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.benryan.webwork.util;

import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class SearchPreviewFileCondition
implements Condition {
    public void init(Map map) throws PluginParseException {
    }

    public boolean shouldDisplay(Map map) {
        SearchResult result = (SearchResult)map.get("searchResult");
        if (result != null && result.getType().equals("attachment")) {
            String title = result.getDisplayTitle();
            return title.endsWith("docx") || title.endsWith(".doc") || title.endsWith(".xls") || title.endsWith("xlsx") || title.endsWith(".ppt") || title.endsWith(".pptx") || title.endsWith(".pdf");
        }
        return false;
    }
}

