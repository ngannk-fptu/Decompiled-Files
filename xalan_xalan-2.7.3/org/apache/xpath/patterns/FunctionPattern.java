/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.patterns;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.StepPattern;

public class FunctionPattern
extends StepPattern {
    static final long serialVersionUID = -5426793413091209944L;
    Expression m_functionExpr;

    public FunctionPattern(Expression expr, int axis, int predaxis) {
        super(0, null, null, axis, predaxis);
        this.m_functionExpr = expr;
    }

    @Override
    public final void calcScore() {
        this.m_score = SCORE_OTHER;
        if (null == this.m_targetString) {
            this.calcTargetString();
        }
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        this.m_functionExpr.fixupVariables(vars, globalsSize);
    }

    @Override
    public XObject execute(XPathContext xctxt, int context) throws TransformerException {
        DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
        XNumber score = SCORE_NONE;
        if (null != nl) {
            int n;
            while (-1 != (n = nl.nextNode())) {
                score = n == context ? SCORE_OTHER : SCORE_NONE;
                if (score != SCORE_OTHER) continue;
                context = n;
                break;
            }
        }
        nl.detach();
        return score;
    }

    @Override
    public XObject execute(XPathContext xctxt, int context, DTM dtm, int expType) throws TransformerException {
        DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
        XNumber score = SCORE_NONE;
        if (null != nl) {
            int n;
            while (-1 != (n = nl.nextNode())) {
                score = n == context ? SCORE_OTHER : SCORE_NONE;
                if (score != SCORE_OTHER) continue;
                context = n;
                break;
            }
            nl.detach();
        }
        return score;
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        int context = xctxt.getCurrentNode();
        DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
        XNumber score = SCORE_NONE;
        if (null != nl) {
            int n;
            while (-1 != (n = nl.nextNode())) {
                score = n == context ? SCORE_OTHER : SCORE_NONE;
                if (score != SCORE_OTHER) continue;
                context = n;
                break;
            }
            nl.detach();
        }
        return score;
    }

    @Override
    protected void callSubtreeVisitors(XPathVisitor visitor) {
        this.m_functionExpr.callVisitors(new FunctionOwner(), visitor);
        super.callSubtreeVisitors(visitor);
    }

    class FunctionOwner
    implements ExpressionOwner {
        FunctionOwner() {
        }

        @Override
        public Expression getExpression() {
            return FunctionPattern.this.m_functionExpr;
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(FunctionPattern.this);
            FunctionPattern.this.m_functionExpr = exp;
        }
    }
}

