/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.queryparser.flexible.core.QueryNodeException
 *  org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
 *  org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler$Operator
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryParserFactory;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.query.TextSearchQuery;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.search.Query;

public class TextSearchQueryMapper
implements LuceneQueryMapper<TextSearchQuery> {
    private LuceneQueryParserFactory luceneQueryParserFactory;

    @Override
    public Query convertToLuceneQuery(TextSearchQuery searchQuery) {
        StandardQueryParser queryParser = this.luceneQueryParserFactory.createQueryParser();
        StandardQueryConfigHandler.Operator operator = searchQuery.getOperator() == BooleanOperator.AND ? StandardQueryConfigHandler.Operator.AND : StandardQueryConfigHandler.Operator.OR;
        queryParser.setDefaultOperator(operator);
        try {
            return queryParser.parse(searchQuery.getRawQuery(), searchQuery.getFieldName());
        }
        catch (QueryNodeException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLuceneQueryParserFactory(LuceneQueryParserFactory luceneQueryParserFactory) {
        this.luceneQueryParserFactory = luceneQueryParserFactory;
    }
}

