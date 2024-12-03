/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import org.hibernate.hql.internal.ast.tree.AbstractRestrictableStatement;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class UpdateStatement
extends AbstractRestrictableStatement {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(UpdateStatement.class);

    @Override
    public int getStatementType() {
        return 51;
    }

    @Override
    public boolean needsExecutor() {
        return true;
    }

    @Override
    protected int getWhereClauseParentTokenType() {
        return 47;
    }

    @Override
    protected CoreMessageLogger getLog() {
        return LOG;
    }

    public AST getSetClause() {
        return ASTUtil.findTypeInChildren((AST)this, 47);
    }
}

