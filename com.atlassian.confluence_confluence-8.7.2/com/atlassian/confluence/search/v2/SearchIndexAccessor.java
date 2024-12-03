/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.BatchUpdateAction;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.ScannedDocument;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import com.atlassian.confluence.search.v2.SearchIndexAction;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface SearchIndexAccessor {
    public SearchResults search(ISearch var1, Set<String> var2) throws InvalidSearchException;

    public long scan(SearchQuery var1, Set<String> var2, Consumer<Map<String, String[]>> var3);

    public long scan(SearchQuery var1, Set<String> var2, Consumer<ScannedDocument> var3, float var4);

    public void execute(SearchIndexAction var1) throws SearchIndexAccessException;

    public int numDocs() throws SearchIndexAccessException;

    public void withBatchUpdate(BatchUpdateAction var1);

    public void snapshot(File var1) throws SearchIndexAccessException;

    public void reset(Runnable var1);

    public FieldMappings getFieldMappings();
}

