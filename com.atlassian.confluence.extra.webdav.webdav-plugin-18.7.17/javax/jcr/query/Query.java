/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query;

import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

public interface Query {
    public static final String XPATH = "xpath";
    public static final String SQL = "sql";
    public static final String JCR_SQL2 = "JCR-SQL2";
    public static final String JCR_JQOM = "JCR-JQOM";

    public QueryResult execute() throws InvalidQueryException, RepositoryException;

    public void setLimit(long var1);

    public void setOffset(long var1);

    public String getStatement();

    public String getLanguage();

    public String getStoredQueryPath() throws ItemNotFoundException, RepositoryException;

    public Node storeAsNode(String var1) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, UnsupportedRepositoryOperationException, RepositoryException;

    public void bindValue(String var1, Value var2) throws IllegalArgumentException, RepositoryException;

    public String[] getBindVariableNames() throws RepositoryException;
}

