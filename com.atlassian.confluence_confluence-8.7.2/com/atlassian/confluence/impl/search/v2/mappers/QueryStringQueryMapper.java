/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
 *  org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler$Operator
 *  org.apache.lucene.search.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.impl.search.v2.mappers.AbstractTextFieldQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryParserFactory;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.query.QueryStringQuery;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryStringQueryMapper
extends AbstractTextFieldQueryMapper<QueryStringQuery> {
    private static final Logger log = LoggerFactory.getLogger(QueryStringQueryMapper.class);
    private LuceneQueryParserFactory luceneQueryParserFactory;

    @Override
    public Query convertToLuceneQuery(QueryStringQuery query) {
        CharSequence[] charSequenceArray;
        StandardQueryParser queryParser = this.luceneQueryParserFactory.createQueryParser(query.getAnalyzerProviders());
        if (query.getFieldNames().isEmpty()) {
            String[] stringArray = new String[1];
            charSequenceArray = stringArray;
            stringArray[0] = "";
        } else {
            charSequenceArray = query.getFieldNames().toArray(new String[0]);
        }
        queryParser.setMultiFields(charSequenceArray);
        queryParser.setDefaultOperator(query.getOperator() == BooleanOperator.AND ? StandardQueryConfigHandler.Operator.AND : StandardQueryConfigHandler.Operator.OR);
        queryParser.setFieldsBoost(query.getFieldsBoost());
        return this.tryParse(queryParser, query.getOperator(), query.getQuery(), null);
    }

    public void setLuceneQueryParserFactory(LuceneQueryParserFactory luceneQueryParserFactory) {
        this.luceneQueryParserFactory = luceneQueryParserFactory;
    }
}

