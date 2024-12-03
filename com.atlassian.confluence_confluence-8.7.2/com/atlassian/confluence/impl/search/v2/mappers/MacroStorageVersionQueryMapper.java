/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.lucene.queryparser.flexible.core.QueryNodeException
 *  org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
 *  org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler$Operator
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryParserFactory;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.query.MacroStorageVersionQuery;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.search.Query;

@Internal
public class MacroStorageVersionQueryMapper
implements LuceneQueryMapper<MacroStorageVersionQuery> {
    private LuceneQueryParserFactory luceneQueryParserFactory;

    @Override
    public Query convertToLuceneQuery(MacroStorageVersionQuery searchQuery) {
        String name = searchQuery.getMacroName();
        int version = searchQuery.getVersion();
        String nameAndVersion = version + "_" + name;
        StandardQueryParser queryParser = this.luceneQueryParserFactory.createQueryParser();
        queryParser.setLowercaseExpandedTerms(false);
        queryParser.setDefaultOperator(StandardQueryConfigHandler.Operator.AND);
        try {
            return queryParser.parse(nameAndVersion, SearchFieldNames.MACRO_STORAGE_VERSION);
        }
        catch (QueryNodeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setLuceneQueryParserFactory(LuceneQueryParserFactory luceneQueryParserFactory) {
        this.luceneQueryParserFactory = luceneQueryParserFactory;
    }
}

