/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.visitor;

import java.util.LinkedList;
import javax.jcr.Item;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.Predicate;

public abstract class FilteringItemVisitor
implements ItemVisitor {
    protected Predicate includePredicate = Predicate.TRUE;
    protected Predicate traversalPredicate = Predicate.TRUE;
    protected boolean walkProperties = false;
    protected boolean breadthFirst = false;
    protected int maxLevel = -1;
    protected LinkedList currentQueue;
    protected LinkedList nextQueue;
    protected int currentLevel;

    public void setMaxLevel(int ml) {
        this.maxLevel = ml;
    }

    public void setBreadthFirst(boolean flag) {
        if (this.breadthFirst != flag) {
            this.breadthFirst = flag;
            if (this.breadthFirst) {
                this.currentQueue = new LinkedList();
                this.nextQueue = new LinkedList();
            } else {
                this.currentQueue = null;
                this.nextQueue = null;
            }
        }
    }

    public void setWalkProperties(boolean flag) {
        this.walkProperties = flag;
    }

    public void setIncludePredicate(Predicate ip) {
        this.includePredicate = ip;
    }

    public void setTraversalPredicate(Predicate tp) {
        this.traversalPredicate = tp;
    }

    protected abstract void entering(Property var1, int var2) throws RepositoryException;

    protected abstract void entering(Node var1, int var2) throws RepositoryException;

    protected abstract void leaving(Property var1, int var2) throws RepositoryException;

    protected abstract void leaving(Node var1, int var2) throws RepositoryException;

    @Override
    public void visit(Property property) throws RepositoryException {
        if (this.walkProperties && this.includePredicate.evaluate(property)) {
            this.entering(property, this.currentLevel);
            this.leaving(property, this.currentLevel);
        }
    }

    @Override
    public void visit(Node node) throws RepositoryException {
        if (this.traversalPredicate.evaluate(node) && (this.includePredicate == this.traversalPredicate || this.includePredicate.evaluate(node))) {
            try {
                if (!this.breadthFirst) {
                    this.entering(node, this.currentLevel);
                    if (this.maxLevel == -1 || this.currentLevel < this.maxLevel) {
                        ++this.currentLevel;
                        if (this.walkProperties) {
                            PropertyIterator propIter = node.getProperties();
                            while (propIter.hasNext()) {
                                propIter.nextProperty().accept(this);
                            }
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
                        if (this.walkProperties) {
                            PropertyIterator propIter = node.getProperties();
                            while (propIter.hasNext()) {
                                this.nextQueue.addLast(propIter.nextProperty());
                            }
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
    }
}

