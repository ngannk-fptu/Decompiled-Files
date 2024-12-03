/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.patterns;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.patterns.StepPattern;

public class UnionPattern
extends Expression {
    static final long serialVersionUID = -6670449967116905820L;
    private StepPattern[] m_patterns;

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        for (int i = 0; i < this.m_patterns.length; ++i) {
            this.m_patterns[i].fixupVariables(vars, globalsSize);
        }
    }

    @Override
    public boolean canTraverseOutsideSubtree() {
        if (null != this.m_patterns) {
            int n = this.m_patterns.length;
            for (int i = 0; i < n; ++i) {
                if (!this.m_patterns[i].canTraverseOutsideSubtree()) continue;
                return true;
            }
        }
        return false;
    }

    public void setPatterns(StepPattern[] patterns) {
        this.m_patterns = patterns;
        if (null != patterns) {
            for (int i = 0; i < patterns.length; ++i) {
                patterns[i].exprSetParent(this);
            }
        }
    }

    public StepPattern[] getPatterns() {
        return this.m_patterns;
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        XObject bestScore = null;
        int n = this.m_patterns.length;
        for (int i = 0; i < n; ++i) {
            XObject score = this.m_patterns[i].execute(xctxt);
            if (score == NodeTest.SCORE_NONE) continue;
            if (null == bestScore) {
                bestScore = score;
                continue;
            }
            if (!(score.num() > bestScore.num())) continue;
            bestScore = score;
        }
        if (null == bestScore) {
            bestScore = NodeTest.SCORE_NONE;
        }
        return bestScore;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        visitor.visitUnionPattern(owner, this);
        if (null != this.m_patterns) {
            int n = this.m_patterns.length;
            for (int i = 0; i < n; ++i) {
                this.m_patterns[i].callVisitors(new UnionPathPartOwner(i), visitor);
            }
        }
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!this.isSameClass(expr)) {
            return false;
        }
        UnionPattern up = (UnionPattern)expr;
        if (null != this.m_patterns) {
            int n = this.m_patterns.length;
            if (null == up.m_patterns || up.m_patterns.length != n) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                if (this.m_patterns[i].deepEquals(up.m_patterns[i])) continue;
                return false;
            }
        } else if (up.m_patterns != null) {
            return false;
        }
        return true;
    }

    class UnionPathPartOwner
    implements ExpressionOwner {
        int m_index;

        UnionPathPartOwner(int index) {
            this.m_index = index;
        }

        @Override
        public Expression getExpression() {
            return UnionPattern.this.m_patterns[this.m_index];
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(UnionPattern.this);
            ((UnionPattern)UnionPattern.this).m_patterns[this.m_index] = (StepPattern)exp;
        }
    }
}

