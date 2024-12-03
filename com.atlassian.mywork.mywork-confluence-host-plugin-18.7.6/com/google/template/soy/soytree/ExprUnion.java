/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.collect.Lists;
import com.google.template.soy.exprtree.ExprRootNode;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class ExprUnion {
    @Nullable
    private final ExprRootNode<?> expr;
    @Nullable
    private final String exprText;

    public static List<ExprUnion> createList(List<? extends ExprRootNode<?>> exprs) {
        ArrayList exprUnions = Lists.newArrayListWithCapacity((int)exprs.size());
        for (ExprRootNode<?> expr : exprs) {
            exprUnions.add(new ExprUnion(expr));
        }
        return exprUnions;
    }

    public ExprUnion(ExprRootNode<?> expr) {
        this.expr = expr;
        this.exprText = null;
    }

    public ExprUnion(String exprTextV1) {
        this.expr = null;
        this.exprText = exprTextV1;
    }

    public ExprRootNode<?> getExpr() {
        return this.expr;
    }

    public String getExprText() {
        return this.expr != null ? this.expr.toSourceString() : this.exprText;
    }

    public ExprUnion clone() {
        return this.expr != null ? new ExprUnion((ExprRootNode<?>)this.expr.clone()) : new ExprUnion(this.exprText);
    }
}

