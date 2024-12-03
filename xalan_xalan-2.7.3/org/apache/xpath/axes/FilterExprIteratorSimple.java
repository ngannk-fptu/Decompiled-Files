/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.PathComponent;
import org.apache.xpath.objects.XNodeSet;

public class FilterExprIteratorSimple
extends LocPathIterator {
    static final long serialVersionUID = -6978977187025375579L;
    private Expression m_expr;
    private transient XNodeSet m_exprObj;
    private boolean m_mustHardReset = false;
    private boolean m_canDetachNodeset = true;

    public FilterExprIteratorSimple() {
        super((PrefixResolver)null);
    }

    public FilterExprIteratorSimple(Expression expr) {
        super((PrefixResolver)null);
        this.m_expr = expr;
    }

    @Override
    public void setRoot(int context, Object environment) {
        super.setRoot(context, environment);
        this.m_exprObj = FilterExprIteratorSimple.executeFilterExpr(context, this.m_execContext, this.getPrefixResolver(), this.getIsTopLevel(), this.m_stackFrame, this.m_expr);
    }

    public static XNodeSet executeFilterExpr(int context, XPathContext xctxt, PrefixResolver prefixResolver, boolean isTopLevel, int stackFrame, Expression expr) throws WrappedRuntimeException {
        PrefixResolver savedResolver = xctxt.getNamespaceContext();
        XNodeSet result = null;
        try {
            xctxt.pushCurrentNode(context);
            xctxt.setNamespaceContext(prefixResolver);
            if (isTopLevel) {
                VariableStack vars = xctxt.getVarStack();
                int savedStart = vars.getStackFrame();
                vars.setStackFrame(stackFrame);
                result = (XNodeSet)expr.execute(xctxt);
                result.setShouldCacheNodes(true);
                vars.setStackFrame(savedStart);
            } else {
                result = (XNodeSet)expr.execute(xctxt);
            }
        }
        catch (TransformerException se) {
            throw new WrappedRuntimeException(se);
        }
        finally {
            xctxt.popCurrentNode();
            xctxt.setNamespaceContext(savedResolver);
        }
        return result;
    }

    @Override
    public int nextNode() {
        int next;
        if (this.m_foundLast) {
            return -1;
        }
        if (null != this.m_exprObj) {
            this.m_lastFetched = next = this.m_exprObj.nextNode();
        } else {
            next = -1;
            this.m_lastFetched = -1;
        }
        if (-1 != next) {
            ++this.m_pos;
            return next;
        }
        this.m_foundLast = true;
        return -1;
    }

    @Override
    public void detach() {
        if (this.m_allowDetach) {
            super.detach();
            this.m_exprObj.detach();
            this.m_exprObj = null;
        }
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
        FilterExprIteratorSimple fet = (FilterExprIteratorSimple)expr;
        return this.m_expr.deepEquals(fet.m_expr);
    }

    @Override
    public int getAxis() {
        if (null != this.m_exprObj) {
            return this.m_exprObj.getAxis();
        }
        return 20;
    }

    class filterExprOwner
    implements ExpressionOwner {
        filterExprOwner() {
        }

        @Override
        public Expression getExpression() {
            return FilterExprIteratorSimple.this.m_expr;
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(FilterExprIteratorSimple.this);
            FilterExprIteratorSimple.this.m_expr = exp;
        }
    }
}

