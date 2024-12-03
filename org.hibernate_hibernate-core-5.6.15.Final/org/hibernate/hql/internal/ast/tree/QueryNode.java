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
import org.hibernate.hql.internal.ast.tree.AbstractRestrictableStatement;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.OrderByClause;
import org.hibernate.hql.internal.ast.tree.SelectClause;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.Type;

public class QueryNode
extends AbstractRestrictableStatement
implements SelectExpression {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(QueryNode.class);
    private OrderByClause orderByClause;
    private int scalarColumnIndex = -1;
    private String alias;

    @Override
    public int getStatementType() {
        return 91;
    }

    @Override
    public boolean needsExecutor() {
        return false;
    }

    @Override
    protected int getWhereClauseParentTokenType() {
        return 23;
    }

    @Override
    protected CoreMessageLogger getLog() {
        return LOG;
    }

    public final SelectClause getSelectClause() {
        return (SelectClause)ASTUtil.findTypeInChildren((AST)this, 145);
    }

    public final boolean hasOrderByClause() {
        OrderByClause orderByClause = this.locateOrderByClause();
        return orderByClause != null && orderByClause.getNumberOfChildren() > 0;
    }

    public final OrderByClause getOrderByClause() {
        if (this.orderByClause == null) {
            this.orderByClause = this.locateOrderByClause();
            if (this.orderByClause == null) {
                LOG.debug("getOrderByClause() : Creating a new ORDER BY clause");
                this.orderByClause = (OrderByClause)this.getWalker().getASTFactory().create(42, "ORDER");
                AST prevSibling = ASTUtil.findTypeInChildren((AST)this, 53);
                if (prevSibling == null) {
                    prevSibling = ASTUtil.findTypeInChildren((AST)this, 23);
                }
                this.orderByClause.setNextSibling(prevSibling.getNextSibling());
                prevSibling.setNextSibling((AST)this.orderByClause);
            }
        }
        return this.orderByClause;
    }

    private OrderByClause locateOrderByClause() {
        return (OrderByClause)ASTUtil.findTypeInChildren((AST)this, 42);
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public FromElement getFromElement() {
        return null;
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
    public boolean isScalar() throws SemanticException {
        return true;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
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

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }

    @Override
    public Type getDataType() {
        return ((SelectExpression)this.getSelectClause().getFirstSelectExpression()).getDataType();
    }
}

