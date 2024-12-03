/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.contentnames;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.contentnames.ResultTemplate;
import com.atlassian.confluence.search.contentnames.SearchResult;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ContentNameSearcher {
    default public Map<Category, List<SearchResult>> search(List<QueryToken> queryTokens) {
        return this.search(queryTokens, ResultTemplate.DEFAULT, (Set<Attachment.Type>)null, (String[])null);
    }

    default public Map<Category, List<SearchResult>> search(List<QueryToken> queryTokens, ResultTemplate grouping) {
        return this.search(queryTokens, grouping, (Set<Attachment.Type>)null, (String[])null);
    }

    default public Map<Category, List<SearchResult>> search(List<QueryToken> queryTokens, ResultTemplate grouping, String ... spaceKeys) {
        return this.search(queryTokens, grouping, (Set<Attachment.Type>)null, spaceKeys);
    }

    default public Map<Category, List<SearchResult>> search(List<QueryToken> queryTokens, ResultTemplate resultTemplate, Set<Attachment.Type> attachmentTypes, String ... spaceKeys) {
        return this.search(queryTokens, resultTemplate, attachmentTypes, false, 0, null, null, spaceKeys);
    }

    public Map<Category, List<SearchResult>> search(List<QueryToken> var1, ResultTemplate var2, Set<Attachment.Type> var3, boolean var4, int var5, Integer var6, Map<String, Object> var7, String ... var8);

    public List<SearchResult> searchNoCategorisation(List<QueryToken> var1, ResultTemplate var2, Set<Attachment.Type> var3, boolean var4, int var5, Integer var6, Map<String, Object> var7, String ... var8);
}

