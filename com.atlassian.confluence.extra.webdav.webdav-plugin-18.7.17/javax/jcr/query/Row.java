/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

public interface Row {
    public Value[] getValues() throws RepositoryException;

    public Value getValue(String var1) throws ItemNotFoundException, RepositoryException;

    public Node getNode() throws RepositoryException;

    public Node getNode(String var1) throws RepositoryException;

    public String getPath() throws RepositoryException;

    public String getPath(String var1) throws RepositoryException;

    public double getScore() throws RepositoryException;

    public double getScore(String var1) throws RepositoryException;
}

