/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.type.Type;

public class SearchedCaseNode
extends AbstractSelectExpression
implements SelectExpression,
ExpectedTypeAwareNode {
    @Override
    public Type getDataType() {
        for (AST option = this.getFirstChild(); option != null; option = option.getNextSibling()) {
            Type nodeDataType;
            AST result;
            if (option.getType() == 61) {
                result = option.getFirstChild().getNextSibling();
            } else if (option.getType() == 59) {
                result = option.getFirstChild();
            } else {
                throw new QueryException("Unexpected node type :" + ASTUtil.getTokenTypeName(HqlSqlTokenTypes.class, option.getType()) + "; expecting WHEN or ELSE");
            }
            if (!SqlNode.class.isInstance(result) || (nodeDataType = ((SqlNode)result).getDataType()) == null) continue;
            return nodeDataType;
        }
        return null;
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }

    @Override
    public void setExpectedType(Type expectedType) {
        for (AST option = this.getFirstChild(); option != null; option = option.getNextSibling()) {
            if (option.getType() == 61) {
                if (!ParameterNode.class.isAssignableFrom(option.getFirstChild().getNextSibling().getClass())) continue;
                ((ParameterNode)option.getFirstChild().getNextSibling()).setExpectedType(expectedType);
                continue;
            }
            if (option.getType() != 59 || !ParameterNode.class.isAssignableFrom(option.getFirstChild().getClass())) continue;
            ((ParameterNode)option.getFirstChild()).setExpectedType(expectedType);
        }
    }

    @Override
    public Type getExpectedType() {
        return null;
    }
}

