/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.TermSetQuery
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction;

import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.SearchIndexActionWithNumberOfIds;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletePageWithDescendantsAction
implements SearchIndexActionWithNumberOfIds {
    private static final Logger log = LoggerFactory.getLogger(DeletePageWithDescendantsAction.class);
    private final Collection<Long> ancestorIds;

    public DeletePageWithDescendantsAction(Collection<Long> ancestorIds) {
        this.ancestorIds = ancestorIds;
    }

    public void accept(SearchIndexWriter searchIndexWriter) throws IOException {
        log.trace("Removing tasks from {} ancestors", (Object)this.ancestorIds.size());
        Set stringIds = this.ancestorIds.stream().map(id -> Long.toString(id)).collect(Collectors.toSet());
        searchIndexWriter.delete((SearchQuery)new TermSetQuery("ancestorIds", stringIds));
        searchIndexWriter.delete((SearchQuery)new TermSetQuery("contentId", stringIds));
    }

    @Override
    public int getNumberOfIds() {
        return this.ancestorIds.size();
    }
}

