/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.TermSetQuery
 *  org.opensearch.client.opensearch._types.FieldValue
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 */
package com.atlassian.confluence.plugins.opensearch.mappers.query;

import com.atlassian.confluence.plugins.opensearch.mappers.query.OpenSearchQueryMapper;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import java.util.List;
import java.util.stream.Collectors;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class OpenSearchTermSetQueryMapper
implements OpenSearchQueryMapper<TermSetQuery> {
    @Override
    public Query mapQueryToOpenSearch(TermSetQuery query) {
        List terms = query.getValues().stream().map(FieldValue::of).collect(Collectors.toList());
        return Query.of(q -> q.terms(ts -> ts.field(query.getFieldName()).terms(tqf -> tqf.value(terms))));
    }

    @Override
    public String getKey() {
        return "termSet";
    }
}

