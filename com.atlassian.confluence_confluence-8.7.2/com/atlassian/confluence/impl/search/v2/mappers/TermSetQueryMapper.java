/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.impl.search.v2.lucene.TermsQuery;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import org.apache.lucene.search.Query;

public class TermSetQueryMapper
implements LuceneQueryMapper<TermSetQuery> {
    @Override
    public Query convertToLuceneQuery(TermSetQuery query) {
        return new TermsQuery(query.getFieldName(), query.getValues());
    }
}

