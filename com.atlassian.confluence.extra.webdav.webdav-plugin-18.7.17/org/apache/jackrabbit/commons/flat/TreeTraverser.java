/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import java.util.Collections;
import java.util.Iterator;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.iterator.FilterIterator;
import org.apache.jackrabbit.commons.iterator.LazyIteratorChain;
import org.apache.jackrabbit.commons.predicate.Predicate;

public final class TreeTraverser
implements Iterable<Node> {
    private final Node root;
    private final ErrorHandler errorHandler;
    private final InclusionPolicy<? super Node> inclusionPolicy;

    public TreeTraverser(Node root, ErrorHandler errorHandler, InclusionPolicy<? super Node> inclusionPolicy) {
        this.root = root;
        this.errorHandler = errorHandler == null ? ErrorHandler.IGNORE : errorHandler;
        this.inclusionPolicy = inclusionPolicy;
    }

    public TreeTraverser(Node root) {
        this(root, ErrorHandler.IGNORE, InclusionPolicy.ALL);
    }

    public static Iterator<Node> nodeIterator(Node root, ErrorHandler errorHandler, InclusionPolicy<? super Node> inclusionPolicy) {
        return new TreeTraverser(root, errorHandler, inclusionPolicy).iterator();
    }

    public static Iterator<Node> nodeIterator(Node root) {
        return TreeTraverser.nodeIterator(root, ErrorHandler.IGNORE, InclusionPolicy.ALL);
    }

    public static Iterator<Property> propertyIterator(Iterator<Node> nodes, ErrorHandler errorHandler, InclusionPolicy<? super Property> inclusionPolicy) {
        return TreeTraverser.filter(LazyIteratorChain.chain(TreeTraverser.propertyIterators(nodes, errorHandler)), inclusionPolicy);
    }

    public static Iterator<Property> propertyIterator(Iterator<Node> nodes) {
        return TreeTraverser.propertyIterator(nodes, ErrorHandler.IGNORE, InclusionPolicy.ALL);
    }

    public static Iterator<Property> propertyIterator(Node root, ErrorHandler errorHandler, InclusionPolicy<Item> inclusionPolicy) {
        return TreeTraverser.propertyIterator(TreeTraverser.nodeIterator(root, errorHandler, inclusionPolicy), errorHandler, inclusionPolicy);
    }

    public static Iterator<Property> propertyIterator(Node root) {
        return TreeTraverser.propertyIterator(root, ErrorHandler.IGNORE, InclusionPolicy.ALL);
    }

    @Override
    public Iterator<Node> iterator() {
        return this.iterator(this.root);
    }

    private Iterator<Node> iterator(Node node) {
        if (this.inclusionPolicy.include(node)) {
            return LazyIteratorChain.chain(this.singleton(node), LazyIteratorChain.chain(this.childIterators(node)));
        }
        return LazyIteratorChain.chain(this.childIterators(node));
    }

    private Iterator<Iterator<Node>> childIterators(Node node) {
        try {
            final NodeIterator childNodes = node.getNodes();
            return new Iterator<Iterator<Node>>(){

                @Override
                public boolean hasNext() {
                    return childNodes.hasNext();
                }

                @Override
                public Iterator<Node> next() {
                    return TreeTraverser.this.iterator(childNodes.nextNode());
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        catch (RepositoryException e) {
            this.errorHandler.call(node, e);
            return TreeTraverser.empty();
        }
    }

    private static Iterator<Iterator<Property>> propertyIterators(final Iterator<Node> nodes, final ErrorHandler errorHandler) {
        return new Iterator<Iterator<Property>>(){

            @Override
            public boolean hasNext() {
                return nodes.hasNext();
            }

            @Override
            public Iterator<Property> next() {
                Node n = (Node)nodes.next();
                try {
                    return n.getProperties();
                }
                catch (RepositoryException e) {
                    errorHandler.call(n, e);
                    return TreeTraverser.empty();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static <T> Iterator<T> empty() {
        return Collections.emptySet().iterator();
    }

    private <T> Iterator<T> singleton(T value) {
        return Collections.singleton(value).iterator();
    }

    private static <T extends Item> Iterator<T> filter(Iterator<T> iterator, final InclusionPolicy<? super T> inclusionPolicy) {
        return new FilterIterator<T>(iterator, new Predicate(){

            @Override
            public boolean evaluate(Object object) {
                return inclusionPolicy.include((Item)object);
            }
        });
    }

    public static interface InclusionPolicy<T extends Item> {
        public static final InclusionPolicy<Item> ALL = new InclusionPolicy<Item>(){

            @Override
            public boolean include(Item item) {
                return true;
            }
        };
        public static final InclusionPolicy<Node> LEAVES = new InclusionPolicy<Node>(){

            @Override
            public boolean include(Node node) {
                try {
                    return !node.hasNodes();
                }
                catch (RepositoryException e) {
                    return false;
                }
            }
        };

        public boolean include(T var1);
    }

    public static interface ErrorHandler {
        public static final ErrorHandler IGNORE = new ErrorHandler(){

            @Override
            public void call(Item item, RepositoryException exception) {
            }
        };

        public void call(Item var1, RepositoryException var2);
    }
}

