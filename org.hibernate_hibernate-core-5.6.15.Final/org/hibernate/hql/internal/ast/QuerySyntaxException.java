/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 */
package org.hibernate.hql.internal.ast;

import antlr.RecognitionException;
import org.hibernate.QueryException;

public class QuerySyntaxException
extends QueryException {
    public QuerySyntaxException(String message) {
        super(message);
    }

    public QuerySyntaxException(String message, String hql) {
        super(message, hql);
    }

    protected QuerySyntaxException(String message, String queryString, Exception cause) {
        super(message, queryString, cause);
    }

    public static QuerySyntaxException convert(RecognitionException e) {
        return QuerySyntaxException.convert(e, null);
    }

    public static QuerySyntaxException convert(RecognitionException e, String hql) {
        String positionInfo = e.getLine() > 0 && e.getColumn() > 0 ? " near line " + e.getLine() + ", column " + e.getColumn() : "";
        return new QuerySyntaxException(e.getMessage() + positionInfo, hql);
    }

    @Override
    protected QueryException generateQueryException(String queryString) {
        return new QuerySyntaxException(this.getOriginalMessage(), queryString, (Exception)((Object)this));
    }
}

