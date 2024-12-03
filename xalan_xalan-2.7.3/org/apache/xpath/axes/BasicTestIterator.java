/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.VariableStack;
import org.apache.xpath.axes.ChildTestIterator;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpMap;

public abstract class BasicTestIterator
extends LocPathIterator {
    static final long serialVersionUID = 3505378079378096623L;

    protected BasicTestIterator() {
    }

    protected BasicTestIterator(PrefixResolver nscontext) {
        super(nscontext);
    }

    protected BasicTestIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
        super(compiler, opPos, analysis, false);
        int firstStepPos = OpMap.getFirstChildPos(opPos);
        int whatToShow = compiler.getWhatToShow(firstStepPos);
        if (0 == (whatToShow & 0x1043) || whatToShow == -1) {
            this.initNodeTest(whatToShow);
        } else {
            this.initNodeTest(whatToShow, compiler.getStepNS(firstStepPos), compiler.getStepLocalName(firstStepPos));
        }
        this.initPredicateInfo(compiler, firstStepPos);
    }

    protected BasicTestIterator(Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers) throws TransformerException {
        super(compiler, opPos, analysis, shouldLoadWalkers);
    }

    protected abstract int getNextNode();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int nextNode() {
        int savedStart;
        VariableStack vars;
        if (this.m_foundLast) {
            this.m_lastFetched = -1;
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
            while (-1 != (next = this.getNextNode()) && 1 != this.acceptNode(next) && next != -1) {
            }
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
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        ChildTestIterator clone = (ChildTestIterator)super.cloneWithReset();
        clone.resetProximityPositions();
        return clone;
    }
}

