/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.AxesWalker;
import org.apache.xpath.axes.FilterExprIteratorSimple;
import org.apache.xpath.axes.PathComponent;
import org.apache.xpath.axes.WalkingIterator;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.operations.Variable;

public class FilterExprWalker
extends AxesWalker {
    static final long serialVersionUID = 5457182471424488375L;
    private Expression m_expr;
    private transient XNodeSet m_exprObj;
    private boolean m_mustHardReset = false;
    private boolean m_canDetachNodeset = true;

    public FilterExprWalker(WalkingIterator locPathIterator) {
        super(locPathIterator, 20);
    }

    @Override
    public void init(Compiler compiler, int opPos, int stepType) throws TransformerException {
        super.init(compiler, opPos, stepType);
        switch (stepType) {
            case 24: 
            case 25: {
                this.m_mustHardReset = true;
            }
            case 22: 
            case 23: {
                this.m_expr = compiler.compile(opPos);
                this.m_expr.exprSetParent(this);
                if (!(this.m_expr instanceof Variable)) break;
                this.m_canDetachNodeset = false;
                break;
            }
            default: {
                this.m_expr = compiler.compile(opPos + 2);
                this.m_expr.exprSetParent(this);
            }
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (this.m_canDetachNodeset) {
            this.m_exprObj.detach();
        }
        this.m_exprObj = null;
    }

    @Override
    public void setRoot(int root) {
        super.setRoot(root);
        this.m_exprObj = FilterExprIteratorSimple.executeFilterExpr(root, this.m_lpi.getXPathContext(), this.m_lpi.getPrefixResolver(), this.m_lpi.getIsTopLevel(), this.m_lpi.m_stackFrame, this.m_expr);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FilterExprWalker clone = (FilterExprWalker)super.clone();
        if (null != this.m_exprObj) {
            clone.m_exprObj = (XNodeSet)this.m_exprObj.clone();
        }
        return clone;
    }

    @Override
    public short acceptNode(int n) {
        try {
            if (this.getPredicateCount() > 0) {
                this.countProximityPosition(0);
                if (!this.executePredicates(n, this.m_lpi.getXPathContext())) {
                    return 3;
                }
            }
            return 1;
        }
        catch (TransformerException se) {
            throw new RuntimeException(se.getMessage());
        }
    }

    @Override
    public int getNextNode() {
        if (null != this.m_exprObj) {
            int next = this.m_exprObj.nextNode();
            return next;
        }
        return -1;
    }

    @Override
    public int getLastPos(XPathContext xctxt) {
        return this.m_exprObj.getLength();
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
    public int getAxis() {
        return this.m_exprObj.getAxis();
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
        FilterExprWalker walker = (FilterExprWalker)expr;
        return this.m_expr.deepEquals(walker.m_expr);
    }

    class filterExprOwner
    implements ExpressionOwner {
        filterExprOwner() {
        }

        @Override
        public Expression getExpression() {
            return FilterExprWalker.this.m_expr;
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(FilterExprWalker.this);
            FilterExprWalker.this.m_expr = exp;
        }
    }
}

