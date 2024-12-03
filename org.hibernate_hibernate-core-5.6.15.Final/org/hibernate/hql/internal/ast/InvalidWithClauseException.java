/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast;

import org.hibernate.hql.internal.ast.QuerySyntaxException;

public class InvalidWithClauseException
extends QuerySyntaxException {
    public InvalidWithClauseException(String message, String queryString) {
        super(message, queryString);
        if (queryString == null) {
            throw new IllegalArgumentException("Illegal to pass null as queryString argument");
        }
    }
}

