/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.util.Collection;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.util.TraversingItemVisitor;

public class ChildrenCollector
extends TraversingItemVisitor.Default {
    private final Collection children;
    private final boolean collectNodes;
    private final boolean collectProperties;

    public ChildrenCollector(Collection children, boolean collectNodes, boolean collectProperties, int maxLevel) {
        super(false, maxLevel);
        this.children = children;
        this.collectNodes = collectNodes;
        this.collectProperties = collectProperties;
    }

    @Override
    protected void entering(Node node, int level) throws RepositoryException {
        if (level > 0 && this.collectNodes) {
            this.children.add(node);
        }
    }

    @Override
    protected void entering(Property property, int level) throws RepositoryException {
        if (level > 0 && this.collectProperties) {
            this.children.add(property);
        }
    }
}

