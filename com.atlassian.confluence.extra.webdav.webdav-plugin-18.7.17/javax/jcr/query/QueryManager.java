/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.qom.QueryObjectModelFactory;

public interface QueryManager {
    public Query createQuery(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public QueryObjectModelFactory getQOMFactory();

    public Query getQuery(Node var1) throws InvalidQueryException, RepositoryException;

    public String[] getSupportedQueryLanguages() throws RepositoryException;
}

