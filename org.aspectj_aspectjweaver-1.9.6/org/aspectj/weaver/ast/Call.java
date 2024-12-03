/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.Member;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Test;

public class Call
extends Test {
    private final Member method;
    private final Expr[] args;

    public Call(Member m, Expr[] args) {
        this.method = m;
        this.args = args;
    }

    @Override
    public void accept(ITestVisitor v) {
        v.visit(this);
    }

    public Expr[] getArgs() {
        return this.args;
    }

    public Member getMethod() {
        return this.method;
    }
}

