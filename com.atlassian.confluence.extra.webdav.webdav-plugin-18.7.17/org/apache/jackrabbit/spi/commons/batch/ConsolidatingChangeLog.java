/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.batch;

import java.util.Iterator;
import java.util.ListIterator;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.batch.AbstractChangeLog;
import org.apache.jackrabbit.spi.commons.batch.Operation;
import org.apache.jackrabbit.spi.commons.batch.Operations;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;

public class ConsolidatingChangeLog
extends AbstractChangeLog<CancelableOperation> {
    private static final PathFactory PATH_FACTORY = PathFactoryImpl.getInstance();

    protected static Path getPath(NodeId parentId, Name name) throws RepositoryException {
        Path parent = parentId.getPath();
        if (!parent.isAbsolute()) {
            return null;
        }
        return PATH_FACTORY.create(parent, name, true);
    }

    protected static Path getPath(ItemId itemId) {
        Path path = itemId.getPath();
        if (path != null && !path.isAbsolute()) {
            return null;
        }
        return path;
    }

    @Override
    public void addNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) throws RepositoryException {
        this.addOperation(CancelableOperations.addNode(parentId, nodeName, nodetypeName, uuid));
    }

    @Override
    public void addProperty(NodeId parentId, Name propertyName, QValue value) throws RepositoryException {
        this.addOperation(CancelableOperations.addProperty(parentId, propertyName, value));
    }

    @Override
    public void addProperty(NodeId parentId, Name propertyName, QValue[] values) throws RepositoryException {
        this.addOperation(CancelableOperations.addProperty(parentId, propertyName, values));
    }

    @Override
    public void move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) throws RepositoryException {
        this.addOperation(CancelableOperations.move(srcNodeId, destParentNodeId, destName));
    }

    @Override
    public void remove(ItemId itemId) throws RepositoryException {
        this.addOperation(CancelableOperations.remove(itemId));
    }

    @Override
    public void reorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) throws RepositoryException {
        this.addOperation(CancelableOperations.reorderNodes(parentId, srcNodeId, beforeNodeId));
    }

    @Override
    public void setMixins(NodeId nodeId, Name[] mixinNodeTypeNames) throws RepositoryException {
        this.addOperation(CancelableOperations.setMixins(nodeId, mixinNodeTypeNames));
    }

    @Override
    public void setPrimaryType(NodeId nodeId, Name primaryNodeTypeName) throws RepositoryException {
        this.addOperation(CancelableOperations.setPrimaryType(nodeId, primaryNodeTypeName));
    }

    @Override
    public void setValue(PropertyId propertyId, QValue value) throws RepositoryException {
        this.addOperation(CancelableOperations.setValue(propertyId, value));
    }

    @Override
    public void setValue(PropertyId propertyId, QValue[] values) throws RepositoryException {
        this.addOperation(CancelableOperations.setValue(propertyId, values));
    }

    @Override
    public void setTree(NodeId parentId, Tree contentTree) throws RepositoryException {
        this.addOperation(CancelableOperations.setTree(parentId, contentTree));
    }

    @Override
    public void addOperation(CancelableOperation op) throws RepositoryException {
        CancelableOperation otherOp = op;
        OperationsBackwardWithSentinel it = new OperationsBackwardWithSentinel();
        block6: while (it.hasNext()) {
            CancelableOperation thisOp = it.next();
            switch (thisOp.cancel(otherOp)) {
                case 0: {
                    it.remove();
                    continue block6;
                }
                case 1: {
                    return;
                }
                case 2: {
                    it.remove();
                    return;
                }
                case 3: {
                    super.addOperation(otherOp);
                    return;
                }
            }
            assert (false) : "Invalid case in switch";
        }
    }

    protected static final class CancelableOperations {
        private CancelableOperations() {
        }

        public static CancelableOperation empty() {
            return new Empty();
        }

        public static CancelableOperation addNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
            return new AddNode(parentId, nodeName, nodetypeName, uuid);
        }

        public static CancelableOperation addProperty(NodeId parentId, Name propertyName, QValue value) {
            return new AddProperty(parentId, propertyName, value);
        }

        public static CancelableOperation addProperty(NodeId parentId, Name propertyName, QValue[] values) {
            return new AddProperty(parentId, propertyName, values);
        }

        public static CancelableOperation move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
            return new Move(srcNodeId, destParentNodeId, destName);
        }

        public static CancelableOperation remove(ItemId itemId) {
            return new Remove(itemId);
        }

        public static CancelableOperation reorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
            return new ReorderNodes(parentId, srcNodeId, beforeNodeId);
        }

        public static CancelableOperation setMixins(NodeId nodeId, Name[] mixinNodeTypeNames) {
            return new SetMixins(nodeId, mixinNodeTypeNames);
        }

        public static CancelableOperation setPrimaryType(NodeId nodeId, Name primaryTypeName) {
            return new SetPrimaryType(nodeId, primaryTypeName);
        }

        public static CancelableOperation setValue(PropertyId propertyId, QValue value) {
            return new SetValue(propertyId, value);
        }

        public static CancelableOperation setValue(PropertyId propertyId, QValue[] values) {
            return new SetValue(propertyId, values);
        }

        public static CancelableOperation setTree(NodeId parentId, Tree tree) {
            return new SetTree(parentId, tree);
        }

        public static class SetTree
        extends Operations.SetTree
        implements CancelableOperation {
            public SetTree(NodeId parentId, Tree contentTree) {
                super(parentId, contentTree);
            }

            @Override
            public int cancel(CancelableOperation other) throws RepositoryException {
                if (other instanceof Remove) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.parentId, this.tree.getName());
                    Path otherPath = ConsolidatingChangeLog.getPath(((Remove)other).itemId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    if (thisPath.equals(otherPath)) {
                        return 2;
                    }
                    return thisPath.isDescendantOf(otherPath) ? 0 : 3;
                }
                return 3;
            }
        }

        public static class SetValue
        extends Operations.SetValue
        implements CancelableOperation {
            public SetValue(PropertyId propertyId, QValue value) {
                super(propertyId, value);
            }

            public SetValue(PropertyId propertyId, QValue[] values) {
                super(propertyId, values);
            }

            @Override
            public int cancel(CancelableOperation other) throws RepositoryException {
                if (other instanceof Remove) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.propertyId);
                    Path otherPath = ConsolidatingChangeLog.getPath(((Remove)other).itemId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    return thisPath.isDescendantOf(otherPath) || thisPath.equals(otherPath) ? 0 : 3;
                }
                if (other instanceof SetValue) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.propertyId);
                    Path otherPath = ConsolidatingChangeLog.getPath(((SetValue)other).propertyId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    if (thisPath.equals(otherPath)) {
                        return 0;
                    }
                }
                return 3;
            }
        }

        public static class SetPrimaryType
        extends Operations.SetPrimaryType
        implements CancelableOperation {
            public SetPrimaryType(NodeId nodeId, Name primaryTypeName) {
                super(nodeId, primaryTypeName);
            }

            @Override
            public int cancel(CancelableOperation other) throws RepositoryException {
                if (other instanceof Remove) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.nodeId);
                    Path otherPath = ConsolidatingChangeLog.getPath(((Remove)other).itemId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    return thisPath.isDescendantOf(otherPath) || thisPath.equals(otherPath) ? 0 : 3;
                }
                if (other instanceof SetPrimaryType) {
                    SetPrimaryType setPrimaryType = (SetPrimaryType)other;
                    if (this.primaryTypeName.equals(setPrimaryType.primaryTypeName)) {
                        Path thisPath = ConsolidatingChangeLog.getPath(this.nodeId);
                        Path otherPath = ConsolidatingChangeLog.getPath(setPrimaryType.nodeId);
                        if (thisPath == null || otherPath == null) {
                            return 3;
                        }
                        if (thisPath.equals(otherPath)) {
                            return 0;
                        }
                    }
                }
                return 3;
            }
        }

        public static class SetMixins
        extends Operations.SetMixins
        implements CancelableOperation {
            public SetMixins(NodeId nodeId, Name[] mixinNodeTypeNames) {
                super(nodeId, mixinNodeTypeNames);
            }

            @Override
            public int cancel(CancelableOperation other) throws RepositoryException {
                if (other instanceof Remove) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.nodeId);
                    Path otherPath = ConsolidatingChangeLog.getPath(((Remove)other).itemId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    return thisPath.isDescendantOf(otherPath) || thisPath.equals(otherPath) ? 0 : 3;
                }
                if (other instanceof SetMixins) {
                    SetMixins setMixin = (SetMixins)other;
                    if (this.mixinNodeTypeNames.length == setMixin.mixinNodeTypeNames.length) {
                        Path thisPath = ConsolidatingChangeLog.getPath(this.nodeId);
                        Path otherPath = ConsolidatingChangeLog.getPath(setMixin.nodeId);
                        if (thisPath == null || otherPath == null) {
                            return 3;
                        }
                        if (thisPath.equals(otherPath)) {
                            for (int k = 0; k < this.mixinNodeTypeNames.length; ++k) {
                                if (this.mixinNodeTypeNames[k].equals(setMixin.mixinNodeTypeNames[k])) continue;
                                return 3;
                            }
                            return 0;
                        }
                    }
                }
                return 3;
            }
        }

        public static class ReorderNodes
        extends Operations.ReorderNodes
        implements CancelableOperation {
            public ReorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
                super(parentId, srcNodeId, beforeNodeId);
            }

            @Override
            public int cancel(CancelableOperation other) throws RepositoryException {
                if (other instanceof Remove) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.srcNodeId);
                    Path otherPath = ConsolidatingChangeLog.getPath(((Remove)other).itemId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    return thisPath.isDescendantOf(otherPath) || thisPath.equals(otherPath) ? 0 : 3;
                }
                if (other instanceof ReorderNodes) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.parentId);
                    Path otherPath = ConsolidatingChangeLog.getPath(((ReorderNodes)other).parentId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    return thisPath.equals(otherPath) && !this.hasSNS(this.srcNodeId) && !this.hasSNS(this.beforeNodeId) ? 0 : 3;
                }
                return 3;
            }

            private boolean hasSNS(NodeId nodeId) {
                if (nodeId != null) {
                    Path path = ConsolidatingChangeLog.getPath(nodeId);
                    return path != null && path.getIndex() > 1;
                }
                return false;
            }
        }

        public static class Remove
        extends Operations.Remove
        implements CancelableOperation {
            public Remove(ItemId itemId) {
                super(itemId);
            }

            @Override
            public int cancel(CancelableOperation other) {
                return 3;
            }
        }

        public static class Move
        extends Operations.Move
        implements CancelableOperation {
            public Move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
                super(srcNodeId, destParentNodeId, destName);
            }

            @Override
            public int cancel(CancelableOperation other) {
                return 3;
            }
        }

        public static class AddProperty
        extends Operations.AddProperty
        implements CancelableOperation {
            public AddProperty(NodeId parentId, Name propertyName, QValue value) {
                super(parentId, propertyName, value);
            }

            public AddProperty(NodeId parentId, Name propertyName, QValue[] values) {
                super(parentId, propertyName, values);
            }

            @Override
            public int cancel(CancelableOperation other) throws RepositoryException {
                if (other instanceof Remove) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.parentId, this.propertyName);
                    Path otherPath = ConsolidatingChangeLog.getPath(((Remove)other).itemId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    if (thisPath.equals(otherPath)) {
                        return 2;
                    }
                    return thisPath.isDescendantOf(otherPath) ? 0 : 3;
                }
                if (other instanceof SetValue) {
                    SetValue setValue = (SetValue)other;
                    Path thisPath = ConsolidatingChangeLog.getPath(this.parentId, this.propertyName);
                    Path otherPath = ConsolidatingChangeLog.getPath(setValue.propertyId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    if (thisPath.equals(otherPath)) {
                        if (!this.isMultivalued && setValue.values[0] == null) {
                            return 2;
                        }
                        if (this.values.length == setValue.values.length) {
                            for (int k = 0; k < this.values.length; ++k) {
                                if (this.values[k].equals(setValue.values[k])) continue;
                                return 3;
                            }
                            return 1;
                        }
                    }
                }
                return 3;
            }
        }

        public static class AddNode
        extends Operations.AddNode
        implements CancelableOperation {
            public AddNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
                super(parentId, nodeName, nodetypeName, uuid);
            }

            @Override
            public int cancel(CancelableOperation other) throws RepositoryException {
                if (other instanceof Remove) {
                    Path thisPath = ConsolidatingChangeLog.getPath(this.parentId, this.nodeName);
                    Path otherPath = ConsolidatingChangeLog.getPath(((Remove)other).itemId);
                    if (thisPath == null || otherPath == null) {
                        return 3;
                    }
                    if (thisPath.equals(otherPath)) {
                        return 2;
                    }
                    return thisPath.isDescendantOf(otherPath) ? 0 : 3;
                }
                return 3;
            }
        }

        public static class Empty
        extends Operations.Empty
        implements CancelableOperation {
            @Override
            public int cancel(CancelableOperation other) throws RepositoryException {
                return 3;
            }
        }
    }

    protected static interface CancelableOperation
    extends Operation {
        public static final int CANCEL_THIS = 0;
        public static final int CANCEL_OTHER = 1;
        public static final int CANCEL_BOTH = 2;
        public static final int CANCEL_NONE = 3;

        public int cancel(CancelableOperation var1) throws RepositoryException;
    }

    private class OperationsBackwardWithSentinel
    implements Iterator<CancelableOperation> {
        private final ListIterator<CancelableOperation> it;
        private boolean last;
        private boolean done;

        private OperationsBackwardWithSentinel() {
            this.it = ConsolidatingChangeLog.this.operations.listIterator(ConsolidatingChangeLog.this.operations.size());
            this.last = !this.it.hasPrevious();
        }

        @Override
        public boolean hasNext() {
            return this.it.hasPrevious() || this.last;
        }

        @Override
        public CancelableOperation next() {
            if (this.last) {
                this.done = true;
                return CancelableOperations.empty();
            }
            CancelableOperation o = this.it.previous();
            this.last = !this.it.hasPrevious();
            return o;
        }

        @Override
        public void remove() {
            if (this.done) {
                throw new IllegalStateException("Cannot remove last element");
            }
            this.it.remove();
        }
    }
}

