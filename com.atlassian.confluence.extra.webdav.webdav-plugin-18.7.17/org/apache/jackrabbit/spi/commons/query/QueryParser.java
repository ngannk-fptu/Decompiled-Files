/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.QueryTreeBuilder;
import org.apache.jackrabbit.spi.commons.query.QueryTreeBuilderRegistry;

public class QueryParser {
    private QueryParser() {
    }

    public static QueryRootNode parse(String statement, String language, NameResolver resolver, QueryNodeFactory factory) throws InvalidQueryException {
        QueryTreeBuilder builder = QueryTreeBuilderRegistry.getQueryTreeBuilder(language);
        return builder.createQueryTree(statement, resolver, factory);
    }

    public static String toString(QueryRootNode root, String language, NameResolver resolver) throws InvalidQueryException {
        QueryTreeBuilder builder = QueryTreeBuilderRegistry.getQueryTreeBuilder(language);
        return builder.toString(root, resolver);
    }
}

