/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.Tree;

public class SerializableBatch
implements Batch,
Serializable {
    private List<Operation> recording = new ArrayList<Operation>();
    private final ItemId itemId;

    public SerializableBatch(ItemId itemId) {
        this.itemId = itemId;
    }

    public ItemId getSaveTarget() {
        return this.itemId;
    }

    public void replay(Batch batch) throws PathNotFoundException, ItemNotFoundException, NoSuchNodeTypeException, ValueFormatException, VersionException, LockException, ConstraintViolationException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
        for (Operation operation : this.recording) {
            operation.replay(batch);
        }
    }

    @Override
    public void addNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
        this.recording.add(new AddNode(parentId, nodeName, nodetypeName, uuid));
    }

    @Override
    public void addProperty(NodeId parentId, Name propertyName, QValue value) {
        this.recording.add(new AddProperty(parentId, propertyName, new QValue[]{value}, false));
    }

    @Override
    public void addProperty(NodeId parentId, Name propertyName, QValue[] values) {
        this.recording.add(new AddProperty(parentId, propertyName, values, true));
    }

    @Override
    public void setValue(PropertyId propertyId, QValue value) {
        this.recording.add(new SetValue(propertyId, new QValue[]{value}, false));
    }

    @Override
    public void setValue(PropertyId propertyId, QValue[] values) {
        this.recording.add(new SetValue(propertyId, values, true));
    }

    @Override
    public void remove(ItemId itemId) {
        this.recording.add(new Remove(itemId));
    }

    @Override
    public void reorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
        this.recording.add(new ReorderNodes(parentId, srcNodeId, beforeNodeId));
    }

    @Override
    public void setMixins(NodeId nodeId, Name[] mixinNodeTypeIds) {
        this.recording.add(new SetMixins(nodeId, mixinNodeTypeIds));
    }

    @Override
    public void setPrimaryType(NodeId nodeId, Name primaryNodeTypeName) throws RepositoryException {
        this.recording.add(new SetPrimaryType(nodeId, primaryNodeTypeName));
    }

    @Override
    public void move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
        this.recording.add(new Move(srcNodeId, destParentNodeId, destName));
    }

    @Override
    public void setTree(NodeId parentId, Tree contentTree) throws RepositoryException {
        this.recording.add(new SetTree(parentId, contentTree));
    }

    private static class Move
    implements Operation {
        private final NodeId srcNodeId;
        private final NodeId destParentNodeId;
        private final Name destName;

        Move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
            this.srcNodeId = srcNodeId;
            this.destParentNodeId = destParentNodeId;
            this.destName = destName;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            batch.move(this.srcNodeId, this.destParentNodeId, this.destName);
        }
    }

    private static class SetPrimaryType
    implements Operation {
        private final NodeId nodeId;
        private final Name primaryNodeTypeName;

        SetPrimaryType(NodeId nodeId, Name primaryNodeTypeName) {
            this.nodeId = nodeId;
            this.primaryNodeTypeName = primaryNodeTypeName;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            batch.setPrimaryType(this.nodeId, this.primaryNodeTypeName);
        }
    }

    private static class SetMixins
    implements Operation {
        private final NodeId nodeId;
        private final Name[] mixinNodeTypeNames;

        SetMixins(NodeId nodeId, Name[] mixinNodeTypeNames) {
            this.nodeId = nodeId;
            this.mixinNodeTypeNames = mixinNodeTypeNames;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            batch.setMixins(this.nodeId, this.mixinNodeTypeNames);
        }
    }

    private static class ReorderNodes
    implements Operation {
        private final NodeId parentId;
        private final NodeId srcNodeId;
        private final NodeId beforeNodeId;

        ReorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
            this.parentId = parentId;
            this.srcNodeId = srcNodeId;
            this.beforeNodeId = beforeNodeId;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            batch.reorderNodes(this.parentId, this.srcNodeId, this.beforeNodeId);
        }
    }

    private static class Remove
    implements Operation {
        private final ItemId itemId;

        Remove(ItemId itemId) {
            this.itemId = itemId;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            batch.remove(this.itemId);
        }
    }

    private static class SetValue
    implements Operation {
        private final PropertyId propertyId;
        private final QValue[] values;
        private final boolean isMultiValued;

        SetValue(PropertyId propertyId, QValue[] values, boolean isMultiValued) {
            this.propertyId = propertyId;
            this.values = values;
            this.isMultiValued = isMultiValued;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            if (this.isMultiValued) {
                batch.setValue(this.propertyId, this.values);
            } else {
                batch.setValue(this.propertyId, this.values[0]);
            }
        }
    }

    private static class AddProperty
    implements Operation {
        private final NodeId parentId;
        private final Name propertyName;
        private final QValue[] values;
        private final boolean isMultiValued;

        AddProperty(NodeId parentId, Name propertyName, QValue[] values, boolean isMultiValued) {
            this.parentId = parentId;
            this.propertyName = propertyName;
            this.values = values;
            this.isMultiValued = isMultiValued;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            if (this.isMultiValued) {
                batch.addProperty(this.parentId, this.propertyName, this.values);
            } else {
                batch.addProperty(this.parentId, this.propertyName, this.values[0]);
            }
        }
    }

    private static class SetTree
    implements Operation {
        private final NodeId parentId;
        private final Tree contentTree;

        SetTree(NodeId parentId, Tree contentTree) {
            this.parentId = parentId;
            this.contentTree = contentTree;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            batch.setTree(this.parentId, this.contentTree);
        }
    }

    private static class AddNode
    implements Operation {
        private final NodeId parentId;
        private final Name nodeName;
        private final Name nodetypeName;
        private final String uuid;

        AddNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
            this.parentId = parentId;
            this.nodeName = nodeName;
            this.nodetypeName = nodetypeName;
            this.uuid = uuid;
        }

        @Override
        public void replay(Batch batch) throws RepositoryException {
            batch.addNode(this.parentId, this.nodeName, this.nodetypeName, this.uuid);
        }
    }

    public static interface Operation
    extends Serializable {
        public void replay(Batch var1) throws RepositoryException;
    }
}

