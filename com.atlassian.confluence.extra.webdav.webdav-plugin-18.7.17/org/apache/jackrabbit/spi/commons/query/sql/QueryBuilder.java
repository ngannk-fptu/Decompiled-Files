/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.QueryTreeBuilder;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLQueryBuilder;

public class QueryBuilder
implements QueryTreeBuilder {
    @Override
    public QueryRootNode createQueryTree(String statement, NameResolver resolver, QueryNodeFactory factory) throws InvalidQueryException {
        return JCRSQLQueryBuilder.createQuery(statement, resolver, factory);
    }

    @Override
    public boolean canHandle(String language) {
        return "sql".equals(language);
    }

    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"sql"};
    }

    @Override
    public String toString(QueryRootNode root, NameResolver resolver) throws InvalidQueryException {
        return JCRSQLQueryBuilder.toString(root, resolver);
    }
}

