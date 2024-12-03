/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.impl.search.v2.mappers.AbstractTextFieldQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryParserFactory;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import java.util.Collections;
import java.util.Map;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Query;

public class TextFieldQueryMapper
extends AbstractTextFieldQueryMapper<TextFieldQuery> {
    private LuceneQueryParserFactory luceneQueryParserFactory;

    @Override
    public Query convertToLuceneQuery(TextFieldQuery textFieldQuery) {
        Map<String, AnalyzerDescriptorProvider> fieldAnalyzers = Collections.singletonMap(textFieldQuery.getFieldName(), textFieldQuery::getAnalyzer);
        StandardQueryParser queryParser = this.luceneQueryParserFactory.createQueryParser(fieldAnalyzers);
        return this.tryParse(queryParser, textFieldQuery.getOperator(), textFieldQuery.getUnescapedQuery(), textFieldQuery.getFieldName());
    }

    public void setLuceneQueryParserFactory(LuceneQueryParserFactory luceneQueryParserFactory) {
        this.luceneQueryParserFactory = luceneQueryParserFactory;
    }
}

