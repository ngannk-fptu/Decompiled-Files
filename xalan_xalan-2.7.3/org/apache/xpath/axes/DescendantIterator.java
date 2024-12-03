/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.Expression;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpMap;

public class DescendantIterator
extends LocPathIterator {
    static final long serialVersionUID = -1190338607743976938L;
    protected transient DTMAxisTraverser m_traverser;
    protected int m_axis;
    protected int m_extendedTypeID;

    DescendantIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
        super(compiler, opPos, analysis, false);
        int stepOp;
        int nextStepPos;
        int firstStepPos = OpMap.getFirstChildPos(opPos);
        int stepType = compiler.getOp(firstStepPos);
        boolean orSelf = 42 == stepType;
        boolean fromRoot = false;
        if (48 == stepType) {
            orSelf = true;
        } else if (50 == stepType) {
            fromRoot = true;
            nextStepPos = compiler.getNextStepPos(firstStepPos);
            if (compiler.getOp(nextStepPos) == 42) {
                orSelf = true;
            }
        }
        nextStepPos = firstStepPos;
        while ((nextStepPos = compiler.getNextStepPos(nextStepPos)) > 0 && -1 != (stepOp = compiler.getOp(nextStepPos))) {
            firstStepPos = nextStepPos;
        }
        if ((analysis & 0x10000) != 0) {
            orSelf = false;
        }
        this.m_axis = fromRoot ? (orSelf ? 18 : 17) : (orSelf ? 5 : 4);
        int whatToShow = compiler.getWhatToShow(firstStepPos);
        if (0 == (whatToShow & 0x43) || whatToShow == -1) {
            this.initNodeTest(whatToShow);
        } else {
            this.initNodeTest(whatToShow, compiler.getStepNS(firstStepPos), compiler.getStepLocalName(firstStepPos));
        }
        this.initPredicateInfo(compiler, firstStepPos);
    }

    public DescendantIterator() {
        super((PrefixResolver)null);
        this.m_axis = 18;
        int whatToShow = -1;
        this.initNodeTest(whatToShow);
    }

    @Override
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        DescendantIterator clone = (DescendantIterator)super.cloneWithReset();
        clone.m_traverser = this.m_traverser;
        clone.resetProximityPositions();
        return clone;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int nextNode() {
        int savedStart;
        VariableStack vars;
        if (this.m_foundLast) {
            return -1;
        }
        if (-1 == this.m_lastFetched) {
            this.resetProximityPositions();
        }
        if (-1 != this.m_stackFrame) {
            vars = this.m_execContext.getVarStack();
            savedStart = vars.getStackFrame();
            vars.setStackFrame(this.m_stackFrame);
        } else {
            vars = null;
            savedStart = 0;
        }
        try {
            int next;
            do {
                if (0 == this.m_extendedTypeID) {
                    this.m_lastFetched = -1 == this.m_lastFetched ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
                    next = this.m_lastFetched;
                    continue;
                }
                this.m_lastFetched = -1 == this.m_lastFetched ? this.m_traverser.first(this.m_context, this.m_extendedTypeID) : this.m_traverser.next(this.m_context, this.m_lastFetched, this.m_extendedTypeID);
                next = this.m_lastFetched;
            } while (-1 != next && 1 != this.acceptNode(next) && next != -1);
            if (-1 != next) {
                ++this.m_pos;
                int n = next;
                return n;
            }
            this.m_foundLast = true;
            int n = -1;
            return n;
        }
        finally {
            if (-1 != this.m_stackFrame) {
                vars.setStackFrame(savedStart);
            }
        }
    }

    @Override
    public void setRoot(int context, Object environment) {
        super.setRoot(context, environment);
        this.m_traverser = this.m_cdtm.getAxisTraverser(this.m_axis);
        String localName = this.getLocalName();
        String namespace = this.getNamespace();
        int what = this.m_whatToShow;
        if (-1 == what || "*".equals(localName) || "*".equals(namespace)) {
            this.m_extendedTypeID = 0;
        } else {
            int type = DescendantIterator.getNodeTypeTest(what);
            this.m_extendedTypeID = this.m_cdtm.getExpandedTypeID(namespace, localName, type);
        }
    }

    @Override
    public int asNode(XPathContext xctxt) throws TransformerException {
        if (this.getPredicateCount() > 0) {
            return super.asNode(xctxt);
        }
        int current = xctxt.getCurrentNode();
        DTM dtm = xctxt.getDTM(current);
        DTMAxisTraverser traverser = dtm.getAxisTraverser(this.m_axis);
        String localName = this.getLocalName();
        String namespace = this.getNamespace();
        int what = this.m_whatToShow;
        if (-1 == what || localName == "*" || namespace == "*") {
            return traverser.first(current);
        }
        int type = DescendantIterator.getNodeTypeTest(what);
        int extendedType = dtm.getExpandedTypeID(namespace, localName, type);
        return traverser.first(current, extendedType);
    }

    @Override
    public void detach() {
        if (this.m_allowDetach) {
            this.m_traverser = null;
            this.m_extendedTypeID = 0;
            super.detach();
        }
    }

    @Override
    public int getAxis() {
        return this.m_axis;
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        return this.m_axis == ((DescendantIterator)expr).m_axis;
    }
}

