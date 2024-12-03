/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal;

import org.hibernate.QueryException;

public class QueryExecutionRequestException
extends QueryException {
    public QueryExecutionRequestException(String message, String queryString) {
        super(message, queryString);
        if (queryString == null) {
            throw new IllegalArgumentException("Illegal to pass null as queryString argument");
        }
    }
}

