/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.IExprVisitor;

public class Var
extends Expr {
    public static final Var[] NONE = new Var[0];
    ResolvedType variableType;

    public Var(ResolvedType variableType) {
        this.variableType = variableType;
    }

    @Override
    public ResolvedType getType() {
        return this.variableType;
    }

    public String toString() {
        return "(Var " + this.variableType + ")";
    }

    @Override
    public void accept(IExprVisitor v) {
        v.visit(this);
    }

    public Var getAccessorForValue(ResolvedType formalType, String formalName) {
        throw new IllegalStateException("Only makes sense for annotation variables");
    }
}

