/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.TermSetQuery
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction;

import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.SearchIndexActionWithNumberOfIds;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTasksAction
implements SearchIndexActionWithNumberOfIds {
    private static final Logger log = LoggerFactory.getLogger(DeleteTasksAction.class);
    private final Collection<Long> globalTaskIds;

    public DeleteTasksAction(Collection<Long> globalTaskIds) {
        this.globalTaskIds = ImmutableSet.copyOf(globalTaskIds);
    }

    public void accept(SearchIndexWriter searchIndexWriter) throws IOException {
        log.trace("Removing {} tasks", (Object)this.globalTaskIds.size());
        Set stringIds = this.globalTaskIds.stream().map(id -> Long.toString(id)).collect(Collectors.toSet());
        searchIndexWriter.delete((SearchQuery)new TermSetQuery("globalId", stringIds));
    }

    @Override
    public int getNumberOfIds() {
        return this.globalTaskIds.size();
    }
}

