/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.List;
import java.util.Set;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeMatcher;

public abstract class NodeHandlerDecorator<T>
implements NodeHandler<T> {
    @Override
    public String nodeName(T node) {
        return this.getDecoratedNodeHandler().nodeName(node);
    }

    @Override
    public Object getValue(T node) {
        return this.getDecoratedNodeHandler().getValue(node);
    }

    @Override
    public T getParent(T node) {
        return this.getDecoratedNodeHandler().getParent(node);
    }

    @Override
    public List<T> getChildren(T node) {
        return this.getDecoratedNodeHandler().getChildren(node);
    }

    @Override
    public <C> List<T> getMatchingChildren(T node, NodeMatcher<C> matcher, C criterion) {
        return this.getDecoratedNodeHandler().getMatchingChildren(node, matcher, criterion);
    }

    @Override
    public <C> int getMatchingChildrenCount(T node, NodeMatcher<C> matcher, C criterion) {
        return this.getDecoratedNodeHandler().getMatchingChildrenCount(node, matcher, criterion);
    }

    @Override
    public List<T> getChildren(T node, String name) {
        return this.getDecoratedNodeHandler().getChildren(node, name);
    }

    @Override
    public T getChild(T node, int index) {
        return this.getDecoratedNodeHandler().getChild(node, index);
    }

    @Override
    public int indexOfChild(T parent, T child) {
        return this.getDecoratedNodeHandler().indexOfChild(parent, child);
    }

    @Override
    public int getChildrenCount(T node, String name) {
        return this.getDecoratedNodeHandler().getChildrenCount(node, name);
    }

    @Override
    public Set<String> getAttributes(T node) {
        return this.getDecoratedNodeHandler().getAttributes(node);
    }

    @Override
    public boolean hasAttributes(T node) {
        return this.getDecoratedNodeHandler().hasAttributes(node);
    }

    @Override
    public Object getAttributeValue(T node, String name) {
        return this.getDecoratedNodeHandler().getAttributeValue(node, name);
    }

    @Override
    public boolean isDefined(T node) {
        return this.getDecoratedNodeHandler().isDefined(node);
    }

    @Override
    public T getRootNode() {
        return this.getDecoratedNodeHandler().getRootNode();
    }

    protected abstract NodeHandler<T> getDecoratedNodeHandler();
}

