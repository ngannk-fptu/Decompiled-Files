/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.FunctionNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class AggregateNode
extends AbstractSelectExpression
implements SelectExpression,
FunctionNode {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)AggregateNode.class.getName());
    private SQLFunction sqlFunction;

    @Override
    public SQLFunction getSQLFunction() {
        return this.sqlFunction;
    }

    public void resolve() {
        this.resolveFunction();
    }

    private SQLFunction resolveFunction() {
        if (this.sqlFunction == null) {
            String name = this.getText();
            this.sqlFunction = this.getSessionFactoryHelper().findSQLFunction(this.getText());
            if (this.sqlFunction == null) {
                LOG.unableToResolveAggregateFunction(name);
                this.sqlFunction = new StandardSQLFunction(name);
            }
        }
        return this.sqlFunction;
    }

    @Override
    public Type getFirstArgumentType() {
        AST argument = this.getFirstChild();
        while (argument != null) {
            if (!(argument instanceof SqlNode)) continue;
            Type type = ((SqlNode)argument).getDataType();
            if (type != null) {
                return type;
            }
            argument = argument.getNextSibling();
        }
        return null;
    }

    @Override
    public Type getDataType() {
        return this.getSessionFactoryHelper().findFunctionReturnType(this.getText(), this.resolveFunction(), this.getFirstChild());
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }

    @Override
    public boolean isScalar() throws SemanticException {
        return true;
    }
}

