/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.ast.CallExpr;
import org.aspectj.weaver.ast.FieldGet;
import org.aspectj.weaver.ast.Var;

public interface IExprVisitor {
    public void visit(Var var1);

    public void visit(FieldGet var1);

    public void visit(CallExpr var1);
}

