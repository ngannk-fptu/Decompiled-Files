/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;

public interface QueryTreeBuilder {
    public QueryRootNode createQueryTree(String var1, NameResolver var2, QueryNodeFactory var3) throws InvalidQueryException;

    public boolean canHandle(String var1);

    public String[] getSupportedLanguages();

    public String toString(QueryRootNode var1, NameResolver var2) throws InvalidQueryException;
}

