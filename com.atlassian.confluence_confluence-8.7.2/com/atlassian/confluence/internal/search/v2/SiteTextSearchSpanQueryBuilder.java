/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.apache.lucene.analysis.Analyzer
 */
package com.atlassian.confluence.internal.search.v2;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryUtil;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.SpanNearQuery;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;

public class SiteTextSearchSpanQueryBuilder {
    private static final int TERMS_LIMIT = 6;
    public static final int BASE_SLOP = 5;
    public static final int SHINGLE_SIZE = 2;

    public Optional<SearchQuery> getQuery(String field, String queryString, Analyzer analyzer, float boost) {
        LinkedHashSet<String> tokens = new LinkedHashSet<String>(LuceneQueryUtil.tokenize(analyzer, field, queryString));
        if (tokens.size() <= 1 || tokens.size() > 6) {
            return Optional.empty();
        }
        if (tokens.size() == 2) {
            return Optional.of(new SpanNearQuery(field, new ArrayList<String>(tokens), 5, false, boost));
        }
        BooleanQueryBuilder<SearchQuery> queryBuilder = BooleanQuery.builder().disableCoord(true);
        Set tokensPowerSet = Sets.powerSet(tokens);
        for (Set set : tokensPowerSet) {
            if (set.size() != 2) continue;
            SpanNearQuery spanNearQuery = new SpanNearQuery(field, new ArrayList<String>(set), 5, false);
            queryBuilder.addShould(spanNearQuery);
        }
        return Optional.of(queryBuilder.boost(boost).build());
    }
}

