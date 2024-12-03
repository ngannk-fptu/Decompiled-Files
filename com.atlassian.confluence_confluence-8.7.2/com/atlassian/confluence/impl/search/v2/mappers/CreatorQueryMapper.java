/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.impl.search.v2.mappers.AbstractUserQueryMapper;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.query.CreatorQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class CreatorQueryMapper
extends AbstractUserQueryMapper<CreatorQuery> {
    @Override
    public Query internalConvertToLuceneQuery(CreatorQuery creatorQuery) {
        return new TermQuery(new Term(SearchFieldNames.CREATOR, this.getTermValue(creatorQuery)));
    }
}

