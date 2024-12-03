/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.util.TokenPrinters;

public abstract class SelectExpressionList
extends HqlSqlWalkerNode {
    private List<Integer> parameterPositions = new ArrayList<Integer>();

    public SelectExpression[] collectSelectExpressions() {
        AST firstChild = this.getFirstSelectExpression();
        ArrayList<SelectExpression> list = new ArrayList<SelectExpression>();
        int p = 0;
        for (AST n = firstChild; n != null; n = n.getNextSibling()) {
            if (n instanceof SelectExpression) {
                list.add((SelectExpression)n);
            } else if (n instanceof ParameterNode) {
                this.parameterPositions.add(p);
            } else {
                throw new IllegalStateException("Unexpected AST: " + n.getClass().getName() + " " + TokenPrinters.SQL_TOKEN_PRINTER.showAsString(n, ""));
            }
            ++p;
        }
        return list.toArray(new SelectExpression[list.size()]);
    }

    public int getTotalParameterCount() {
        return this.parameterPositions.size();
    }

    public List<Integer> getParameterPositions() {
        return this.parameterPositions;
    }

    protected abstract AST getFirstSelectExpression();
}

