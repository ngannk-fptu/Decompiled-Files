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

public class ASTIterator
implements Iterator {
    private AST next;
    private LinkedList<AST> parents = new LinkedList();

    public ASTIterator(AST tree) {
        this.next = tree;
        this.down();
    }

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

    public AST nextNode() {
        AST current = this.next;
        if (this.next != null) {
            AST nextSibling = this.next.getNextSibling();
            if (nextSibling == null) {
                this.next = this.pop();
            } else {
                this.next = nextSibling;
                this.down();
            }
        }
        return current;
    }

    private void down() {
        while (this.next != null && this.next.getFirstChild() != null) {
            this.push(this.next);
            this.next = this.next.getFirstChild();
        }
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

