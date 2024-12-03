/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.ChildInfo;
import org.apache.jackrabbit.spi.ItemInfo;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.NodeInfo;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.PropertyInfo;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.ChildInfoImpl;
import org.apache.jackrabbit.spi.commons.NodeInfoImpl;
import org.apache.jackrabbit.spi.commons.PropertyInfoImpl;
import org.apache.jackrabbit.spi.commons.identifier.IdFactoryImpl;
import org.apache.jackrabbit.spi.commons.iterator.Iterators;
import org.apache.jackrabbit.spi.commons.iterator.Transformer;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.apache.jackrabbit.spi.commons.value.QValueFactoryImpl;

public final class ItemInfoBuilder {
    private ItemInfoBuilder() {
    }

    public static NodeInfoBuilder nodeInfoBuilder(Listener listener) {
        return ItemInfoBuilder.nodeInfoBuilder("", listener);
    }

    public static NodeInfoBuilder nodeInfoBuilder() {
        return ItemInfoBuilder.nodeInfoBuilder("", null);
    }

    public static NodeInfoBuilder nodeInfoBuilder(String localName) {
        return ItemInfoBuilder.nodeInfoBuilder(localName, null);
    }

    public static NodeInfoBuilder nodeInfoBuilder(String localName, Listener listener) {
        return new NodeInfoBuilder(null, localName, listener);
    }

    public static NodeInfoBuilder nodeInfoBuilder(Name name, Listener listener) {
        return new NodeInfoBuilder(null, name, listener);
    }

    public static class PropertyInfoBuilder {
        private final NodeInfoBuilder parent;
        private final Listener listener;
        private Name name;
        private String localName;
        private String namespace;
        private final List<QValue> values = new ArrayList<QValue>();
        private int type = 0;
        private boolean isMultivalued = true;
        private boolean stale;
        private PropertyInfo propertyInfo;

        private PropertyInfoBuilder(NodeInfoBuilder nodeInfoBuilder, String localName, Listener listener) {
            this.parent = nodeInfoBuilder;
            this.localName = localName;
            this.listener = listener;
        }

        public PropertyInfoBuilder setName(Name name) {
            this.name = name;
            return this;
        }

        public PropertyInfoBuilder setName(String localName) {
            this.localName = localName;
            return this;
        }

        public PropertyInfoBuilder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public PropertyInfoBuilder setType(int type) {
            if (this.values.size() > 0 && type != this.values.get(0).getType()) {
                throw new IllegalStateException("Type mismatch. Required " + PropertyType.nameFromValue(this.values.get(0).getType()) + " found " + PropertyType.nameFromValue(type));
            }
            this.type = type;
            return this;
        }

        public PropertyInfoBuilder addValue(QValue value) {
            int actualType = value.getType();
            if (this.type != 0 && this.type != actualType) {
                throw new IllegalStateException("Type mismatch. Required " + PropertyType.nameFromValue(this.type) + " found " + PropertyType.nameFromValue(value.getType()));
            }
            this.values.add(value);
            this.type = actualType;
            this.isMultivalued = this.values.size() != 1;
            return this;
        }

