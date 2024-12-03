/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.Statement;

public interface RestrictableStatement
extends Statement {
    public FromClause getFromClause();

    public boolean hasWhereClause();

    public AST getWhereClause();
}

