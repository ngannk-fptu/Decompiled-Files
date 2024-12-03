/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.batch;

import java.util.Arrays;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.batch.Operation;

public final class Operations {
    private Operations() {
    }

    public static Operation empty() {
        return Empty.INSTANCE;
    }

    public static Operation addNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
        return new AddNode(parentId, nodeName, nodetypeName, uuid);
    }

    public static Operation addProperty(NodeId parentId, Name propertyName, QValue value) {
        return new AddProperty(parentId, propertyName, value);
    }

    public static Operation addProperty(NodeId parentId, Name propertyName, QValue[] values) {
        return new AddProperty(parentId, propertyName, values);
    }

    public static Operation move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
        return new Move(srcNodeId, destParentNodeId, destName);
    }

    public static Operation remove(ItemId itemId) {
        return new Remove(itemId);
    }

    public static Operation reorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
        return new ReorderNodes(parentId, srcNodeId, beforeNodeId);
    }

    public static Operation setMixins(NodeId nodeId, Name[] mixinNodeTypeNames) {
        return new SetMixins(nodeId, mixinNodeTypeNames);
    }

    public static Operation setPrimaryType(NodeId nodeId, Name primaryTypeName) {
        return new SetPrimaryType(nodeId, primaryTypeName);
    }

    public static Operation setValue(PropertyId propertyId, QValue value) {
        return new SetValue(propertyId, value);
    }

    public static Operation setValue(PropertyId propertyId, QValue[] values) {
        return new SetValue(propertyId, values);
    }

    protected static boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    protected static int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    public static Operation setTree(NodeId parentId, Tree contentTree) {
        return new SetTree(parentId, contentTree);
    }

    public static class SetTree
    implements Operation {
        protected final NodeId parentId;
        protected final Tree tree;

        public SetTree(NodeId parentId, Tree tree) {
            this.parentId = parentId;
            this.tree = tree;
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            batch.setTree(this.parentId, this.tree);
        }

        public String toString() {
            return "SetTree[" + this.parentId + ", " + this.tree + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof SetTree) {
                return this.equals((SetTree)other);
            }
            return false;
        }

        public boolean equals(SetTree other) {
            return Operations.equals(this.parentId, other.parentId) && Operations.equals(this.tree, other.tree);
        }

        public int hashCode() {
            return 41 * (41 + Operations.hashCode(this.parentId)) + Operations.hashCode(this.tree);
        }
    }

    public static class SetValue
    implements Operation {
        protected final PropertyId propertyId;
        protected final QValue[] values;
        protected final boolean isMultivalued;

        private SetValue(PropertyId propertyId, QValue[] values, boolean isMultivalued) {
            this.propertyId = propertyId;
            this.values = values;
            this.isMultivalued = isMultivalued;
        }

        public SetValue(PropertyId propertyId, QValue value) {
            this(propertyId, new QValue[]{value}, false);
        }

        public SetValue(PropertyId propertyId, QValue[] values) {
            this(propertyId, values, true);
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            if (this.isMultivalued) {
                batch.setValue(this.propertyId, this.values);
            } else {
                batch.setValue(this.propertyId, this.values[0]);
            }
        }

        public String toString() {
            return "SetValue[" + this.propertyId + ", " + Arrays.toString(this.values) + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof SetValue) {
                return this.equals((SetValue)other);
            }
            return false;
        }

        public boolean equals(SetValue other) {
            return Operations.equals(this.propertyId, other.propertyId) && this.isMultivalued == other.isMultivalued && Arrays.equals(this.values, other.values);
        }

        public int hashCode() {
            return 41 * (41 + Operations.hashCode(this.propertyId)) + Operations.hashCode(this.values);
        }
    }

    public static class SetPrimaryType
    implements Operation {
        protected final NodeId nodeId;
        protected final Name primaryTypeName;

        public SetPrimaryType(NodeId nodeId, Name primaryTypeName) {
            this.nodeId = nodeId;
            this.primaryTypeName = primaryTypeName;
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            batch.setPrimaryType(this.nodeId, this.primaryTypeName);
        }

        public String toString() {
            return "SetPrimaryType[" + this.nodeId + ", " + this.primaryTypeName + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof SetPrimaryType) {
                return this.equals((SetPrimaryType)other);
            }
            return false;
        }

        public boolean equals(SetPrimaryType other) {
            return Operations.equals(this.nodeId, other.nodeId) && this.primaryTypeName.equals(other.primaryTypeName);
        }

        public int hashCode() {
            return 41 * (41 + Operations.hashCode(this.nodeId)) + Operations.hashCode(this.primaryTypeName);
        }
    }

    public static class SetMixins
    implements Operation {
        protected final NodeId nodeId;
        protected final Name[] mixinNodeTypeNames;

        public SetMixins(NodeId nodeId, Name[] mixinNodeTypeNames) {
            this.nodeId = nodeId;
            this.mixinNodeTypeNames = mixinNodeTypeNames;
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            batch.setMixins(this.nodeId, this.mixinNodeTypeNames);
        }

        public String toString() {
            return "SetMixins[" + this.nodeId + ", " + Arrays.toString(this.mixinNodeTypeNames) + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof SetMixins) {
                return this.equals((SetMixins)other);
            }
            return false;
        }

        public boolean equals(SetMixins other) {
            return Operations.equals(this.nodeId, other.nodeId) && Arrays.equals(this.mixinNodeTypeNames, other.mixinNodeTypeNames);
        }

        public int hashCode() {
            return 41 * (41 + Operations.hashCode(this.nodeId)) + Operations.hashCode(this.mixinNodeTypeNames);
        }
    }

    public static class ReorderNodes
    implements Operation {
        protected final NodeId parentId;
        protected final NodeId srcNodeId;
        protected final NodeId beforeNodeId;

        public ReorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
            this.parentId = parentId;
            this.srcNodeId = srcNodeId;
            this.beforeNodeId = beforeNodeId;
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            batch.reorderNodes(this.parentId, this.srcNodeId, this.beforeNodeId);
        }

        public String toString() {
            return "ReorderNodes[" + this.parentId + ", " + this.srcNodeId + ", " + this.beforeNodeId + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof ReorderNodes) {
                return this.equals((ReorderNodes)other);
            }
            return false;
        }

        public boolean equals(ReorderNodes other) {
            return Operations.equals(this.parentId, other.parentId) && Operations.equals(this.srcNodeId, other.srcNodeId) && Operations.equals(this.beforeNodeId, other.beforeNodeId);
        }

        public int hashCode() {
            return 41 * (41 * (41 + Operations.hashCode(this.parentId)) + Operations.hashCode(this.srcNodeId)) + Operations.hashCode(this.beforeNodeId);
        }
    }

    public static class Remove
    implements Operation {
        protected final ItemId itemId;

        public Remove(ItemId itemId) {
            this.itemId = itemId;
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            batch.remove(this.itemId);
        }

        public String toString() {
            return "Remove[" + this.itemId + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof Remove) {
                return this.equals((Remove)other);
            }
            return false;
        }

        public boolean equals(Remove other) {
            return Operations.equals(this.itemId, other.itemId);
        }

        public int hashCode() {
            return 41 + Operations.hashCode(this.itemId);
        }
    }

    public static class Move
    implements Operation {
        protected final NodeId srcNodeId;
        protected final NodeId destParentNodeId;
        protected final Name destName;

        public Move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
            this.srcNodeId = srcNodeId;
            this.destParentNodeId = destParentNodeId;
            this.destName = destName;
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            batch.move(this.srcNodeId, this.destParentNodeId, this.destName);
        }

        public String toString() {
            return "Move[" + this.srcNodeId + ", " + this.destParentNodeId + ", " + this.destName + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof Move) {
                return this.equals((Move)other);
            }
            return false;
        }

        public boolean equals(Move other) {
            return Operations.equals(this.srcNodeId, other.srcNodeId) && Operations.equals(this.destParentNodeId, other.destParentNodeId) && Operations.equals(this.destName, other.destName);
        }

        public int hashCode() {
            return 41 * (41 * (41 + Operations.hashCode(this.srcNodeId)) + Operations.hashCode(this.destParentNodeId)) + Operations.hashCode(this.destName);
        }
    }

    public static class AddProperty
    implements Operation {
        protected final NodeId parentId;
        protected final Name propertyName;
        protected final QValue[] values;
        protected final boolean isMultivalued;

        private AddProperty(NodeId parentId, Name propertyName, QValue[] values, boolean isMultivalued) {
            this.parentId = parentId;
            this.propertyName = propertyName;
            this.values = values;
            this.isMultivalued = isMultivalued;
        }

        public AddProperty(NodeId parentId, Name propertyName, QValue value) {
            this(parentId, propertyName, new QValue[]{value}, false);
        }

        public AddProperty(NodeId parentId, Name propertyName, QValue[] values) {
            this(parentId, propertyName, values, true);
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            if (this.isMultivalued) {
                batch.addProperty(this.parentId, this.propertyName, this.values);
            } else {
                batch.addProperty(this.parentId, this.propertyName, this.values[0]);
            }
        }

        public String toString() {
            return "AddProperty[" + this.parentId + ", " + this.propertyName + ", " + Arrays.toString(this.values) + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof AddProperty) {
                return this.equals((AddProperty)other);
            }
            return false;
        }

        public boolean equals(AddProperty other) {
            return Operations.equals(this.parentId, other.parentId) && Operations.equals(this.propertyName, other.propertyName) && this.isMultivalued == other.isMultivalued && Arrays.equals(this.values, other.values);
        }

        public int hashCode() {
            return 41 * (41 * (41 + Operations.hashCode(this.parentId)) + Operations.hashCode(this.propertyName)) + Operations.hashCode(this.values);
        }
    }

    public static class AddNode
    implements Operation {
        protected final NodeId parentId;
        protected final Name nodeName;
        protected final Name nodetypeName;
        protected final String uuid;

        public AddNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
            this.parentId = parentId;
            this.nodeName = nodeName;
            this.nodetypeName = nodetypeName;
            this.uuid = uuid;
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
            batch.addNode(this.parentId, this.nodeName, this.nodetypeName, this.uuid);
        }

        public String toString() {
            return "AddNode[" + this.parentId + ", " + this.nodeName + ", " + this.nodetypeName + ", " + this.uuid + "]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof AddNode) {
                return this.equals((AddNode)other);
            }
            return false;
        }

        public boolean equals(AddNode other) {
            return Operations.equals(this.parentId, other.parentId) && Operations.equals(this.nodeName, other.nodeName) && Operations.equals(this.nodetypeName, other.nodetypeName) && Operations.equals(this.uuid, other.uuid);
        }

        public int hashCode() {
            return 41 * (41 * (41 * (41 + Operations.hashCode(this.parentId)) + Operations.hashCode(this.nodeName)) + Operations.hashCode(this.nodetypeName)) + Operations.hashCode(this.uuid);
        }
    }

    public static class Empty
    implements Operation {
        private static final Empty INSTANCE = new Empty();

        protected Empty() {
        }

        @Override
        public void apply(Batch batch) throws RepositoryException {
        }

        public String toString() {
            return "Empty[]";
        }

        public boolean equals(Object other) {
            if (null == other) {
                return false;
            }
            return other instanceof Empty;
        }

        public int hashCode() {
            return Empty.class.hashCode();
        }
    }
}

