/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.batch;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.batch.AbstractChangeLog;
import org.apache.jackrabbit.spi.commons.batch.Operation;
import org.apache.jackrabbit.spi.commons.batch.Operations;

public class ChangeLogImpl
extends AbstractChangeLog<Operation> {
    @Override
    public void addNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) throws RepositoryException {
        this.addOperation(Operations.addNode(parentId, nodeName, nodetypeName, uuid));
    }

    @Override
    public void addProperty(NodeId parentId, Name propertyName, QValue value) throws RepositoryException {
        this.addOperation(Operations.addProperty(parentId, propertyName, value));
    }

    @Override
    public void addProperty(NodeId parentId, Name propertyName, QValue[] values) throws RepositoryException {
        this.addOperation(Operations.addProperty(parentId, propertyName, values));
    }

    @Override
    public void move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) throws RepositoryException {
        this.addOperation(Operations.move(srcNodeId, destParentNodeId, destName));
    }

    @Override
    public void remove(ItemId itemId) throws RepositoryException {
        this.addOperation(Operations.remove(itemId));
    }

    @Override
    public void reorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) throws RepositoryException {
        this.addOperation(Operations.reorderNodes(parentId, srcNodeId, beforeNodeId));
    }

    @Override
    public void setMixins(NodeId nodeId, Name[] mixinNodeTypeNames) throws RepositoryException {
        this.addOperation(Operations.setMixins(nodeId, mixinNodeTypeNames));
    }

    @Override
    public void setPrimaryType(NodeId nodeId, Name primaryNodeTypeName) throws RepositoryException {
        this.addOperation(Operations.setPrimaryType(nodeId, primaryNodeTypeName));
    }

    @Override
    public void setValue(PropertyId propertyId, QValue value) throws RepositoryException {
        this.addOperation(Operations.setValue(propertyId, value));
    }

    @Override
    public void setValue(PropertyId propertyId, QValue[] values) throws RepositoryException {
        this.addOperation(Operations.setValue(propertyId, values));
    }

    @Override
    public void setTree(NodeId parentId, Tree contentTree) throws RepositoryException {
        this.addOperation(Operations.setTree(parentId, contentTree));
    }
}

