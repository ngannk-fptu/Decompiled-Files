/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.QueryException;

public class QueryParameterException
extends QueryException {
    public QueryParameterException(String message) {
        super(message);
    }

    public QueryParameterException(String message, String queryString, Exception cause) {
        super(message, queryString, cause);
    }

    @Override
    protected QueryException generateQueryException(String queryString) {
        return new QueryParameterException(super.getOriginalMessage(), queryString, (Exception)((Object)this));
    }
}

