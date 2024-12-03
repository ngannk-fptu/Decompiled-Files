/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.IExprVisitor;

public class CallExpr
extends Expr {
    private final Member method;
    private final Expr[] args;
    private final ResolvedType returnType;

    public CallExpr(Member m, Expr[] args, ResolvedType returnType) {
        this.method = m;
        this.args = args;
        this.returnType = returnType;
    }

    @Override
    public void accept(IExprVisitor v) {
        v.visit(this);
    }

    public Expr[] getArgs() {
        return this.args;
    }

    public Member getMethod() {
        return this.method;
    }

    @Override
    public ResolvedType getType() {
        return this.returnType;
    }
}

