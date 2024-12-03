/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.RowIterator;

public interface QueryResult {
    public String[] getColumnNames() throws RepositoryException;

    public RowIterator getRows() throws RepositoryException;

    public NodeIterator getNodes() throws RepositoryException;

    public String[] getSelectorNames() throws RepositoryException;
}

