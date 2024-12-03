/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ast.ASTNode;
import org.aspectj.weaver.ast.CallExpr;
import org.aspectj.weaver.ast.IExprVisitor;

public abstract class Expr
extends ASTNode {
    public static final Expr[] NONE = new Expr[0];

    public abstract void accept(IExprVisitor var1);

    public abstract ResolvedType getType();

    public static CallExpr makeCallExpr(Member member, Expr[] exprs, ResolvedType returnType) {
        return new CallExpr(member, exprs, returnType);
    }
}

