/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.flat.NodeSequence;
import org.apache.jackrabbit.commons.flat.PropertySequence;
import org.apache.jackrabbit.commons.flat.TreeManager;
import org.apache.jackrabbit.commons.flat.TreeTraverser;

public abstract class ItemSequence {
    protected final TreeManager treeManager;
    protected final TreeTraverser.ErrorHandler errorHandler;
    protected final Node root;
    protected final Comparator<String> order;
    protected final boolean autoSave;

    protected ItemSequence(TreeManager treeManager, TreeTraverser.ErrorHandler errorHandler) {
        if (treeManager == null) {
            throw new IllegalArgumentException("tree manager must not be null");
        }
        if (treeManager.getRoot() == null) {
            throw new IllegalArgumentException("root must not be null");
        }
        if (treeManager.getOrder() == null) {
            throw new IllegalArgumentException("order must not be null");
        }
        this.treeManager = treeManager;
        this.errorHandler = errorHandler;
        this.root = treeManager.getRoot();
        this.order = treeManager.getOrder();
        this.autoSave = treeManager.getAutoSave();
    }

    public static NodeSequence createNodeSequence(TreeManager treeManager, TreeTraverser.ErrorHandler errorHandler) {
        return new NodeSequenceImpl(treeManager, errorHandler);
    }

    public static NodeSequence createNodeSequence(TreeManager treeManager) {
        return new NodeSequenceImpl(treeManager, TreeTraverser.ErrorHandler.IGNORE);
    }

    public static PropertySequence createPropertySequence(TreeManager treeManager, TreeTraverser.ErrorHandler errorHandler) {
        return new PropertySequenceImpl(treeManager, errorHandler);
    }

    public static PropertySequence createPropertySequence(TreeManager treeManager) {
        return new PropertySequenceImpl(treeManager, TreeTraverser.ErrorHandler.IGNORE);
    }

    public NodeSequence getNodeSequence() {
        return new NodeSequenceImpl(this.treeManager, this.errorHandler);
    }

    public PropertySequence getPropertySequence() {
        return new PropertySequenceImpl(this.treeManager, this.errorHandler);
    }

    protected abstract Node getParent(String var1) throws RepositoryException;

    protected final Node getPredecessor(String key) throws RepositoryException {
        Node n;
        Node p = this.root;
        while ((n = this.getPredecessor(p, key)) != null) {
            p = n;
        }
        return p;
    }

    protected final Node getPredecessor(Node node, String key) throws RepositoryException {
        if (!node.hasNodes() || this.treeManager.isLeaf(node)) {
            return null;
        }
        try {
            return node.getNode(key);
        }
        catch (PathNotFoundException pathNotFoundException) {
            NodeIterator childNodes = node.getNodes();
            Node p = null;
            while (childNodes.hasNext()) {
                Node n = childNodes.nextNode();
                String childKey = n.getName();
                if (this.order.compare(key, childKey) <= 0 || p != null && this.order.compare(childKey, p.getName()) <= 0) continue;
                p = n;
            }
            return p;
        }
    }

    protected final Node getSuccessor(Node node, String key) throws RepositoryException {
        if (!node.hasNodes() || this.treeManager.isLeaf(node)) {
            return null;
        }
        try {
            return node.getNode(key);
        }
        catch (PathNotFoundException pathNotFoundException) {
            NodeIterator childNodes = node.getNodes();
            Node s = null;
            while (childNodes.hasNext()) {
                Node n = childNodes.nextNode();
                String childKey = n.getName();
                if (this.order.compare(key, childKey) >= 0 || s != null && this.order.compare(childKey, s.getName()) >= 0) continue;
                s = n;
            }
            return s;
        }
    }

    protected final Node getMinimal() throws RepositoryException {
        Node p = null;
        Node n = this.root;
        while ((n = this.getMinimal(n)) != null) {
            p = n;
        }
        return p;
    }

    protected final Node getMinimal(Node node) throws RepositoryException {
        if (!node.hasNodes() || this.treeManager.isLeaf(node)) {
            return null;
        }
        NodeIterator childNodes = node.getNodes();
        Node p = childNodes.nextNode();
        String minKey = p.getName();
        while (childNodes.hasNext()) {
            Node n = childNodes.nextNode();
            if (this.order.compare(n.getName(), minKey) >= 0) continue;
            p = n;
            minKey = p.getName();
        }
        return p;
    }

    protected final void renamePath(Node node, String key) throws RepositoryException {
        if (!this.treeManager.isRoot(node)) {
            Node p = node.getParent();
            this.renamePath(p, key);
            Session s = node.getSession();
            s.move(node.getPath(), p.getPath() + "/" + key);
            if (p.getPrimaryNodeType().hasOrderableChildNodes()) {
                p.orderBefore(key, p.getNodes().nextNode().getName());
            }
        }
    }

