/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.identifier;

import java.io.Serializable;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.util.Text;

public abstract class AbstractIdFactory
implements IdFactory {
    private static final char DELIMITER = '@';

    @Override
    public NodeId createNodeId(NodeId parentId, Path path) {
        try {
            return new NodeIdImpl(parentId, path, this.getPathFactory());
        }
        catch (RepositoryException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public NodeId createNodeId(String uniqueID, Path path) {
        return new NodeIdImpl(uniqueID, path);
    }

    @Override
    public NodeId createNodeId(String uniqueID) {
        return new NodeIdImpl(uniqueID);
    }

    @Override
    public PropertyId createPropertyId(NodeId parentId, Name propertyName) {
        try {
            return new PropertyIdImpl(parentId, propertyName, this.getPathFactory());
        }
        catch (RepositoryException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public String toJcrIdentifier(NodeId nodeId) {
        String uniqueId = nodeId.getUniqueID();
        Path path = nodeId.getPath();
        if (path == null) {
            return uniqueId;
        }
        if (uniqueId == null) {
            return '@' + path.toString();
        }
        StringBuffer bf = new StringBuffer();
        bf.append(Text.escape(uniqueId, '@'));
        bf.append('@');
        bf.append(path.toString());
        return bf.toString();
    }

    @Override
    public NodeId fromJcrIdentifier(String jcrIdentifier) {
        int pos = jcrIdentifier.indexOf(64);
        switch (pos) {
            case -1: {
                return this.createNodeId(jcrIdentifier);
            }
            case 0: {
                return this.createNodeId((String)null, this.getPathFactory().create(jcrIdentifier.substring(1)));
            }
        }
        String uniqueId = Text.unescape(jcrIdentifier.substring(0, pos), '@');
        Path path = this.getPathFactory().create(jcrIdentifier.substring(pos + 1));
        return this.createNodeId(uniqueId, path);
    }

    protected abstract PathFactory getPathFactory();

    private static class PropertyIdImpl
    extends ItemIdImpl
    implements PropertyId,
    Serializable {
        private static final long serialVersionUID = -1953124047770776444L;
        private final NodeId parentId;

        private PropertyIdImpl(NodeId parentId, Name name, PathFactory factory) throws RepositoryException {
            super(parentId, name, factory);
            this.parentId = parentId;
        }

        @Override
        public boolean denotesNode() {
            return false;
        }

        @Override
        public NodeId getParentId() {
            return this.parentId;
        }

        @Override
        public Name getName() {
            return this.getPath().getName();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof PropertyId) {
                return super.equals((PropertyId)obj);
            }
            return false;
        }
    }

    private static class NodeIdImpl
    extends ItemIdImpl
    implements NodeId {
        private static final long serialVersionUID = -360276648861146631L;

        public NodeIdImpl(String uniqueID) {
            super(uniqueID, null);
        }

        public NodeIdImpl(String uniqueID, Path path) {
            super(uniqueID, path);
        }

        public NodeIdImpl(NodeId parentId, Path path, PathFactory factory) throws RepositoryException {
            super(parentId.getUniqueID(), parentId.getPath() != null ? factory.create(parentId.getPath(), path, true) : path);
        }

        @Override
        public boolean denotesNode() {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof NodeId) {
                return super.equals((NodeId)obj);
            }
            return false;
        }
    }

    private static abstract class ItemIdImpl
    implements ItemId,
    Serializable {
        private final String uniqueID;
        private final Path path;
        private transient int hashCode = 0;

        private ItemIdImpl(String uniqueID, Path path) {
            if (uniqueID == null && path == null) {
                throw new IllegalArgumentException("Only uniqueID or relative path might be null.");
            }
            this.uniqueID = uniqueID;
            this.path = path;
        }

        private ItemIdImpl(NodeId parentId, Name name, PathFactory factory) throws RepositoryException {
            if (parentId == null || name == null) {
                throw new IllegalArgumentException("Invalid ItemIdImpl: parentId and name must not be null.");
            }
            this.uniqueID = parentId.getUniqueID();
            Path parentPath = parentId.getPath();
            this.path = parentPath != null ? factory.create(parentPath, name, true) : factory.create(name);
        }

        @Override
        public abstract boolean denotesNode();

        @Override
        public String getUniqueID() {
            return this.uniqueID;
        }

        @Override
        public Path getPath() {
            return this.path;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ItemId) {
                ItemId other = (ItemId)obj;
                return this.equals(other);
            }
            return false;
        }

        boolean equals(ItemId other) {
            return (this.uniqueID == null ? other.getUniqueID() == null : this.uniqueID.equals(other.getUniqueID())) && (this.path == null ? other.getPath() == null : this.path.equals(other.getPath()));
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                int result = 17;
                result = 37 * result + (this.uniqueID != null ? this.uniqueID.hashCode() : 0);
                this.hashCode = result = 37 * result + (this.path != null ? this.path.hashCode() : 0);
            }
            return this.hashCode;
        }

        public String toString() {
            StringBuffer b = new StringBuffer();
            if (this.uniqueID != null) {
                b.append(this.uniqueID);
            }
            if (this.path != null) {
                b.append(this.path.toString());
            }
            return b.toString();
        }
    }
}

