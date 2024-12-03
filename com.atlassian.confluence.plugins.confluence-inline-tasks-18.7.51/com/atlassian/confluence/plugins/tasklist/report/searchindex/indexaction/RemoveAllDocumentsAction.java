/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchIndexAction
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.AllQuery
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction;

import com.atlassian.confluence.search.v2.SearchIndexAction;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveAllDocumentsAction
implements SearchIndexAction {
    private static final Logger log = LoggerFactory.getLogger(RemoveAllDocumentsAction.class);

    public void accept(SearchIndexWriter searchIndexWriter) throws IOException {
        log.debug("Removing all documents started.");
        searchIndexWriter.delete((SearchQuery)AllQuery.getInstance());
        log.debug("Removing all documents finished.");
    }
}

