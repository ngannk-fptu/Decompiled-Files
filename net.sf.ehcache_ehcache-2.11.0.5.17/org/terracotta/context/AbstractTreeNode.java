/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.terracotta.context.ContextListener;
import org.terracotta.context.TreeNode;

abstract class AbstractTreeNode
implements TreeNode {
    private final CopyOnWriteArraySet<AbstractTreeNode> children = new CopyOnWriteArraySet();

    AbstractTreeNode() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addChild(AbstractTreeNode child) {
        AbstractTreeNode abstractTreeNode = this;
        synchronized (abstractTreeNode) {
            HashSet<AbstractTreeNode> ancestors = new HashSet<AbstractTreeNode>(this.getAncestors());
            ancestors.removeAll(child.getAncestors());
            if (this.children.add(child)) {
                child.addedParent(this);
                for (AbstractTreeNode ancestor : ancestors) {
                    for (ContextListener listener : ancestor.getListeners()) {
                        listener.graphAdded(this, child);
                    }
                }
                return true;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeChild(AbstractTreeNode child) {
        AbstractTreeNode abstractTreeNode = this;
        synchronized (abstractTreeNode) {
            if (this.children.remove(child)) {
                child.removedParent(this);
                HashSet<AbstractTreeNode> ancestors = new HashSet<AbstractTreeNode>(this.getAncestors());
                ancestors.removeAll(child.getAncestors());
                for (AbstractTreeNode ancestor : ancestors) {
                    for (ContextListener listener : ancestor.getListeners()) {
                        listener.graphRemoved(this, child);
                    }
                }
                return true;
            }
            return false;
        }
    }

    public Set<? extends AbstractTreeNode> getChildren() {
        return Collections.unmodifiableSet(this.children);
    }

    @Override
    public List<? extends TreeNode> getPath() {
        Collection<List<? extends TreeNode>> paths = this.getPaths();
        if (paths.size() == 1) {
            return paths.iterator().next();
        }
        throw new IllegalStateException("No unique path to root");
    }

    @Override
    public String toTreeString() {
        return AbstractTreeNode.dumpSubtree(0, this);
    }

    public static String dumpSubtree(int indent, TreeNode node) {
        char[] indentChars = new char[indent];
        Arrays.fill(indentChars, ' ');
        StringBuilder sb = new StringBuilder();
        String nodeString = node.toString();
        sb.append(indentChars).append(nodeString).append("\n");
        for (TreeNode treeNode : node.getChildren()) {
            sb.append(AbstractTreeNode.dumpSubtree(indent + 2, treeNode));
        }
        return sb.toString();
    }

    abstract void addedParent(AbstractTreeNode var1);

    abstract void removedParent(AbstractTreeNode var1);

    abstract Set<AbstractTreeNode> getAncestors();

    abstract Collection<ContextListener> getListeners();

    @Override
    public void clean() {
        for (AbstractTreeNode abstractTreeNode : this.getChildren()) {
            this.removeChild(abstractTreeNode);
        }
        for (AbstractTreeNode abstractTreeNode : this.getAncestors()) {
            abstractTreeNode.removeChild(this);
        }
    }
}

