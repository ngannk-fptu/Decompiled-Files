/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.io.InputStream;
import java.util.Calendar;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import org.apache.jackrabbit.commons.AbstractItem;
import org.apache.jackrabbit.commons.NamespaceHelper;

public abstract class AbstractNode
extends AbstractItem
implements Node {
    @Override
    public void accept(ItemVisitor visitor) throws RepositoryException {
        visitor.visit(this);
    }

    @Override
    public String getPath() throws RepositoryException {
        try {
            StringBuffer buffer = new StringBuffer(this.getParent().getPath());
            if (buffer.length() > 1) {
                buffer.append('/');
            }
            buffer.append(this.getName());
            int index = this.getIndex();
            if (index != 1) {
                buffer.append('[');
                buffer.append(index);
                buffer.append(']');
            }
            return buffer.toString();
        }
        catch (ItemNotFoundException e) {
            return "/";
        }
    }

    @Override
    public boolean isNode() {
        return true;
    }

    @Override
    public NodeType[] getMixinNodeTypes() throws RepositoryException {
        try {
            NodeTypeManager manager = this.getSession().getWorkspace().getNodeTypeManager();
            Property property = this.getProperty(this.getName("jcr:mixinTypes"));
            Value[] values = property.getValues();
            NodeType[] types = new NodeType[values.length];
            for (int i = 0; i < values.length; ++i) {
                types[i] = manager.getNodeType(values[i].getString());
            }
            return types;
        }
        catch (PathNotFoundException e) {
            return new NodeType[0];
        }
    }

    @Override
    public NodeType getPrimaryNodeType() throws RepositoryException {
        NodeTypeManager manager = this.getSession().getWorkspace().getNodeTypeManager();
        Property property = this.getProperty(this.getName("jcr:primaryType"));
        return manager.getNodeType(property.getString());
    }

    @Override
    public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException {
        while (relPath.endsWith("/.")) {
            relPath = relPath.substring(0, relPath.length() - 2);
        }
        Node node = this;
        int slash = relPath.lastIndexOf(47);
        if (slash == 0) {
            node = this.getSession().getRootNode();
            relPath = relPath.substring(1);
        } else if (slash > 0) {
            node = this.getNode(relPath.substring(0, slash));
            relPath = relPath.substring(slash + 1);
        }
        PropertyIterator properties = node.getProperties(relPath);
        while (properties.hasNext()) {
            Property property = (Property)properties.next();
            if (!relPath.equals(property.getName())) continue;
            return property;
        }
        throw new PathNotFoundException("Property not found: " + relPath);
    }

    @Override
    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException {
        if (this.isNodeType(this.getName("mix:referenceable"))) {
            return this.getProperty(this.getName("jcr:uuid")).getString();
        }
        throw new UnsupportedRepositoryOperationException("This node is not referenceable: " + this.getPath());
    }

    @Override
    public VersionHistory getVersionHistory() throws RepositoryException {
        return this.getBaseVersion().getContainingHistory();
    }

    @Override
    public boolean hasNode(String relPath) throws RepositoryException {
        try {
            this.getNode(relPath);
            return true;
        }
        catch (PathNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean hasNodes() throws RepositoryException {
        return this.getNodes().hasNext();
    }

    @Override
    public boolean hasProperties() throws RepositoryException {
        return this.getProperties().hasNext();
    }

    @Override
    public boolean hasProperty(String relPath) throws RepositoryException {
        try {
            this.getProperty(relPath);
            return true;
        }
        catch (PathNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean holdsLock() throws RepositoryException {
        try {
            return this.isSame(this.getLock().getNode());
        }
        catch (LockException e) {
            return false;
        }
    }

    @Override
    public boolean isCheckedOut() throws RepositoryException {
        if (this.isNodeType(this.getName("jcr:versionable"))) {
            return this.getProperty(this.getName("jcr:isCheckedOut")).getBoolean();
        }
        try {
            return this.getParent().isCheckedOut();
        }
        catch (ItemNotFoundException e) {
            return true;
        }
    }

    @Override
    public boolean isLocked() throws RepositoryException {
        try {
            this.getLock();
            return true;
        }
        catch (LockException e) {
            return false;
        }
    }

    @Override
    public boolean isNodeType(String name) throws RepositoryException {
        NodeType type = this.getPrimaryNodeType();
        if (name.equals(type.getName())) {
            return true;
        }
        NodeType[] supertypes = type.getSupertypes();
        for (int i = 0; i < supertypes.length; ++i) {
            if (!name.equals(supertypes[i].getName())) continue;
            return true;
        }
        NodeType[] mixins = this.getMixinNodeTypes();
        for (int i = 0; i < mixins.length; ++i) {
            if (name.equals(mixins[i].getName())) {
                return true;
            }
            supertypes = mixins[i].getSupertypes();
            for (int j = 0; j < supertypes.length; ++j) {
                if (!name.equals(supertypes[j].getName())) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void restore(String versionName, boolean removeExisting) throws RepositoryException {
        this.restore(this.getVersionHistory().getVersion(versionName), removeExisting);
    }

    @Override
    public void restore(Version version, boolean removeExisting) throws RepositoryException {
        this.restore(version, ".", removeExisting);
    }

    @Override
    public void restoreByLabel(String versionLabel, boolean removeExisting) throws RepositoryException {
        this.restore(this.getVersionHistory().getVersionByLabel(versionLabel), removeExisting);
    }

    @Override
    public Property setProperty(String name, String[] strings) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        Value[] values = new Value[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            values[i] = factory.createValue(strings[i]);
        }
        return this.setProperty(name, values);
    }

    @Override
    public Property setProperty(String name, String value) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        return this.setProperty(name, factory.createValue(value));
    }

    @Override
    public Property setProperty(String name, InputStream value) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        return this.setProperty(name, factory.createValue(value));
    }

    @Override
    public Property setProperty(String name, boolean value) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        return this.setProperty(name, factory.createValue(value));
    }

    @Override
    public Property setProperty(String name, double value) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        return this.setProperty(name, factory.createValue(value));
    }

    @Override
    public Property setProperty(String name, long value) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        return this.setProperty(name, factory.createValue(value));
    }

    @Override
    public Property setProperty(String name, Calendar value) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        return this.setProperty(name, factory.createValue(value));
    }

    @Override
    public Property setProperty(String name, Node value) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        return this.setProperty(name, factory.createValue(value));
    }

    @Override
    public Property setProperty(String name, Value value, int type) throws RepositoryException {
        if (value.getType() != type) {
            ValueFactory factory = this.getSession().getValueFactory();
            value = factory.createValue(value.getString(), type);
        }
        return this.setProperty(name, value);
    }

    @Override
    public Property setProperty(String name, Value[] values, int type) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        Value[] converted = new Value[values.length];
        for (int i = 0; i < values.length; ++i) {
            converted[i] = values[i].getType() != type ? factory.createValue(values[i].getString(), type) : values[i];
        }
        return this.setProperty(name, converted);
    }

    @Override
    public Property setProperty(String name, String[] strings, int type) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        Value[] values = new Value[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            values[i] = factory.createValue(strings[i], type);
        }
        return this.setProperty(name, values);
    }

    @Override
    public Property setProperty(String name, String value, int type) throws RepositoryException {
        ValueFactory factory = this.getSession().getValueFactory();
        return this.setProperty(name, factory.createValue(value, type));
    }

    private String getName(String name) throws RepositoryException {
        return new NamespaceHelper(this.getSession()).getJcrName(name);
    }
}

