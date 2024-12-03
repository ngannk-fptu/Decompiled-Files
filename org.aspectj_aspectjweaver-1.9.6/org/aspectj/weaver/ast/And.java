/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Test;

public class And
extends Test {
    Test left;
    Test right;

    public And(Test left, Test right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(ITestVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "(" + this.left + " && " + this.right + ")";
    }

    public boolean equals(Object other) {
        if (other instanceof And) {
            And o = (And)other;
            return o.left.equals(this.left) && o.right.equals(this.right);
        }
        return false;
    }

    public Test getLeft() {
        return this.left;
    }

    public Test getRight() {
        return this.right;
    }
}

