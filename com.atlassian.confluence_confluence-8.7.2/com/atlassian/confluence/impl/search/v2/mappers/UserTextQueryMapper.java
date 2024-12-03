/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  org.apache.lucene.queryparser.classic.MultiFieldQueryParser
 *  org.apache.lucene.queryparser.classic.ParseException
 *  org.apache.lucene.queryparser.classic.QueryParser$Operator
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.search.v2.query.UserTextQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class UserTextQueryMapper
implements LuceneQueryMapper<UserTextQuery> {
    private LuceneAnalyzerFactory luceneAnalyzerFactory;

    @Override
    public Query convertToLuceneQuery(UserTextQuery searchQuery) {
        MultiFieldQueryParser parser = new MultiFieldQueryParser(LuceneConstants.LUCENE_VERSION, new String[]{"username", "fullName", "email"}, this.luceneAnalyzerFactory.createAnalyzer());
        parser.setDefaultOperator(QueryParser.Operator.AND);
        try {
            return parser.parse(searchQuery.getQueryString());
        }
        catch (ParseException e) {
            throw new RuntimeException("Unable to parse query: " + e, e);
        }
    }

    public void setLuceneAnalyzerFactory(LuceneAnalyzerFactory luceneAnalyzerFactory) {
        this.luceneAnalyzerFactory = luceneAnalyzerFactory;
    }
}

