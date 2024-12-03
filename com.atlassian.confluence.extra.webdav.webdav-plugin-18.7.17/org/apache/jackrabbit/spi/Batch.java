/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.Tree;

public interface Batch {
    public void addNode(NodeId var1, Name var2, Name var3, String var4) throws RepositoryException;

    public void addProperty(NodeId var1, Name var2, QValue var3) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, PathNotFoundException, ItemExistsException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

    public void addProperty(NodeId var1, Name var2, QValue[] var3) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, PathNotFoundException, ItemExistsException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

    public void setValue(PropertyId var1, QValue var2) throws RepositoryException;

    public void setValue(PropertyId var1, QValue[] var2) throws RepositoryException;

    public void remove(ItemId var1) throws RepositoryException;

    public void reorderNodes(NodeId var1, NodeId var2, NodeId var3) throws RepositoryException;

    public void setMixins(NodeId var1, Name[] var2) throws RepositoryException;

    public void setPrimaryType(NodeId var1, Name var2) throws RepositoryException;

    public void move(NodeId var1, NodeId var2, Name var3) throws RepositoryException;

    public void setTree(NodeId var1, Tree var2) throws RepositoryException;
}

