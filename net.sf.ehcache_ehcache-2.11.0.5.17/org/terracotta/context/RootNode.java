/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.terracotta.context.AbstractTreeNode;
import org.terracotta.context.ContextElement;
import org.terracotta.context.ContextListener;
import org.terracotta.context.TreeNode;

class RootNode
extends AbstractTreeNode {
    private final Collection<ContextListener> listeners = new CopyOnWriteArrayList<ContextListener>();

    RootNode() {
    }

    @Override
    void addedParent(AbstractTreeNode child) {
        throw new IllegalStateException();
    }

    @Override
    void removedParent(AbstractTreeNode child) {
        throw new IllegalStateException();
    }

    @Override
    Set<AbstractTreeNode> getAncestors() {
        return Collections.emptySet();
    }

    @Override
    Collection<ContextListener> getListeners() {
        return Collections.unmodifiableCollection(this.listeners);
    }

    public void addListener(ContextListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ContextListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public ContextElement getContext() {
        throw new IllegalStateException();
    }

    @Override
    public Collection<List<? extends TreeNode>> getPaths() {
        return Collections.singleton(Collections.emptyList());
    }
}

