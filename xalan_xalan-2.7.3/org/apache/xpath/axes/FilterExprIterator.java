/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import java.util.Vector;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.BasicTestIterator;
import org.apache.xpath.axes.FilterExprIteratorSimple;
import org.apache.xpath.axes.PathComponent;
import org.apache.xpath.objects.XNodeSet;

public class FilterExprIterator
extends BasicTestIterator {
    static final long serialVersionUID = 2552176105165737614L;
    private Expression m_expr;
    private transient XNodeSet m_exprObj;
    private boolean m_mustHardReset = false;
    private boolean m_canDetachNodeset = true;

    public FilterExprIterator() {
        super((PrefixResolver)null);
    }

    public FilterExprIterator(Expression expr) {
        super((PrefixResolver)null);
        this.m_expr = expr;
    }

    @Override
    public void setRoot(int context, Object environment) {
        super.setRoot(context, environment);
        this.m_exprObj = FilterExprIteratorSimple.executeFilterExpr(context, this.m_execContext, this.getPrefixResolver(), this.getIsTopLevel(), this.m_stackFrame, this.m_expr);
    }

    @Override
    protected int getNextNode() {
        this.m_lastFetched = null != this.m_exprObj ? this.m_exprObj.nextNode() : -1;
        return this.m_lastFetched;
    }

    @Override
    public void detach() {
        super.detach();
        this.m_exprObj.detach();
        this.m_exprObj = null;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        this.m_expr.fixupVariables(vars, globalsSize);
    }

    public Expression getInnerExpression() {
        return this.m_expr;
    }

    public void setInnerExpression(Expression expr) {
        expr.exprSetParent(this);
        this.m_expr = expr;
    }

    @Override
    public int getAnalysisBits() {
        if (null != this.m_expr && this.m_expr instanceof PathComponent) {
            return ((PathComponent)((Object)this.m_expr)).getAnalysisBits();
        }
        return 0x4000000;
    }

    @Override
    public boolean isDocOrdered() {
        return this.m_exprObj.isDocOrdered();
    }

    @Override
    public void callPredicateVisitors(XPathVisitor visitor) {
        this.m_expr.callVisitors(new filterExprOwner(), visitor);
        super.callPredicateVisitors(visitor);
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        FilterExprIterator fet = (FilterExprIterator)expr;
        return this.m_expr.deepEquals(fet.m_expr);
    }

    class filterExprOwner
    implements ExpressionOwner {
        filterExprOwner() {
        }

        @Override
        public Expression getExpression() {
            return FilterExprIterator.this.m_expr;
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(FilterExprIterator.this);
            FilterExprIterator.this.m_expr = exp;
        }
    }
}