    protected static class PropertySequenceImpl
    extends ItemSequence
    implements PropertySequence {
        private final TreeTraverser.InclusionPolicy<Property> inclusionPolicy = new TreeTraverser.InclusionPolicy<Property>(){
            private final Set<String> ignoredProperties;
            {
                this.ignoredProperties = treeManager.getIgnoredProperties();
            }

            @Override
            public boolean include(Property property) {
                try {
                    return !this.ignoredProperties.contains(property.getName());
                }
                catch (RepositoryException e) {
                    return false;
                }
            }
        };

        public PropertySequenceImpl(TreeManager treeManager, TreeTraverser.ErrorHandler errorHandler) {
            super(treeManager, errorHandler);
        }

        @Override
        public Iterator<Property> iterator() {
            return TreeTraverser.propertyIterator(this.getNodeSequence().iterator(), this.errorHandler, this.inclusionPolicy);
        }

        @Override
        public Property getItem(String key) throws RepositoryException {
            return this.getParent(key).getProperty(key);
        }

        @Override
        public boolean hasItem(String key) throws RepositoryException {
            return this.getParent(key).hasProperty(key);
        }

        @Override
        public Property addProperty(String key, Value value) throws RepositoryException {
            Node parent = this.getOrCreateParent(key);
            if (parent.hasProperty(key)) {
                throw new ItemExistsException(key);
            }
            Property p = parent.setProperty(key, value);
            this.treeManager.split((ItemSequence)this, parent, p);
            if (this.autoSave) {
                p.getSession().save();
            }
            return p;
        }

        @Override
        public void removeProperty(String key) throws RepositoryException {
            Node parent = this.getParent(key);
            Property p = parent.getProperty(key);
            p.remove();
            this.treeManager.join((ItemSequence)this, parent, p);
            if (this.autoSave) {
                parent.getSession().save();
            }
        }

        @Override
        public Node getParent(String key) throws RepositoryException {
            return this.getPredecessor(key);
        }

        private Node getOrCreateParent(String key) throws RepositoryException {
            Node min;
            Node p = this.getParent(key);
            if (this.treeManager.isRoot(p) && (min = this.getMinimal()) != null) {
                p = min;
                this.renamePath(p, key);
            }
            return p;
        }
    }

    protected static class NodeSequenceImpl
    extends ItemSequence
    implements NodeSequence {
        private final TreeTraverser.InclusionPolicy<Node> inclusionPolicy = new TreeTraverser.InclusionPolicy<Node>(){

            @Override
            public boolean include(Node node) {
                try {
                    return treeManager.isLeaf(node);
                }
                catch (RepositoryException e) {
                    return false;
                }
            }
        };

        public NodeSequenceImpl(TreeManager treeManager, TreeTraverser.ErrorHandler errorHandler) {
            super(treeManager, errorHandler);
        }

        @Override
        public Iterator<Node> iterator() {
            return TreeTraverser.nodeIterator(this.root, this.errorHandler, this.inclusionPolicy);
        }

        @Override
        public Node getItem(String key) throws RepositoryException {
            return this.getParent(key).getNode(key);
        }

        @Override
        public boolean hasItem(String key) throws RepositoryException {
            return this.getParent(key).hasNode(key);
        }

        @Override
        public Node addNode(String key, String primaryNodeTypeName) throws RepositoryException {
            Node n;
            Node parent = this.getOrCreateParent(key);
            if (parent.hasNode(key)) {
                throw new ItemExistsException(key);
            }
            if (parent.getPrimaryNodeType().hasOrderableChildNodes()) {
                Node dest = this.getSuccessor(parent, key);
                n = parent.addNode(key, primaryNodeTypeName);
                parent.orderBefore(key, dest == null ? null : dest.getName());
            } else {
                n = parent.addNode(key, primaryNodeTypeName);
            }
            this.treeManager.split((ItemSequence)this, parent, n);
            if (this.autoSave) {
                parent.getSession().save();
            }
            return n;
        }

        @Override
        public void removeNode(String key) throws RepositoryException {
            Node parent = this.getParent(key);
            Node n = parent.getNode(key);
            n.remove();
            this.treeManager.join((ItemSequence)this, parent, n);
            if (this.autoSave) {
                parent.getSession().save();
            }
        }

        @Override
        public Node getParent(String key) throws RepositoryException {
            Node p = this.getPredecessor(key);
            if (this.treeManager.isLeaf(p) && !this.treeManager.isRoot(p)) {
                return p.getParent();
            }
            return p;
        }

        private Node getOrCreateParent(String key) throws RepositoryException {
            Node min;
            Node p = this.getParent(key);
            if (this.treeManager.isRoot(p) && (min = this.getMinimal()) != null) {
                p = min.getParent();
                this.renamePath(p, key);
            }
            return p;
        }
    }
}

