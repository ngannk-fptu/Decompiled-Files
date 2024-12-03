/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchConstants;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMentionsContentReindexer {
    private static final Logger log = LoggerFactory.getLogger(UserMentionsContentReindexer.class);
    private static final String THREAD_PREFIX = UserMentionsContentReindexer.class.getSimpleName();
    private final int entityCommitSize = Integer.getInteger("reindex.entity.commit.size", 100);
    private final ConfluenceIndexer indexer;
    private final BatchOperationManager batchOperationManager;
    private final SearchManager searchManager;
    private final SynchronizationManager synchronizationManager;
    private final ExecutorService executorService;

    public UserMentionsContentReindexer(ConfluenceIndexer indexer, BatchOperationManager batchOperationManager, SearchManager searchManager, SynchronizationManager synchronizationManager) {
        this.indexer = Objects.requireNonNull(indexer);
        this.batchOperationManager = Objects.requireNonNull(batchOperationManager);
        this.searchManager = Objects.requireNonNull(searchManager);
        this.synchronizationManager = Objects.requireNonNull(synchronizationManager);
        this.executorService = Executors.newFixedThreadPool(3, ThreadFactories.namedThreadFactory((String)THREAD_PREFIX));
    }

    public void reindex(ConfluenceUser user, String oldUsername) {
        if (oldUsername == null || user == null) {
            return;
        }
        this.synchronizationManager.runOnSuccessfulCommit(() -> this.executorService.submit(() -> {
            try {
                log.info("Reindexing content with mentions of user with key [{}]", (Object)user.getKey().getStringValue());
                this.doReindex(oldUsername);
            }
            catch (InvalidSearchException | SearchTokenExpiredException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @VisibleForTesting
    protected void doReindex(String username) throws InvalidSearchException, SearchTokenExpiredException {
        SearchResults mentionsContent = null;
        long startUpdateIndex = System.currentTimeMillis();
        int entitiesFound = 0;
        do {
            mentionsContent = mentionsContent == null ? this.searchByIndex(username, 0, SearchConstants.MAX_LIMIT) : this.searchByToken(mentionsContent.getNextPageSearch());
            List<Searchable> entities = this.searchManager.convertToEntities(mentionsContent, SearchManager.EntityVersionPolicy.INDEXED_VERSION);
            this.batchOperationManager.applyInBatches(entities, this.entityCommitSize, entities.size(), entity -> {
                this.indexer.reIndexExcludingDependents((Searchable)entity);
                return null;
            });
            entitiesFound += entities.size();
        } while (!mentionsContent.isLastPage());
        log.debug("Finished adding {} entities with removed user mentions to index queue in {}ms", (Object)entitiesFound, (Object)(System.currentTimeMillis() - startUpdateIndex));
    }

    private SearchResults searchByIndex(String username, int startIndex, int batchSize) throws InvalidSearchException {
        TermQuery mentionTermQuery = new TermQuery(SearchFieldNames.MENTION, username);
        return this.searchManager.search(new ContentSearch(mentionTermQuery, null, startIndex, batchSize));
    }

    private SearchResults searchByToken(SearchWithToken searchWithToken) throws SearchTokenExpiredException, InvalidSearchException {
        return this.searchManager.search(searchWithToken);
    }

    @PreDestroy
    public void cleanUp() {
        this.executorService.shutdownNow();
    }
}

