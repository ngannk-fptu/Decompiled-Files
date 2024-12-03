/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;

public class ResultVariableRefNode
extends HqlSqlWalkerNode {
    private SelectExpression selectExpression;

    public void setSelectExpression(SelectExpression selectExpression) throws SemanticException {
        if (selectExpression == null || selectExpression.getAlias() == null) {
            throw new SemanticException("A ResultVariableRefNode must refer to a non-null alias.");
        }
        this.selectExpression = selectExpression;
    }

    @Override
    public String getRenderText(SessionFactoryImplementor sessionFactory) {
        int scalarColumnIndex = this.selectExpression.getScalarColumnIndex();
        if (scalarColumnIndex < 0) {
            throw new IllegalStateException("selectExpression.getScalarColumnIndex() must be >= 0; actual = " + scalarColumnIndex);
        }
        return sessionFactory.getDialect().replaceResultVariableInOrderByClauseWithPosition() ? this.getColumnPositionsString(scalarColumnIndex) : this.getColumnNamesString(scalarColumnIndex);
    }

    private String getColumnPositionsString(int scalarColumnIndex) {
        int startPosition = this.getWalker().getSelectClause().getColumnNamesStartPosition(scalarColumnIndex);
        StringBuilder buf = new StringBuilder();
        int nColumns = this.getWalker().getSelectClause().getColumnNames()[scalarColumnIndex].length;
        for (int i = startPosition; i < startPosition + nColumns; ++i) {
            if (i > startPosition) {
                buf.append(", ");
            }
            buf.append(i);
        }
        return buf.toString();
    }

    private String getColumnNamesString(int scalarColumnIndex) {
        return String.join((CharSequence)", ", this.getWalker().getSelectClause().getColumnNames()[scalarColumnIndex]);
    }
}

