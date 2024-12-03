/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Test;

public class Not
extends Test {
    Test test;

    public Not(Test test) {
        this.test = test;
    }

    @Override
    public void accept(ITestVisitor v) {
        v.visit(this);
    }

    public Test getBody() {
        return this.test;
    }

    public String toString() {
        return "!" + this.test;
    }

    public boolean equals(Object other) {
        if (other instanceof Not) {
            Not o = (Not)other;
            return o.test.equals(this.test);
        }
        return false;
    }

    public int hashCode() {
        return this.test.hashCode();
    }
}

