/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.ASTFactory;
import antlr.collections.AST;
import org.hibernate.hql.internal.NameGenerator;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.util.ASTUtil;

public final class ColumnHelper {
    @Deprecated
    private ColumnHelper() {
    }

    public static void generateSingleScalarColumn(HqlSqlWalkerNode node, int i) {
        ASTFactory factory = node.getASTFactory();
        ASTUtil.createSibling(factory, 151, " as " + NameGenerator.scalarName(i, 0), (AST)node);
    }

    public static void generateScalarColumns(HqlSqlWalkerNode node, String[] sqlColumns, int i) {
        if (sqlColumns.length == 1) {
            ColumnHelper.generateSingleScalarColumn(node, i);
        } else {
            ASTFactory factory = node.getASTFactory();
            HqlSqlWalkerNode n = node;
            n.setText(sqlColumns[0]);
            for (int j = 0; j < sqlColumns.length; ++j) {
                if (j > 0) {
                    n = ASTUtil.createSibling(factory, 150, sqlColumns[j], (AST)n);
                }
                n = ASTUtil.createSibling(factory, 151, " as " + NameGenerator.scalarName(i, j), (AST)n);
            }
        }
    }
}

