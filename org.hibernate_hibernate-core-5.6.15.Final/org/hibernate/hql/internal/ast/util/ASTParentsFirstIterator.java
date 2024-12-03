/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.collections.AST;
import java.util.Iterator;
import java.util.LinkedList;

public class ASTParentsFirstIterator
implements Iterator {
    private AST next;
    private AST tree;
    private LinkedList<AST> parents = new LinkedList();

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    public Object next() {
        return this.nextNode();
    }

    public ASTParentsFirstIterator(AST tree) {
        this.tree = this.next = tree;
    }

    public AST nextNode() {
        AST current = this.next;
        if (this.next != null) {
            AST child = this.next.getFirstChild();
            if (child == null) {
                AST sibling = this.next.getNextSibling();
                if (sibling == null) {
                    AST parent = this.pop();
                    while (parent != null && parent.getNextSibling() == null) {
                        parent = this.pop();
                    }
                    this.next = parent != null ? parent.getNextSibling() : null;
                } else {
                    this.next = sibling;
                }
            } else {
                if (this.next != this.tree) {
                    this.push(this.next);
                }
                this.next = child;
            }
        }
        return current;
    }

    private void push(AST parent) {
        this.parents.addFirst(parent);
    }

    private AST pop() {
        if (this.parents.size() == 0) {
            return null;
        }
        return this.parents.removeFirst();
    }
}

