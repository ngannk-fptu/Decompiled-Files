/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.type.Type;

public interface SelectExpression {
    public Type getDataType();

    public void setScalarColumnText(int var1) throws SemanticException;

    public void setScalarColumn(int var1) throws SemanticException;

    public int getScalarColumnIndex();

    public FromElement getFromElement();

    public boolean isConstructor();

    public boolean isReturnableEntity() throws SemanticException;

    public void setText(String var1);

    public boolean isScalar() throws SemanticException;

    public void setAlias(String var1);

    public String getAlias();
}

