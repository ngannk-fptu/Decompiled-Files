/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.io.IOException;

public interface SearchIndexWriter {
    public void add(AtlassianDocument var1) throws IOException;

    public void delete(SearchQuery var1) throws IOException;

    public void deleteAll() throws IOException;

    public void preOptimize();

    public void postOptimize();
}

