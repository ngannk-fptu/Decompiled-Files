/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.flat.ItemSequence;
import org.apache.jackrabbit.commons.flat.Rank;
import org.apache.jackrabbit.commons.flat.SizedIterator;
import org.apache.jackrabbit.commons.flat.TreeManager;
import org.apache.jackrabbit.commons.iterator.FilterIterator;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class BTreeManager
implements TreeManager {
    private final Node root;
    private final int minChildren;
    private final int maxChildren;
    private final Comparator<String> order;
    private final boolean autoSave;
    private final Comparator<Item> itemOrder;
    private final Set<String> ignoredProperties = new HashSet<String>(Arrays.asList("jcr:primaryType", "jcr:mixinTypes"));

    public BTreeManager(Node root, int minChildren, int maxChildren, Comparator<String> order, boolean autoSave) throws RepositoryException {
        if (root == null) {
            throw new IllegalArgumentException("root must not be null");
        }
        if (minChildren <= 0) {
            throw new IllegalArgumentException("minChildren must be positive");
        }
        if (2 * minChildren > maxChildren) {
            throw new IllegalArgumentException("maxChildren must be at least twice minChildren");
        }
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }
        this.root = root;
        this.minChildren = minChildren;
        this.maxChildren = maxChildren;
        this.order = order;
        this.autoSave = autoSave;
        this.itemOrder = new Comparator<Item>(){

            @Override
            public int compare(Item i1, Item i2) {
                try {
                    return BTreeManager.this.order.compare(i1.getName(), i2.getName());
                }
                catch (RepositoryException e) {
                    throw new WrappedRepositoryException(e);
                }
            }
        };
    }

    @Override
    public Set<String> getIgnoredProperties() {
        return this.ignoredProperties;
    }

    @Override
    public void split(ItemSequence itemSequence, Node node, Node cause) throws RepositoryException {
        SizedIterator<Node> childNodes = this.getNodes(node);
        int count = (int)childNodes.getSize();
        if (count >= 0 && count <= this.maxChildren) {
            return;
        }
        this.split(node, new Rank<Item>(childNodes, Node.class, count, this.itemOrder), itemSequence);
    }

    @Override
    public void split(ItemSequence itemSequence, Node node, Property cause) throws RepositoryException {
        SizedIterator<Property> properties = this.getProperties(node);
        int count = (int)properties.getSize();
        if (count >= 0 && count <= this.maxChildren) {
            return;
        }
        this.split(node, new Rank<Item>(properties, Property.class, count, this.itemOrder), itemSequence);
    }

    @Override
    public void join(ItemSequence itemSequence, Node node, Node cause) throws RepositoryException {
        SizedIterator<Node> nodes = this.getNodes(node);
        long count = nodes.getSize();
        if (count < 0L) {
            count = 0L;
            while (nodes.hasNext()) {
                nodes.next();
                ++count;
            }
        }
        if (count == 0L) {
            this.removeRec(node);
        }
    }

    @Override
    public void join(ItemSequence itemSequence, Node node, Property cause) throws RepositoryException {
        SizedIterator<Property> properties = this.getProperties(node);
        long count = properties.getSize();
        if (count < 0L) {
            count = 0L;
            while (properties.hasNext()) {
                properties.next();
                ++count;
            }
        }
        if (count == 0L) {
            this.removeRec(node);
        }
    }

    @Override
    public Node getRoot() {
        return this.root;
    }

    @Override
    public boolean isRoot(Node node) throws RepositoryException {
        return node.isSame(this.root);
    }

    @Override
    public boolean isLeaf(Node node) throws RepositoryException {
        return !node.hasNodes();
    }

    @Override
    public Comparator<String> getOrder() {
        return this.order;
    }

    @Override
    public boolean getAutoSave() {
        return this.autoSave;
    }

    protected SizedIterator<Node> getNodes(Node node) throws RepositoryException {
        NodeIterator nodes = node.getNodes();
        return this.getSizedIterator(BTreeManager.convert(nodes), nodes.getSize());
    }

    protected SizedIterator<Property> getProperties(Node node) throws RepositoryException {
        PropertyIterator properties = node.getProperties();
        long size = properties.getSize();
        Iterator<String> ignored = this.ignoredProperties.iterator();
        while (size > 0L && ignored.hasNext()) {
            if (!node.hasProperty(ignored.next())) continue;
            --size;
        }
        return this.getSizedIterator(this.filterProperties(BTreeManager.convert(properties)), size);
    }

    protected Node createIntermediateNode(Node parent, String name) throws RepositoryException {
        return parent.addNode(name);
    }

    protected void move(Node node, Node parent) throws RepositoryException {
        String oldPath = node.getPath();
        String newPath = parent.getPath() + "/" + node.getName();
        node.getSession().move(oldPath, newPath);
    }

    protected void move(Property property, Node parent) throws RepositoryException {
        parent.setProperty(property.getName(), property.getValue());
        property.remove();
    }

    protected final <T> SizedIterator<T> getSizedIterator(final Iterator<T> iterator, final long size) {
        return new SizedIterator<T>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public long getSize() {
                return size;
            }
        };
    }

    private static Iterator<Property> convert(PropertyIterator it) {
        return it;
    }

    private static Iterator<Node> convert(NodeIterator it) {
        return it;
    }

    private <T extends Item> void split(Node node, Rank<T> ranking, ItemSequence itemSequence) throws RepositoryException {
        if (ranking.size() <= this.maxChildren) {
            return;
        }
        try {
            Node grandParent;
            if (this.isRoot(node)) {
                grandParent = node;
            } else {
                grandParent = node.getParent();
                ranking.take(this.minChildren);
            }
            for (int k = ranking.size() / this.minChildren; k > 0; --k) {
                Node newParent;
                Item item = (Item)ranking.take(1).next();
                String key = item.getName();
                if (grandParent.getPrimaryNodeType().hasOrderableChildNodes()) {
                    Node dest = itemSequence.getSuccessor(grandParent, key);
                    newParent = this.createIntermediateNode(grandParent, key);
                    grandParent.orderBefore(key, dest == null ? null : dest.getName());
                } else {
                    newParent = this.createIntermediateNode(grandParent, key);
                }
                this.move(item, newParent);
                int c = k > 1 ? this.minChildren - 1 : ranking.size();
                Iterator<T> remaining = ranking.take(c);
                while (remaining.hasNext()) {
                    this.move((Item)remaining.next(), newParent);
                }
            }
            if (!node.isSame(this.root)) {
                this.split(itemSequence, grandParent, (Node)null);
            }
        }
        catch (WrappedRepositoryException e) {
            throw e.wrapped();
        }
    }

    private <T extends Item> void move(T item, Node parent) throws RepositoryException {
        if (item.isNode()) {
            this.move((Node)item, parent);
        } else {
            this.move((Property)item, parent);
        }
    }

    private void removeRec(Node node) throws RepositoryException {
        Node n = node;
        while (!n.hasNodes() && !this.isRoot(n)) {
            Node d = n;
            n = n.getParent();
            d.remove();
        }
    }

    private Iterator<Property> filterProperties(Iterator<Property> properties) {
        return new FilterIterator<Property>(properties, new Predicate(){

            @Override
            public boolean evaluate(Object object) {
                try {
                    Property p = (Property)object;
                    return !BTreeManager.this.ignoredProperties.contains(p.getName());
                }
                catch (RepositoryException ignore) {
                    return true;
                }
            }
        });
    }

    private static class WrappedRepositoryException
    extends RuntimeException {
        private final RepositoryException wrapped;

        public WrappedRepositoryException(RepositoryException e) {
            this.wrapped = e;
        }

        public RepositoryException wrapped() {
            return this.wrapped;
        }
    }
}

