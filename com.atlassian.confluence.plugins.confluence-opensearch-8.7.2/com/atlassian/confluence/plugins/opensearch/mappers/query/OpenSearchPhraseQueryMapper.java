/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.PhraseQuery
 *  org.opensearch.client.opensearch._types.query_dsl.MatchPhraseQuery$Builder
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.OpenSearchAnalyzerMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.PhraseQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchPhraseQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class OpenSearchPhraseQueryMapper
implements OpenSearchQueryMapper<PhraseQuery> {
    private final OpenSearchAnalyzerMapper analyzerMapper;

    public OpenSearchPhraseQueryMapper(OpenSearchAnalyzerMapper analyzerMapper) {
        this.analyzerMapper = analyzerMapper;
    }

    @Override
    public Query mapQueryToOpenSearch(PhraseQuery query) {
        return Query.of(q -> q.matchPhrase(p -> ((MatchPhraseQuery.Builder)p.field(query.getFieldName()).query(query.getText()).slop(Integer.valueOf(query.getSlop())).boost(Float.valueOf(query.getBoost()))).analyzer(this.analyzerMapper.getAnalyzerName(query.getAnalyzerDescriptorProvider()))));
    }

    @Override
    public String getKey() {
        return "phrase";
    }
}

