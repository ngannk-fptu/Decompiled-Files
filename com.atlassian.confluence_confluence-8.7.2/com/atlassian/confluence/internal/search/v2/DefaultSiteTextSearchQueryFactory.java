/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.queryparser.flexible.core.QueryNodeException
 *  org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.exact.ExactAnalyzer;
import com.atlassian.confluence.internal.search.v2.SiteTextSearchSpanQueryBuilder;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryParserFactory;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryUtil;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.mapping.MappingDeconflictDarkFeature;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SiteSearchContainsSearchSyntaxEvent;
import com.atlassian.confluence.search.v2.SiteTextSearchQueryFactory;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.PhraseQuery;
import com.atlassian.confluence.search.v2.query.QueryStringQuery;
import com.atlassian.confluence.search.v2.query.SiteTextSearchQuery;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSiteTextSearchQueryFactory
implements SiteTextSearchQueryFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultSiteTextSearchQueryFactory.class);
    @VisibleForTesting
    public static final String SEARCH_IMPROVEMENTS_EXACT_DARK_FEATURE_KEY = "confluence.search.improvements.exact";
    public static final float TITLE_FIELD_BOOST = 2.1f;
    public static final float SPAN_QUERY_BOOST = 4.0f;
    private final LuceneQueryParserFactory luceneQueryParserFactory;
    private final Analyzer exactSearchAnalyzer = new ExactAnalyzer(LuceneConstants.LUCENE_VERSION);
    private final LuceneAnalyzerFactory luceneAnalyzerFactory;
    private final SiteTextSearchSpanQueryBuilder spanQueryBuilder;
    private final EventPublisher eventPublisher;
    private final DarkFeatureManager darkFeatureManager;

    public DefaultSiteTextSearchQueryFactory(LuceneAnalyzerFactory luceneAnalyzerFactory, LuceneQueryParserFactory luceneQueryParserFactory, SiteTextSearchSpanQueryBuilder spanQueryBuilder, EventPublisher eventPublisher, DarkFeatureManager darkFeatureManager) {
        this.luceneAnalyzerFactory = luceneAnalyzerFactory;
        this.luceneQueryParserFactory = luceneQueryParserFactory;
        this.spanQueryBuilder = spanQueryBuilder;
        this.eventPublisher = eventPublisher;
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    public SearchQuery getQuery(String queryString) {
        if (Strings.isNullOrEmpty((String)queryString) || "*".equals(StringUtils.trim((String)queryString))) {
            return AllQuery.getInstance();
        }
        BooleanQueryBuilder<SearchQuery> boolQuery = BooleanQuery.builder().disableCoord(true);
        Analyzer analyzer = this.luceneAnalyzerFactory.createAnalyzer();
        SearchQuery queryStringQuery = this.getQueryParserQuery(queryString);
        if (!SiteTextSearchQuery.isQueryStringSyntax(queryString)) {
            PhraseQuery bodyPhraseQuery;
            PhraseQuery titlePhraseQuery;
            Optional<SearchQuery> titleSpanQuery = this.spanQueryBuilder.getQuery("title", queryString, analyzer, 2.1f);
            if (titleSpanQuery.isPresent()) {
                boolQuery.addShould(BooleanQuery.builder().disableCoord(true).addShould(titleSpanQuery.get()).addShould(this.spanQueryBuilder.getQuery("contentBody", queryString, analyzer, 1.0f).get()).boost(4.0f).build());
            }
            if ((titlePhraseQuery = this.createPhraseQuery("title", queryString, analyzer, true, true, 2.1f)) != null) {
                boolQuery.addShould(titlePhraseQuery);
            }
            if ((bodyPhraseQuery = this.createPhraseQuery("contentBody", queryString, analyzer, true, true, 1.0f)) != null) {
                boolQuery.addShould(bodyPhraseQuery);
            }
        } else {
            this.eventPublisher.publish((Object)new SiteSearchContainsSearchSyntaxEvent());
            if (this.darkFeatureManager.isEnabledForAllUsers(SEARCH_IMPROVEMENTS_EXACT_DARK_FEATURE_KEY).orElse(false).booleanValue() && SiteTextSearchQuery.isExactSearchSyntax(queryString)) {
                return this.getExactQuotesQuery(queryString);
            }
        }
        if (queryStringQuery != null) {
            boolQuery.addShould(queryStringQuery);
        }
        return boolQuery.build();
    }

    private SearchQuery getExactQuotesQuery(String queryString) {
        String extractedQueryString = queryString.substring(1, queryString.length() - 1);
        PhraseQuery exactTitlePhraseQuery = this.createPhraseQuery(SearchFieldNames.EXACT_TITLE, extractedQueryString, this.exactSearchAnalyzer, true, false, 2.1f);
        PhraseQuery exactBodyPhraseQuery = this.createPhraseQuery(SearchFieldNames.EXACT_CONTENT_BODY, extractedQueryString, this.exactSearchAnalyzer, true, false, 1.0f);
        BooleanQueryBuilder<SearchQuery> booleanQuery = BooleanQuery.builder().disableCoord(true).addShould(exactTitlePhraseQuery).addShould(exactBodyPhraseQuery);
        if (MappingDeconflictDarkFeature.create(this.darkFeatureManager).isEnabled()) {
            PhraseQuery exactFilenameTitlePhraseQuery = this.createPhraseQuery(SearchFieldMappings.EXACT_FILENAME.getName(), extractedQueryString, this.exactSearchAnalyzer, true, false, 2.1f);
            booleanQuery.addShould(exactFilenameTitlePhraseQuery);
        }
        return booleanQuery.build();
    }

    private SearchQuery getQueryParserQuery(String queryString) {
        StandardQueryParser queryParser = this.luceneQueryParserFactory.createQueryParser();
        try {
            queryParser.getSyntaxParser().parse((CharSequence)queryString, null);
        }
        catch (QueryNodeException e) {
            log.debug("Error parsing query: ", (Throwable)e);
            return null;
        }
        return new QueryStringQuery(Arrays.asList("title", "contentBody", "content-name-unstemmed"), queryString, BooleanOperator.OR, Map.of("title", Float.valueOf(2.1f)));
    }

    private PhraseQuery createPhraseQuery(String field, String queryString, Analyzer analyzer, boolean exact, boolean shouldDiscardSingularToken, float boost) {
        List<String> tokens = LuceneQueryUtil.tokenize(analyzer, field, queryString);
        if (shouldDiscardSingularToken && tokens.size() == 1) {
            return null;
        }
        int slop = 0;
        if (!exact) {
            slop = tokens.size() + 1;
        }
        return new PhraseQuery(field, queryString, slop, AnalyzerDescriptorProvider.EMPTY, boost);
    }
}

