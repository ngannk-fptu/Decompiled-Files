/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.type.Type;

public abstract class AbstractSelectExpression
extends HqlSqlWalkerNode
implements SelectExpression {
    private String alias;
    private int scalarColumnIndex = -1;

    @Override
    public final void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public final String getAlias() {
        return this.alias;
    }

    @Override
    public boolean isConstructor() {
        return false;
    }

    @Override
    public boolean isReturnableEntity() throws SemanticException {
        return false;
    }

    @Override
    public FromElement getFromElement() {
        return null;
    }

    @Override
    public boolean isScalar() throws SemanticException {
        Type type = this.getDataType();
        return type != null && !type.isAssociationType();
    }

    @Override
    public void setScalarColumn(int i) throws SemanticException {
        this.scalarColumnIndex = i;
        this.setScalarColumnText(i);
    }

    @Override
    public int getScalarColumnIndex() {
        return this.scalarColumnIndex;
    }
}

