/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.ASTFactory;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.InitializeableNode;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.util.AliasGenerator;
import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;

public class HqlSqlWalkerNode
extends SqlNode
implements InitializeableNode {
    private HqlSqlWalker walker;

    @Override
    public void initialize(Object param) {
        this.walker = (HqlSqlWalker)param;
    }

    public HqlSqlWalker getWalker() {
        return this.walker;
    }

    public SessionFactoryHelper getSessionFactoryHelper() {
        return this.walker.getSessionFactoryHelper();
    }

    public ASTFactory getASTFactory() {
        return this.walker.getASTFactory();
    }

    public AliasGenerator getAliasGenerator() {
        return this.walker.getAliasGenerator();
    }
}

