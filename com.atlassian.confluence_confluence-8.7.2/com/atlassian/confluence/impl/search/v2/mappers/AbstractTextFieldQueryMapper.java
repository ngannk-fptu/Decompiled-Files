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
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryUtil;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.SearchQuery;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.search.Query;

public abstract class AbstractTextFieldQueryMapper<T extends SearchQuery>
implements LuceneQueryMapper<T> {
    protected Query tryParse(StandardQueryParser queryParser, BooleanOperator operator, String rawQuery, String defaultQueryField) {
        if (BooleanOperator.AND == operator) {
            queryParser.setDefaultOperator(StandardQueryConfigHandler.Operator.AND);
        } else if (BooleanOperator.OR == operator) {
            queryParser.setDefaultOperator(StandardQueryConfigHandler.Operator.OR);
        } else {
            throw new UnsupportedOperationException("Unsupported operator found: " + operator);
        }
        try {
            return queryParser.parse(rawQuery, defaultQueryField);
        }
        catch (QueryNodeException e1) {
            try {
                return queryParser.parse(LuceneQueryUtil.safeEscape(rawQuery), defaultQueryField);
            }
            catch (QueryNodeException e2) {
                throw new IllegalArgumentException(e2);
            }
        }
    }
}

