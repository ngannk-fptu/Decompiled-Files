/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.UnaryOperatorNode;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class UnaryLogicOperatorNode
extends AbstractSelectExpression
implements UnaryOperatorNode {
    @Override
    public Node getOperand() {
        return (Node)this.getFirstChild();
    }

    @Override
    public void initialize() {
    }

    @Override
    public Type getDataType() {
        return StandardBasicTypes.BOOLEAN;
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }
}

