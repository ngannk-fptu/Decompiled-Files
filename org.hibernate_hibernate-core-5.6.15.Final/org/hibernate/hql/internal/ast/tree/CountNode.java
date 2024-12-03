/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.type.Type;

public class CountNode
extends AbstractSelectExpression
implements SelectExpression {
    @Override
    public Type getDataType() {
        return this.getSessionFactoryHelper().findFunctionReturnType(this.getText(), null);
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }
}

