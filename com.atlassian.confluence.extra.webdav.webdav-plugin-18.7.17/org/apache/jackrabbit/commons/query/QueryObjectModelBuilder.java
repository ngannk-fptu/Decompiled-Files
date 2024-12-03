/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query;

import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;

public interface QueryObjectModelBuilder {
    public QueryObjectModel createQueryObjectModel(String var1, QueryObjectModelFactory var2, ValueFactory var3) throws InvalidQueryException, RepositoryException;

    public boolean canHandle(String var1);

    public String[] getSupportedLanguages();

    public String toString(QueryObjectModel var1) throws InvalidQueryException;
}

