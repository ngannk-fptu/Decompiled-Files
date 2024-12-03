/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.type.Type;

public class NullNode
extends AbstractSelectExpression {
    @Override
    public Type getDataType() {
        return null;
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }

    public Object getValue() {
        return null;
    }

    @Override
    public String getRenderText(SessionFactoryImplementor sessionFactory) {
        return "null";
    }
}

