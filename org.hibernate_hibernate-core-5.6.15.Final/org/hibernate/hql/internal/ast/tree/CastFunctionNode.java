/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.CastFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.FunctionNode;
import org.hibernate.hql.internal.ast.tree.IdentNode;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.type.Type;

public class CastFunctionNode
extends AbstractSelectExpression
implements FunctionNode {
    private SQLFunction dialectCastFunction;
    private Node expressionNode;
    private IdentNode typeNode;
    private Type castType;

    public void resolve(boolean inSelect) {
        Type expressionType;
        this.dialectCastFunction = this.getSessionFactoryHelper().findSQLFunction("cast");
        if (this.dialectCastFunction == null) {
            this.dialectCastFunction = CastFunction.INSTANCE;
        }
        this.expressionNode = (Node)this.getFirstChild();
        if (this.expressionNode == null) {
            throw new QueryException("Could not resolve expression to CAST");
        }
        if (SqlNode.class.isInstance((Object)this.expressionNode) && (expressionType = ((SqlNode)this.expressionNode).getDataType()) != null) {
            if (expressionType.isEntityType()) {
                throw new QueryException("Expression to CAST cannot be an entity : " + this.expressionNode.getText());
            }
            if (expressionType.isComponentType()) {
                throw new QueryException("Expression to CAST cannot be a composite : " + this.expressionNode.getText());
            }
            if (expressionType.isCollectionType()) {
                throw new QueryException("Expression to CAST cannot be a collection : " + this.expressionNode.getText());
            }
        }
        this.typeNode = (IdentNode)this.expressionNode.getNextSibling();
        if (this.typeNode == null) {
            throw new QueryException("Could not resolve requested type for CAST");
        }
        String typeName = this.typeNode.getText();
        this.castType = this.getSessionFactoryHelper().getFactory().getTypeResolver().heuristicType(typeName);
        if (this.castType == null) {
            throw new QueryException("Could not resolve requested type for CAST : " + typeName);
        }
        if (this.castType.isEntityType()) {
            throw new QueryException("CAST target type cannot be an entity : " + this.expressionNode.getText());
        }
        if (this.castType.isComponentType()) {
            throw new QueryException("CAST target type cannot be a composite : " + this.expressionNode.getText());
        }
        if (this.castType.isCollectionType()) {
            throw new QueryException("CAST target type cannot be a collection : " + this.expressionNode.getText());
        }
        this.setDataType(this.castType);
    }

    @Override
    public SQLFunction getSQLFunction() {
        return this.dialectCastFunction;
    }

    @Override
    public Type getFirstArgumentType() {
        return this.castType;
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }
}

