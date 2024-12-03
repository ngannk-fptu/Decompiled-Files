/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;

public class SelectExpressionImpl
extends FromReferenceNode
implements SelectExpression {
    @Override
    public void resolveIndex(AST parent) throws SemanticException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        String text = this.getFromElement().renderScalarIdentifierSelect(i);
        this.setText(text);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) throws SemanticException {
    }
}

