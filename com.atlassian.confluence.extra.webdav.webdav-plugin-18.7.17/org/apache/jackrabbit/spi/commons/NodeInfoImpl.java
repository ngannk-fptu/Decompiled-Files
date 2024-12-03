/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.jackrabbit.spi.ChildInfo;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.NodeInfo;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.commons.ChildInfoImpl;
import org.apache.jackrabbit.spi.commons.ItemInfoImpl;

public class NodeInfoImpl
extends ItemInfoImpl
implements NodeInfo {
    private final NodeId id;
    private final int index;
    private final Name primaryTypeName;
    private final Name[] mixinNames;
    private final List<PropertyId> references;
    private final List<PropertyId> propertyIds;
    private final List<ChildInfo> childInfos;

    public static NodeInfo createSerializableNodeInfo(NodeInfo nodeInfo, final IdFactory idFactory) {
        if (nodeInfo instanceof Serializable) {
            return nodeInfo;
        }
        ArrayList<PropertyId> serRefs = new ArrayList<PropertyId>();
        for (PropertyId ref : nodeInfo.getReferences()) {
            NodeId parentId = ref.getParentId();
            parentId = idFactory.createNodeId(parentId.getUniqueID(), parentId.getPath());
            serRefs.add(idFactory.createPropertyId(parentId, ref.getName()));
        }
        NodeId nodeId = nodeInfo.getId();
        nodeId = idFactory.createNodeId(nodeId.getUniqueID(), nodeId.getPath());
        final Iterator<PropertyId> propIds = nodeInfo.getPropertyIds();
        final Iterator<ChildInfo> childInfos = nodeInfo.getChildInfos();
        return new NodeInfoImpl(nodeInfo.getPath(), nodeId, nodeInfo.getIndex(), nodeInfo.getNodetype(), nodeInfo.getMixins(), serRefs.iterator(), new Iterator<PropertyId>(){

            @Override
            public boolean hasNext() {
                return propIds.hasNext();
            }

            @Override
            public PropertyId next() {
                PropertyId propId = (PropertyId)propIds.next();
                NodeId parentId = propId.getParentId();
                idFactory.createNodeId(parentId.getUniqueID(), parentId.getPath());
                return idFactory.createPropertyId(parentId, propId.getName());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }, childInfos == null ? null : new Iterator<ChildInfo>(){

            @Override
            public boolean hasNext() {
                return childInfos.hasNext();
            }

            @Override
            public ChildInfo next() {
                ChildInfo cInfo = (ChildInfo)childInfos.next();
                if (cInfo instanceof Serializable) {
                    return cInfo;
                }
                return new ChildInfoImpl(cInfo.getName(), cInfo.getUniqueID(), cInfo.getIndex());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }

    public NodeInfoImpl(NodeId parentId, Name name, Path path, NodeId id, int index, Name primaryTypeName, Name[] mixinNames, Iterator<PropertyId> references, Iterator<PropertyId> propertyIds, Iterator<ChildInfo> childInfos) {
        this(path, id, index, primaryTypeName, mixinNames, references, propertyIds, childInfos);
    }

    public NodeInfoImpl(Path path, NodeId id, int index, Name primaryTypeName, Name[] mixinNames, Iterator<PropertyId> references, Iterator<PropertyId> propertyIds, Iterator<ChildInfo> childInfos) {
        super(path, true);
        this.id = id;
        this.index = index;
        this.primaryTypeName = primaryTypeName;
        this.mixinNames = mixinNames;
        if (!references.hasNext()) {
            this.references = Collections.emptyList();
        } else {
            this.references = new ArrayList<PropertyId>();
            while (references.hasNext()) {
                this.references.add(references.next());
            }
        }
        this.propertyIds = new ArrayList<PropertyId>();
        while (propertyIds.hasNext()) {
            this.propertyIds.add(propertyIds.next());
        }
        if (childInfos == null) {
            this.childInfos = null;
        } else {
            this.childInfos = new ArrayList<ChildInfo>();
            while (childInfos.hasNext()) {
                this.childInfos.add(childInfos.next());
            }
        }
    }

    @Override
    public NodeId getId() {
        return this.id;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public Name getNodetype() {
        return this.primaryTypeName;
    }

    @Override
    public Name[] getMixins() {
        return this.mixinNames;
    }

    @Override
    public PropertyId[] getReferences() {
        return this.references.toArray(new PropertyId[this.references.size()]);
    }

    @Override
    public Iterator<PropertyId> getPropertyIds() {
        return this.propertyIds.iterator();
    }

    @Override
    public Iterator<ChildInfo> getChildInfos() {
        return this.childInfos == null ? null : this.childInfos.iterator();
    }
}

