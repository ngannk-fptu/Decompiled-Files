/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.patterns;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.NodeTest;

public class StepPattern
extends NodeTest
implements SubContextList,
ExpressionOwner {
    static final long serialVersionUID = 9071668960168152644L;
    protected int m_axis;
    String m_targetString;
    StepPattern m_relativePathPattern;
    Expression[] m_predicates;
    private static final boolean DEBUG_MATCHES = false;

    public StepPattern(int whatToShow, String namespace, String name, int axis, int axisForPredicate) {
        super(whatToShow, namespace, name);
        this.m_axis = axis;
    }

    public StepPattern(int whatToShow, int axis, int axisForPredicate) {
        super(whatToShow);
        this.m_axis = axis;
    }

    public void calcTargetString() {
        int whatToShow = this.getWhatToShow();
        switch (whatToShow) {
            case 128: {
                this.m_targetString = "#comment";
                break;
            }
            case 4: 
            case 8: 
            case 12: {
                this.m_targetString = "#text";
                break;
            }
            case -1: {
                this.m_targetString = "*";
                break;
            }
            case 256: 
            case 1280: {
                this.m_targetString = "/";
                break;
            }
            case 1: {
                if ("*" == this.m_name) {
                    this.m_targetString = "*";
                    break;
                }
                this.m_targetString = this.m_name;
                break;
            }
            default: {
                this.m_targetString = "*";
            }
        }
    }

    public String getTargetString() {
        return this.m_targetString;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        if (null != this.m_predicates) {
            for (int i = 0; i < this.m_predicates.length; ++i) {
                this.m_predicates[i].fixupVariables(vars, globalsSize);
            }
        }
        if (null != this.m_relativePathPattern) {
            this.m_relativePathPattern.fixupVariables(vars, globalsSize);
        }
    }

    public void setRelativePathPattern(StepPattern expr) {
        this.m_relativePathPattern = expr;
        expr.exprSetParent(this);
        this.calcScore();
    }

    public StepPattern getRelativePathPattern() {
        return this.m_relativePathPattern;
    }

    public Expression[] getPredicates() {
        return this.m_predicates;
    }

    @Override
    public boolean canTraverseOutsideSubtree() {
        int n = this.getPredicateCount();
        for (int i = 0; i < n; ++i) {
            if (!this.getPredicate(i).canTraverseOutsideSubtree()) continue;
            return true;
        }
        return false;
    }

    public Expression getPredicate(int i) {
        return this.m_predicates[i];
    }

    public final int getPredicateCount() {
        return null == this.m_predicates ? 0 : this.m_predicates.length;
    }

    public void setPredicates(Expression[] predicates) {
        this.m_predicates = predicates;
        if (null != predicates) {
            for (int i = 0; i < predicates.length; ++i) {
                predicates[i].exprSetParent(this);
            }
        }
        this.calcScore();
    }

    @Override
    public void calcScore() {
        if (this.getPredicateCount() > 0 || null != this.m_relativePathPattern) {
            this.m_score = SCORE_OTHER;
        } else {
            super.calcScore();
        }
        if (null == this.m_targetString) {
            this.calcTargetString();
        }
    }

    @Override
    public XObject execute(XPathContext xctxt, int currentNode) throws TransformerException {
        DTM dtm = xctxt.getDTM(currentNode);
        if (dtm != null) {
            int expType = dtm.getExpandedTypeID(currentNode);
            return this.execute(xctxt, currentNode, dtm, expType);
        }
        return NodeTest.SCORE_NONE;
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt, xctxt.getCurrentNode());
    }

    @Override
    public XObject execute(XPathContext xctxt, int currentNode, DTM dtm, int expType) throws TransformerException {
        if (this.m_whatToShow == 65536) {
            if (null != this.m_relativePathPattern) {
                return this.m_relativePathPattern.execute(xctxt);
            }
            return NodeTest.SCORE_NONE;
        }
        XObject score = super.execute(xctxt, currentNode, dtm, expType);
        if (score == NodeTest.SCORE_NONE) {
            return NodeTest.SCORE_NONE;
        }
        if (this.getPredicateCount() != 0 && !this.executePredicates(xctxt, dtm, currentNode)) {
            return NodeTest.SCORE_NONE;
        }
        if (null != this.m_relativePathPattern) {
            return this.m_relativePathPattern.executeRelativePathPattern(xctxt, dtm, currentNode);
        }
        return score;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final boolean checkProximityPosition(XPathContext xctxt, int predPos, DTM dtm, int context, int pos) {
        try {
            DTMAxisTraverser traverser = dtm.getAxisTraverser(12);
            int child = traverser.first(context);
            while (-1 != child) {
                block20: {
                    try {
                        xctxt.pushCurrentNode(child);
                        if (NodeTest.SCORE_NONE == super.execute(xctxt, child)) break block20;
                        boolean pass = true;
                        try {
                            xctxt.pushSubContextList(this);
                            for (int i = 0; i < predPos; ++i) {
                                xctxt.pushPredicatePos(i);
                                try {
                                    XObject pred = this.m_predicates[i].execute(xctxt);
                                    try {
                                        if (2 == pred.getType()) {
                                            throw new Error("Why: Should never have been called");
                                        }
                                        if (pred.boolWithSideEffects()) continue;
                                        pass = false;
                                        break;
                                    }
                                    finally {
                                        pred.detach();
                                    }
                                }
                                finally {
                                    xctxt.popPredicatePos();
                                }
                            }
                        }
                        finally {
                            xctxt.popSubContextList();
                        }
                        if (pass) {
                            --pos;
                        }
                        if (pos < 1) {
                            boolean bl = false;
                            return bl;
                        }
                    }
                    finally {
                        xctxt.popCurrentNode();
                    }
                }
                child = traverser.next(context, child);
            }
        }
        catch (TransformerException se) {
            throw new RuntimeException(se.getMessage());
        }
        if (pos != 1) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final int getProximityPosition(XPathContext xctxt, int predPos, boolean findLast) {
        int pos = 0;
        int context = xctxt.getCurrentNode();
        DTM dtm = xctxt.getDTM(context);
        int parent = dtm.getParent(context);
        try {
            DTMAxisTraverser traverser = dtm.getAxisTraverser(3);
            int child = traverser.first(parent);
            while (-1 != child) {
                block23: {
                    try {
                        xctxt.pushCurrentNode(child);
                        if (NodeTest.SCORE_NONE == super.execute(xctxt, child)) break block23;
                        boolean pass = true;
                        try {
                            xctxt.pushSubContextList(this);
                            for (int i = 0; i < predPos; ++i) {
                                xctxt.pushPredicatePos(i);
                                try {
                                    XObject pred = this.m_predicates[i].execute(xctxt);
                                    try {
                                        if (2 == pred.getType()) {
                                            if (pos + 1 == (int)pred.numWithSideEffects()) continue;
                                            pass = false;
                                        } else {
                                            if (pred.boolWithSideEffects()) continue;
                                            pass = false;
                                        }
                                        break;
                                    }
                                    finally {
                                        pred.detach();
                                    }
                                }
                                finally {
                                    xctxt.popPredicatePos();
                                }
                            }
                        }
                        finally {
                            xctxt.popSubContextList();
                        }
                        if (pass) {
                            ++pos;
                        }
                        if (!findLast && child == context) {
                            int n = pos;
                            return n;
                        }
                    }
                    finally {
                        xctxt.popCurrentNode();
                    }
                }
                child = traverser.next(parent, child);
            }
            return pos;
        }
        catch (TransformerException se) {
            throw new RuntimeException(se.getMessage());
        }
    }

    @Override
    public int getProximityPosition(XPathContext xctxt) {
        return this.getProximityPosition(xctxt, xctxt.getPredicatePos(), false);
    }

    @Override
    public int getLastPos(XPathContext xctxt) {
        return this.getProximityPosition(xctxt, xctxt.getPredicatePos(), true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final XObject executeRelativePathPattern(XPathContext xctxt, DTM dtm, int currentNode) throws TransformerException {
        XObject score = NodeTest.SCORE_NONE;
        int context = currentNode;
        DTMAxisTraverser traverser = dtm.getAxisTraverser(this.m_axis);
        int relative = traverser.first(context);
        while (-1 != relative) {
            try {
                xctxt.pushCurrentNode(relative);
                score = this.execute(xctxt);
                if (score != NodeTest.SCORE_NONE) {
                    break;
                }
            }
            finally {
                xctxt.popCurrentNode();
            }
            relative = traverser.next(context, relative);
        }
        return score;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final boolean executePredicates(XPathContext xctxt, DTM dtm, int currentNode) throws TransformerException {
        boolean result = true;
        boolean positionAlreadySeen = false;
        int n = this.getPredicateCount();
        try {
            xctxt.pushSubContextList(this);
            for (int i = 0; i < n; ++i) {
                xctxt.pushPredicatePos(i);
                try {
                    XObject pred = this.m_predicates[i].execute(xctxt);
                    try {
                        if (2 == pred.getType()) {
                            int pos = (int)pred.num();
                            if (positionAlreadySeen) {
                                result = pos == 1;
                                break;
                            }
                            positionAlreadySeen = true;
                            if (this.checkProximityPosition(xctxt, i, dtm, currentNode, pos)) continue;
                            result = false;
                            break;
                        }
                        if (pred.boolWithSideEffects()) continue;
                        result = false;
                        break;
                    }
                    finally {
                        pred.detach();
                    }
                }
                finally {
                    xctxt.popPredicatePos();
                }
            }
        }
        finally {
            xctxt.popSubContextList();
        }
        return result;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        StepPattern pat = this;
        while (pat != null) {
            if (pat != this) {
                buf.append("/");
            }
            buf.append(Axis.getNames(pat.m_axis));
            buf.append("::");
            if (20480 == pat.m_whatToShow) {
                buf.append("doc()");
            } else if (65536 == pat.m_whatToShow) {
                buf.append("function()");
            } else if (-1 == pat.m_whatToShow) {
                buf.append("node()");
            } else if (4 == pat.m_whatToShow) {
                buf.append("text()");
            } else if (64 == pat.m_whatToShow) {
                buf.append("processing-instruction(");
                if (null != pat.m_name) {
                    buf.append(pat.m_name);
                }
                buf.append(")");
            } else if (128 == pat.m_whatToShow) {
                buf.append("comment()");
            } else if (null != pat.m_name) {
                if (2 == pat.m_whatToShow) {
                    buf.append("@");
                }
                if (null != pat.m_namespace) {
                    buf.append("{");
                    buf.append(pat.m_namespace);
                    buf.append("}");
                }
                buf.append(pat.m_name);
            } else if (2 == pat.m_whatToShow) {
                buf.append("@");
            } else if (1280 == pat.m_whatToShow) {
                buf.append("doc-root()");
            } else {
                buf.append("?" + Integer.toHexString(pat.m_whatToShow));
            }
            if (null != pat.m_predicates) {
                for (int i = 0; i < pat.m_predicates.length; ++i) {
                    buf.append("[");
                    buf.append(pat.m_predicates[i]);
                    buf.append("]");
                }
            }
            pat = pat.m_relativePathPattern;
        }
        return buf.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double getMatchScore(XPathContext xctxt, int context) throws TransformerException {
        xctxt.pushCurrentNode(context);
        xctxt.pushCurrentExpressionNode(context);
        try {
            XObject score = this.execute(xctxt);
            double d = score.num();
            return d;
        }
        finally {
            xctxt.popCurrentNode();
            xctxt.popCurrentExpressionNode();
        }
    }

    public void setAxis(int axis) {
        this.m_axis = axis;
    }

    public int getAxis() {
        return this.m_axis;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        if (visitor.visitMatchPattern(owner, this)) {
            this.callSubtreeVisitors(visitor);
        }
    }

    protected void callSubtreeVisitors(XPathVisitor visitor) {
        if (null != this.m_predicates) {
            int n = this.m_predicates.length;
            for (int i = 0; i < n; ++i) {
                PredOwner predOwner = new PredOwner(i);
                if (!visitor.visitPredicate(predOwner, this.m_predicates[i])) continue;
                this.m_predicates[i].callVisitors(predOwner, visitor);
            }
        }
        if (null != this.m_relativePathPattern) {
            this.m_relativePathPattern.callVisitors(this, visitor);
        }
    }

    @Override
    public Expression getExpression() {
        return this.m_relativePathPattern;
    }

    @Override
    public void setExpression(Expression exp) {
        exp.exprSetParent(this);
        this.m_relativePathPattern = (StepPattern)exp;
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        StepPattern sp = (StepPattern)expr;
        if (null != this.m_predicates) {
            int n = this.m_predicates.length;
            if (null == sp.m_predicates || sp.m_predicates.length != n) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                if (this.m_predicates[i].deepEquals(sp.m_predicates[i])) continue;
                return false;
            }
        } else if (null != sp.m_predicates) {
            return false;
        }
        return !(null != this.m_relativePathPattern ? !this.m_relativePathPattern.deepEquals(sp.m_relativePathPattern) : sp.m_relativePathPattern != null);
    }

    class PredOwner
    implements ExpressionOwner {
        int m_index;

        PredOwner(int index) {
            this.m_index = index;
        }

        @Override
        public Expression getExpression() {
            return StepPattern.this.m_predicates[this.m_index];
        }

        @Override
        public void setExpression(Expression exp) {
            exp.exprSetParent(StepPattern.this);
            StepPattern.this.m_predicates[this.m_index] = exp;
        }
    }
}

