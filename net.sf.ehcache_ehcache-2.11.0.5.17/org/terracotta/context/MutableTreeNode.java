/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.terracotta.context.AbstractTreeNode;
import org.terracotta.context.ContextElement;
import org.terracotta.context.ContextListener;
import org.terracotta.context.TreeNode;

class MutableTreeNode
extends AbstractTreeNode {
    private final CopyOnWriteArraySet<AbstractTreeNode> parents = new CopyOnWriteArraySet();
    private final ContextElement context;

    public MutableTreeNode(ContextElement context) {
        this.context = context;
    }

    @Override
    public ContextElement getContext() {
        return this.context;
    }

    public String toString() {
        return "{" + this.context + "}";
    }

    @Override
    Set<AbstractTreeNode> getAncestors() {
        Set ancestors = Collections.newSetFromMap(new IdentityHashMap());
        ancestors.addAll(this.parents);
        for (AbstractTreeNode parent : this.parents) {
            ancestors.addAll(parent.getAncestors());
        }
        return Collections.unmodifiableSet(ancestors);
    }

    @Override
    public Collection<ContextListener> getListeners() {
        return Collections.emptyList();
    }

    @Override
    void addedParent(AbstractTreeNode parent) {
        this.parents.add(parent);
    }

    @Override
    void removedParent(AbstractTreeNode parent) {
        this.parents.remove(parent);
    }

    @Override
    public Collection<List<? extends TreeNode>> getPaths() {
        ArrayList<List<? extends TreeNode>> paths = new ArrayList<List<? extends TreeNode>>();
        for (TreeNode treeNode : this.parents) {
            for (List<? extends TreeNode> path : treeNode.getPaths()) {
                ArrayList<? extends TreeNode> newPath = new ArrayList<TreeNode>(path);
                newPath.add(this);
                paths.add(newPath);
            }
        }
        return paths;
    }
}