        public PropertyInfoBuilder addValue(String value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value, 1));
        }

        public PropertyInfoBuilder addValue(Calendar value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(double value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(long value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(boolean value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(Name value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(Path value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(BigDecimal value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(URI value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(byte[] value) throws RepositoryException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(InputStream value) throws RepositoryException, IOException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder addValue(File value) throws RepositoryException, IOException {
            return this.addValue(QValueFactoryImpl.getInstance().create(value));
        }

        public PropertyInfoBuilder setMultivalued(boolean on) {
            if (!on && this.values.size() != 1) {
                throw new IllegalStateException("Cannot create single valued property when multiple values are present");
            }
            this.isMultivalued = true;
            return this;
        }

        public NodeInfoBuilder build() throws RepositoryException {
            if (this.stale) {
                throw new IllegalStateException("Builder is stale");
            }
            if (this.type == 0) {
                throw new IllegalStateException("Type not set");
            }
            if (this.localName == null && this.name == null) {
                throw new IllegalStateException("Name not set");
            }
            this.stale = true;
            NodeId parentId = this.parent.getId();
            if (this.name == null) {
                String ns = this.namespace == null ? "" : this.namespace;
                this.name = NameFactoryImpl.getInstance().create(ns, this.localName);
            }
            Path path = PathFactoryImpl.getInstance().create(this.parent.getPath(), this.name, true);
            PropertyId id = IdFactoryImpl.getInstance().createPropertyId(parentId, this.name);
            this.propertyInfo = new PropertyInfoImpl(path, id, this.type, this.isMultivalued, this.values.toArray(new QValue[this.values.size()]));
            if (this.listener != null) {
                this.listener.createPropertyInfo(this.propertyInfo);
            }
            return this.parent.addPropertyInfo(this.propertyInfo);
        }

        public NodeInfoBuilder getParent() {
            return this.parent;
        }

        public PropertyInfo getPropertyInfo() {
            if (!this.stale) {
                throw new IllegalStateException("PropertyInfo not built yet");
            }
            return this.propertyInfo;
        }
    }

    public static class NodeInfoBuilder {
        private final NodeInfoBuilder parent;
        private final Listener listener;
        private Path parentPath;
        private String localName;
        private String namespace;
        private Name name;
        private int index = 1;
        private String uuid;
        private Name primaryTypeName = NameConstants.NT_UNSTRUCTURED;
        private final List<Name> mixins = new ArrayList<Name>();
        private boolean includeChildInfos = true;
        private boolean stale;
        private final List<ItemInfo> itemInfos = new ArrayList<ItemInfo>();
        private NodeInfo nodeInfo;

        private NodeInfoBuilder(NodeInfoBuilder nodeInfoBuilder, String localName, Listener listener) {
            this.parent = nodeInfoBuilder;
            this.localName = localName;
            this.listener = listener;
        }

        private NodeInfoBuilder(NodeInfoBuilder nodeInfoBuilder, Name name, Listener listener) {
            this.parent = nodeInfoBuilder;
            this.name = name;
            this.listener = listener;
        }

        public PropertyInfoBuilder createPropertyInfo(String localName, String value) throws RepositoryException {
            PropertyInfoBuilder pBuilder = new PropertyInfoBuilder(this, localName, this.listener);
            pBuilder.addValue(value);
            return pBuilder;
        }

        public PropertyInfoBuilder createPropertyInfo(String localName) {
            return new PropertyInfoBuilder(this, localName, this.listener);
        }

        public PropertyInfoBuilder createPropertyInfo() {
            return new PropertyInfoBuilder(this, null, this.listener);
        }

        public NodeInfoBuilder createNodeInfo(String localName) {
            return new NodeInfoBuilder(this, localName, this.listener);
        }

        public NodeInfoBuilder createNodeInfo() {
            return new NodeInfoBuilder(this, (String)null, this.listener);
        }

        public NodeInfoBuilder setName(Name name) {
            this.name = name;
            return this;
        }

        public NodeInfoBuilder setName(String localName) {
            this.localName = localName;
            return this;
        }

        public NodeInfoBuilder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public NodeInfoBuilder setIndex(int index) {
            this.index = index;
            return this;
        }

        public NodeInfoBuilder setUUID(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public NodeInfoBuilder setParentPath(Path parentPath) {
            this.parentPath = parentPath;
            return this;
        }

        public NodeInfoBuilder setPrimaryType(Name name) {
            this.primaryTypeName = name;
            return this;
        }

        public NodeInfoBuilder addMixin(Name name) {
            this.mixins.add(name);
            return this;
        }

        public NodeInfoBuilder includeChildInfos(boolean include) {
            this.includeChildInfos = include;
            return this;
        }

        public NodeInfoBuilder build() throws RepositoryException {
            if (this.stale) {
                throw new IllegalStateException("Builder is stale");
            }
            this.stale = true;
            NodeId id = this.getId();
            this.nodeInfo = new NodeInfoImpl(this.getPath(), id, this.index, this.primaryTypeName, this.mixins.toArray(new Name[this.mixins.size()]), Iterators.empty(), this.getPropertyIds(), this.includeChildInfos ? this.getChildInfos() : null);
            if (this.listener != null) {
                this.listener.createNodeInfo(this.nodeInfo);
                this.listener.createChildInfos(id, this.getChildInfos());
            }
            if (this.parent == null) {
                return this;
            }
            this.parent.addNodeInfo(this.nodeInfo);
            return this.parent;
        }

        public NodeInfoBuilder getParent() {
            return this.parent;
        }

        public NodeInfo getNodeInfo() {
            if (!this.stale) {
                throw new IllegalStateException("NodeInfo not built yet");
            }
            return this.nodeInfo;
        }

        public NodeInfoBuilder addPropertyInfo(PropertyInfo propertyInfo) {
            this.itemInfos.add(propertyInfo);
            return this;
        }

        public NodeInfoBuilder addNodeInfo(NodeInfo nodeInfo) {
            this.itemInfos.add(nodeInfo);
            return this;
        }

        private NodeId getId() throws RepositoryException {
            if (this.uuid == null) {
                return IdFactoryImpl.getInstance().createNodeId((String)null, this.getPath());
            }
            return IdFactoryImpl.getInstance().createNodeId(this.uuid);
        }

        private Path getPath() throws RepositoryException {
            Path path;
            if (this.localName == null && this.name == null) {
                throw new IllegalStateException("Name not set");
            }
            if (this.parent == null && this.parentPath == null) {
                return PathFactoryImpl.getInstance().getRootPath();
            }
            Path path2 = path = this.parentPath == null ? this.parent.getPath() : this.parentPath;
            if (this.name == null) {
                String ns = this.namespace == null ? "" : this.namespace;
                this.name = NameFactoryImpl.getInstance().create(ns, this.localName);
            }
            return PathFactoryImpl.getInstance().create(path, this.name, true);
        }

        private Iterator<ChildInfo> getChildInfos() {
            return Iterators.transformIterator(Iterators.filterIterator(this.itemInfos.iterator(), new Predicate<ItemInfo>(){

                @Override
                public boolean test(ItemInfo info) {
                    return info.denotesNode();
                }
            }), new Transformer<ItemInfo, ChildInfo>(){

                @Override
                public ChildInfo transform(ItemInfo info) {
                    return new ChildInfoImpl(info.getPath().getName(), null, 1);
                }
            });
        }

        private Iterator<PropertyId> getPropertyIds() {
            return Iterators.transformIterator(Iterators.filterIterator(this.itemInfos.iterator(), new Predicate<ItemInfo>(){

                @Override
                public boolean test(ItemInfo info) {
                    return !info.denotesNode();
                }
            }), new Transformer<ItemInfo, PropertyId>(){

                @Override
                public PropertyId transform(ItemInfo info) {
                    return (PropertyId)info.getId();
                }
            });
        }
    }

    public static interface Listener {
        public void createNodeInfo(NodeInfo var1);

        public void createChildInfos(NodeId var1, Iterator<ChildInfo> var2);

        public void createPropertyInfo(PropertyInfo var1);
    }
}

