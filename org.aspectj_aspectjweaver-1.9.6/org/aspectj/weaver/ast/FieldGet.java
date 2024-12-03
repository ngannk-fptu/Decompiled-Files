/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.IExprVisitor;

public class FieldGet
extends Expr {
    Member field;
    ResolvedType resolvedType;

    public FieldGet(Member field, ResolvedType resolvedType) {
        this.field = field;
        this.resolvedType = resolvedType;
    }

    @Override
    public ResolvedType getType() {
        return this.resolvedType;
    }

    public String toString() {
        return "(FieldGet " + this.field + ")";
    }

    @Override
    public void accept(IExprVisitor v) {
        v.visit(this);
    }

    public Member getField() {
        return this.field;
    }
}

