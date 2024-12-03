/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.util;

import java.util.LinkedList;
import javax.jcr.Item;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

public abstract class TraversingItemVisitor
implements ItemVisitor {
    protected final boolean breadthFirst;
    protected final int maxLevel;
    private LinkedList currentQueue;
    private LinkedList nextQueue;
    private int currentLevel;

    public TraversingItemVisitor() {
        this(false, -1);
    }

    public TraversingItemVisitor(boolean breadthFirst) {
        this(breadthFirst, -1);
    }

    public TraversingItemVisitor(boolean breadthFirst, int maxLevel) {
        this.breadthFirst = breadthFirst;
        if (breadthFirst) {
            this.currentQueue = new LinkedList();
            this.nextQueue = new LinkedList();
        }
        this.currentLevel = 0;
        this.maxLevel = maxLevel;
    }

    protected abstract void entering(Property var1, int var2) throws RepositoryException;

    protected abstract void entering(Node var1, int var2) throws RepositoryException;

    protected abstract void leaving(Property var1, int var2) throws RepositoryException;

    protected abstract void leaving(Node var1, int var2) throws RepositoryException;

    public void visit(Property property) throws RepositoryException {
        this.entering(property, this.currentLevel);
        this.leaving(property, this.currentLevel);
    }

    public void visit(Node node) throws RepositoryException {
        try {
            if (!this.breadthFirst) {
                this.entering(node, this.currentLevel);
                if (this.maxLevel == -1 || this.currentLevel < this.maxLevel) {
                    ++this.currentLevel;
                    PropertyIterator propIter = node.getProperties();
                    while (propIter.hasNext()) {
                        propIter.nextProperty().accept(this);
                    }
                    NodeIterator nodeIter = node.getNodes();
                    while (nodeIter.hasNext()) {
                        nodeIter.nextNode().accept(this);
                    }
                    --this.currentLevel;
                }
                this.leaving(node, this.currentLevel);
            } else {
                this.entering(node, this.currentLevel);
                this.leaving(node, this.currentLevel);
                if (this.maxLevel == -1 || this.currentLevel < this.maxLevel) {
                    PropertyIterator propIter = node.getProperties();
                    while (propIter.hasNext()) {
                        this.nextQueue.addLast(propIter.nextProperty());
                    }
                    NodeIterator nodeIter = node.getNodes();
                    while (nodeIter.hasNext()) {
                        this.nextQueue.addLast(nodeIter.nextNode());
                    }
                }
                while (!this.currentQueue.isEmpty() || !this.nextQueue.isEmpty()) {
                    if (this.currentQueue.isEmpty()) {
                        ++this.currentLevel;
                        this.currentQueue = this.nextQueue;
                        this.nextQueue = new LinkedList();
                    }
                    Item e = (Item)this.currentQueue.removeFirst();
                    e.accept(this);
                }
                this.currentLevel = 0;
            }
        }
        catch (RepositoryException re) {
            this.currentLevel = 0;
            throw re;
        }
    }

    public static class Default
    extends TraversingItemVisitor {
        public Default() {
        }

        public Default(boolean breadthFirst) {
            super(breadthFirst);
        }

        public Default(boolean breadthFirst, int maxLevel) {
            super(breadthFirst, maxLevel);
        }

        protected void entering(Node node, int level) throws RepositoryException {
        }

        protected void entering(Property property, int level) throws RepositoryException {
        }

        protected void leaving(Node node, int level) throws RepositoryException {
        }

        protected void leaving(Property property, int level) throws RepositoryException {
        }
    }
}

