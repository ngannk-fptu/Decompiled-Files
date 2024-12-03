/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.Iterator;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeExistsException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;

public interface NodeTypeStorage {
    public Iterator<QNodeTypeDefinition> getAllDefinitions() throws RepositoryException;

    public Iterator<QNodeTypeDefinition> getDefinitions(Name[] var1) throws NoSuchNodeTypeException, RepositoryException;

    public void registerNodeTypes(QNodeTypeDefinition[] var1, boolean var2) throws RepositoryException, NodeTypeExistsException;

    public void unregisterNodeTypes(Name[] var1) throws NoSuchNodeTypeException, RepositoryException;
}

