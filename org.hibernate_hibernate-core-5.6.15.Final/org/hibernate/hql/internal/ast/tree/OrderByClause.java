/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.util.ASTUtil;

public class OrderByClause
extends HqlSqlWalkerNode
implements HqlSqlTokenTypes {
    public void addOrderFragment(String orderByFragment) {
        AST fragment = ASTUtil.create(this.getASTFactory(), 150, orderByFragment);
        if (this.getFirstChild() == null) {
            this.setFirstChild(fragment);
        } else {
            this.addChild(fragment);
        }
    }
}

