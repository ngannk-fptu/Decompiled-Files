/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class QueryException
extends HibernateException {
    private final String queryString;

    public QueryException(String message) {
        this(message, null, null);
    }

    public QueryException(String message, Exception cause) {
        this(message, null, cause);
    }

    public QueryException(String message, String queryString) {
        this(message, queryString, null);
    }

    public QueryException(String message, String queryString, Exception cause) {
        super(message, cause);
        this.queryString = queryString;
    }

    public QueryException(Exception cause) {
        this("A query exception occurred", null, cause);
    }

    public String getQueryString() {
        return this.queryString;
    }

    public String getMessage() {
        String msg = this.getOriginalMessage();
        if (this.queryString != null) {
            msg = msg + " [" + this.queryString + ']';
        }
        return msg;
    }

    protected final String getOriginalMessage() {
        return super.getMessage();
    }

    public final QueryException wrapWithQueryString(String queryString) {
        if (this.getQueryString() != null) {
            return this;
        }
        return this.generateQueryException(queryString);
    }

    protected QueryException generateQueryException(String queryString) {
        return new QueryException(this.getOriginalMessage(), queryString, (Exception)((Object)this));
    }
}

