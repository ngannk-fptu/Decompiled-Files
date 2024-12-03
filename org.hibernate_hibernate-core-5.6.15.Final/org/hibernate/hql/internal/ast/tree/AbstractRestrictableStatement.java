/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import org.hibernate.hql.internal.ast.tree.AbstractStatement;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.RestrictableStatement;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.internal.CoreMessageLogger;

public abstract class AbstractRestrictableStatement
extends AbstractStatement
implements RestrictableStatement {
    private FromClause fromClause;
    private AST whereClause;

    protected abstract int getWhereClauseParentTokenType();

    protected abstract CoreMessageLogger getLog();

    @Override
    public final FromClause getFromClause() {
        if (this.fromClause == null) {
            this.fromClause = (FromClause)ASTUtil.findTypeInChildren((AST)this, 23);
        }
        return this.fromClause;
    }

    @Override
    public final boolean hasWhereClause() {
        AST whereClause = this.locateWhereClause();
        return whereClause != null && whereClause.getNumberOfChildren() > 0;
    }

    @Override
    public final AST getWhereClause() {
        if (this.whereClause == null) {
            this.whereClause = this.locateWhereClause();
            if (this.whereClause == null) {
                this.getLog().debug("getWhereClause() : Creating a new WHERE clause...");
                this.whereClause = this.getWalker().getASTFactory().create(53, "WHERE");
                AST parent = ASTUtil.findTypeInChildren((AST)this, this.getWhereClauseParentTokenType());
                this.whereClause.setNextSibling(parent.getNextSibling());
                parent.setNextSibling(this.whereClause);
            }
        }
        return this.whereClause;
    }

    protected AST locateWhereClause() {
        return ASTUtil.findTypeInChildren((AST)this, 53);
    }
}

