/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.util.TraversingItemVisitor;
import org.apache.jackrabbit.commons.ItemNameMatcher;
import org.apache.jackrabbit.commons.iterator.NodeIteratorAdapter;
import org.apache.jackrabbit.commons.iterator.PropertyIteratorAdapter;

public class ChildrenCollectorFilter
extends TraversingItemVisitor.Default {
    static final char WILDCARD_CHAR = '*';
    static final String OR = "|";
    private final Collection<Item> children;
    private final boolean collectNodes;
    private final boolean collectProperties;
    private final String namePattern;
    private final String[] nameGlobs;

    public ChildrenCollectorFilter(String namePattern, Collection<Item> children, boolean collectNodes, boolean collectProperties, int maxLevel) {
        super(false, maxLevel);
        this.namePattern = namePattern;
        this.nameGlobs = null;
        this.children = children;
        this.collectNodes = collectNodes;
        this.collectProperties = collectProperties;
    }

    public ChildrenCollectorFilter(String[] nameGlobs, Collection<Item> children, boolean collectNodes, boolean collectProperties, int maxLevel) {
        super(false, maxLevel);
        this.nameGlobs = nameGlobs;
        this.namePattern = null;
        this.children = children;
        this.collectNodes = collectNodes;
        this.collectProperties = collectProperties;
    }

    public static NodeIterator collectChildNodes(Node node, String namePattern) throws RepositoryException {
        ArrayList<Item> nodes = new ArrayList<Item>();
        node.accept(new ChildrenCollectorFilter(namePattern, nodes, true, false, 1));
        return new NodeIteratorAdapter(nodes);
    }

    public static NodeIterator collectChildNodes(Node node, String[] nameGlobs) throws RepositoryException {
        ArrayList<Item> nodes = new ArrayList<Item>();
        node.accept(new ChildrenCollectorFilter(nameGlobs, nodes, true, false, 1));
        return new NodeIteratorAdapter(nodes);
    }

    public static PropertyIterator collectProperties(Node node, String namePattern) throws RepositoryException {
        Collection<Item> properties = Collections.emptySet();
        PropertyIterator pit = node.getProperties();
        while (pit.hasNext()) {
            Property p = pit.nextProperty();
            if (!ChildrenCollectorFilter.matches(p.getName(), namePattern)) continue;
            properties = ChildrenCollectorFilter.addToCollection(properties, p);
        }
        return new PropertyIteratorAdapter(properties);
    }

    public static PropertyIterator collectProperties(Node node, String[] nameGlobs) throws RepositoryException {
        Collection<Item> properties = Collections.emptySet();
        PropertyIterator pit = node.getProperties();
        while (pit.hasNext()) {
            Property p = pit.nextProperty();
            if (!ChildrenCollectorFilter.matches(p.getName(), nameGlobs)) continue;
            properties = ChildrenCollectorFilter.addToCollection(properties, p);
        }
        return new PropertyIteratorAdapter(properties);
    }

    @Override
    protected void entering(Node node, int level) throws RepositoryException {
        if (level > 0 && this.collectNodes) {
            if (this.namePattern != null) {
                if (ChildrenCollectorFilter.matches(node.getName(), this.namePattern)) {
                    this.children.add(node);
                }
            } else if (ChildrenCollectorFilter.matches(node.getName(), this.nameGlobs)) {
                this.children.add(node);
            }
        }
    }

    @Override
    protected void entering(Property property, int level) throws RepositoryException {
        if (level > 0 && this.collectProperties) {
            if (this.namePattern != null) {
                if (ChildrenCollectorFilter.matches(property.getName(), this.namePattern)) {
                    this.children.add(property);
                }
            } else if (ChildrenCollectorFilter.matches(property.getName(), this.nameGlobs)) {
                this.children.add(property);
            }
        }
    }

    public static boolean matches(String name, String pattern) {
        return ItemNameMatcher.matches(name, pattern);
    }

    public static boolean matches(String name, String[] nameGlobs) {
        return ItemNameMatcher.matches(name, nameGlobs);
    }

    private static Collection<Item> addToCollection(Collection<Item> c, Item p) {
        Collection<Item> nc = c;
        if (c.isEmpty()) {
            nc = Collections.singleton(p);
        } else if (c.size() == 1) {
            nc = new ArrayList<Item>(c);
            nc.add(p);
        } else {
            nc.add(p);
        }
        return nc;
    }
}

