/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.logging.AbstractLogger;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;

public class BatchLogger
extends AbstractLogger
implements Batch {
    private final Batch batch;

    public BatchLogger(Batch batch, LogWriter writer) {
        super(writer);
        this.batch = batch;
    }

    public Batch getBatch() {
        return this.batch;
    }

    @Override
    public void addNode(final NodeId parentId, final Name nodeName, final Name nodetypeName, final String uuid) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.addNode(parentId, nodeName, nodetypeName, uuid);
                return null;
            }
        }, "addNode(NodeId, Name, Name, String)", new Object[]{parentId, nodeName, nodetypeName, uuid});
    }

    @Override
    public void addProperty(final NodeId parentId, final Name propertyName, final QValue value) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.addProperty(parentId, propertyName, value);
                return null;
            }
        }, "addProperty(NodeId, Name, QValue)", new Object[]{parentId, propertyName, value});
    }

    @Override
    public void addProperty(final NodeId parentId, final Name propertyName, final QValue[] values) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.addProperty(parentId, propertyName, values);
                return null;
            }
        }, "addProperty(NodeId, Name, QValue[])", new Object[]{parentId, propertyName, values});
    }

    @Override
    public void setValue(final PropertyId propertyId, final QValue value) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.setValue(propertyId, value);
                return null;
            }
        }, "setValue(PropertyId, QValue)", new Object[]{propertyId, value});
    }

    @Override
    public void setValue(final PropertyId propertyId, final QValue[] values) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.setValue(propertyId, values);
                return null;
            }
        }, "setValue(PropertyId, QValue[])", new Object[]{propertyId, values});
    }

    @Override
    public void remove(final ItemId itemId) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.remove(itemId);
                return null;
            }
        }, "remove(ItemId)", new Object[]{itemId});
    }

    @Override
    public void reorderNodes(final NodeId parentId, final NodeId srcNodeId, final NodeId beforeNodeId) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.reorderNodes(parentId, srcNodeId, beforeNodeId);
                return null;
            }
        }, "reorderNodes(NodeId, NodeId, NodeId)", new Object[]{parentId, srcNodeId, beforeNodeId});
    }

    @Override
    public void setMixins(final NodeId nodeId, final Name[] mixinNodeTypeNames) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.setMixins(nodeId, mixinNodeTypeNames);
                return null;
            }
        }, "setMixins(NodeId, Name[])", new Object[]{nodeId, mixinNodeTypeNames});
    }

    @Override
    public void setPrimaryType(final NodeId nodeId, final Name primaryNodeTypeName) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.setPrimaryType(nodeId, primaryNodeTypeName);
                return null;
            }
        }, "setPrimaryType(NodeId, Name)", new Object[]{nodeId, primaryNodeTypeName});
    }

    @Override
    public void move(final NodeId srcNodeId, final NodeId destParentNodeId, final Name destName) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.move(srcNodeId, destParentNodeId, destName);
                return null;
            }
        }, "move(NodeId, NodeId, Name)", new Object[]{srcNodeId, destParentNodeId, destName});
    }

    @Override
    public void setTree(final NodeId parentId, final Tree contentTree) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                BatchLogger.this.batch.setTree(parentId, contentTree);
                return null;
            }
        }, "setTree(NodeId, Tree)", new Object[]{parentId, contentTree});
    }
}

