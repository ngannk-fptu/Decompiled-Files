/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.xpath;

import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.QueryTreeBuilder;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathQueryBuilder;

public class QueryBuilder
implements QueryTreeBuilder {
    @Override
    public QueryRootNode createQueryTree(String statement, NameResolver resolver, QueryNodeFactory factory) throws InvalidQueryException {
        return XPathQueryBuilder.createQuery(statement, resolver, factory);
    }

    @Override
    public boolean canHandle(String language) {
        return "xpath".equals(language);
    }

    @Override
    public String[] getSupportedLanguages() {
        return new String[]{"xpath"};
    }

    @Override
    public String toString(QueryRootNode root, NameResolver resolver) throws InvalidQueryException {
        return XPathQueryBuilder.toString(root, resolver);
    }
}

