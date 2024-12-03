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
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.MethodNode;

public class CollectionFunction
extends MethodNode
implements DisplayableNode {
    @Override
    public void resolve(boolean inSelect) throws SemanticException {
        this.initializeMethodNode((AST)this, inSelect);
        if (!this.isCollectionPropertyMethod()) {
            throw new SemanticException(this.getText() + " is not a collection property name!");
        }
        AST expr = this.getFirstChild();
        if (expr == null) {
            throw new SemanticException(this.getText() + " requires a path!");
        }
        this.resolveCollectionProperty(expr);
    }

    @Override
    protected void prepareSelectColumns(String[] selectColumns) {
        String subselect = selectColumns[0].trim();
        if (subselect.startsWith("(") && subselect.endsWith(")")) {
            subselect = subselect.substring(1, subselect.length() - 1);
        }
        selectColumns[0] = subselect;
    }
}

