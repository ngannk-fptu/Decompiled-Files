/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.collections4.ListUtils
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.ConstantScoreQuery
 *  org.apache.lucene.search.MatchAllDocsQuery
 *  org.apache.lucene.search.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.ListUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanQueryMapper
implements LuceneQueryMapper<com.atlassian.confluence.search.v2.query.BooleanQuery> {
    private static final Logger logger = LoggerFactory.getLogger(BooleanQueryMapper.class);
    public static final String SKIP_BATCHING_BOOLEAN_QUERY = "confluence.lucene.booleanQuery.skip.batching";
    public static final String BOOLEAN_QUERY_BATCHING_NUMBER = "Confluence.Lucene.BooleanQuery.Batching";
    private LuceneSearchMapper searchMapper;
    private int maxClauseCount = BooleanQuery.getMaxClauseCount();
    private final DarkFeatureManager darkFeatureManager;

    public BooleanQueryMapper() {
        this.darkFeatureManager = ContainerManager.isContainerSetup() ? (DarkFeatureManager)ContainerManager.getComponent((String)"salDarkFeatureManager") : null;
    }

    @VisibleForTesting
    public BooleanQueryMapper(LuceneSearchMapper searchMapper) {
        this.searchMapper = searchMapper;
        this.darkFeatureManager = null;
    }

    @VisibleForTesting
    public BooleanQueryMapper(LuceneSearchMapper searchMapper, DarkFeatureManager darkFeatureManager) {
        this.searchMapper = searchMapper;
        this.darkFeatureManager = darkFeatureManager;
    }

    protected int getMaxClauseCount() {
        return this.maxClauseCount;
    }

    @VisibleForTesting
    public void setMaxClauseCount(int maxClauseCount) {
        this.maxClauseCount = maxClauseCount;
    }

    private boolean shouldSkipBatchingQuery() {
        if (this.darkFeatureManager != null) {
            return this.darkFeatureManager.isEnabledForAllUsers(SKIP_BATCHING_BOOLEAN_QUERY).orElse(false);
        }
        logger.warn("Could not obtain DarkFeatureManager instance. Will not skip batching");
        return false;
    }

    private int getBooleanQueryBatchingNumber() {
        return Math.min(Integer.getInteger(BOOLEAN_QUERY_BATCHING_NUMBER, 500), BooleanQuery.getMaxClauseCount());
    }

    @Override
    public Query convertToLuceneQuery(com.atlassian.confluence.search.v2.query.BooleanQuery boolQuery) {
        BooleanQuery luceneQuery = new BooleanQuery(boolQuery.isCoordDisabled());
        this.addSubQueries(luceneQuery, boolQuery.getMustQueries(), BooleanClause.Occur.MUST, true);
        this.addSubQueries(luceneQuery, boolQuery.getFilters(), BooleanClause.Occur.MUST, false);
        this.addSubQueries(luceneQuery, boolQuery.getShouldQueries(), BooleanClause.Occur.SHOULD, true);
        this.addSubQueries(luceneQuery, boolQuery.getMustNotQueries(), BooleanClause.Occur.MUST_NOT, true);
        if (!luceneQuery.clauses().isEmpty()) {
            if (boolQuery.getMustQueries().isEmpty() && boolQuery.getShouldQueries().isEmpty() && boolQuery.getFilters().isEmpty()) {
                luceneQuery.add((Query)new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
            }
            luceneQuery.setBoost(boolQuery.getBoost());
        }
        logger.debug("Output BooleanQuery: {}", (Object)luceneQuery);
        return luceneQuery;
    }

    private void addSubQueries(BooleanQuery luceneQuery, Set<SearchQuery> queries, BooleanClause.Occur operator, boolean shouldScore) {
        int querySize = queries.size();
        if (!this.shouldSkipBatchingQuery() && querySize >= this.getMaxClauseCount()) {
            logger.debug("Apply batching for BooleanQueryMapper as number of clause is too big {}", (Object)querySize);
            this.addSubQueriesWithBatching(luceneQuery, queries, operator, shouldScore);
            return;
        }
        for (SearchQuery query : queries) {
            this.toLuceneQuery(shouldScore, query).ifPresent(mappedQuery -> luceneQuery.add(mappedQuery, operator));
        }
    }

    private void addSubQueriesWithBatching(BooleanQuery outerBooleanQuery, Set<SearchQuery> queries, BooleanClause.Occur operator, boolean shouldScore) {
        List batches = ListUtils.partition(new ArrayList<SearchQuery>(queries), (int)this.getBooleanQueryBatchingNumber());
        logger.debug("There are {} batches for BooleanQuery", (Object)batches.size());
        for (List currentBatch : batches) {
            BooleanQuery innerBooleanQuery = new BooleanQuery();
            if (BooleanClause.Occur.MUST_NOT == operator) {
                innerBooleanQuery.add(new BooleanClause((Query)new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD));
            }
            for (SearchQuery searchQuery : currentBatch) {
                this.toLuceneQuery(shouldScore, searchQuery).ifPresent(query -> innerBooleanQuery.add(query, operator));
            }
            outerBooleanQuery.add((Query)innerBooleanQuery, operator == BooleanClause.Occur.MUST_NOT ? BooleanClause.Occur.MUST : operator);
        }
    }

    private Optional<Query> toLuceneQuery(boolean shouldScore, SearchQuery searchQuery) {
        Query mappedQuery = this.searchMapper.convertToLuceneQuery(searchQuery);
        if (mappedQuery != null && !shouldScore) {
            mappedQuery = new ConstantScoreQuery(mappedQuery);
        }
        return Optional.ofNullable(mappedQuery);
    }

    public void setSearchMapper(LuceneSearchMapper searchMapper) {
        this.searchMapper = searchMapper;
    }
}

