/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpMap;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.patterns.StepPattern;

public class MatchPatternIterator
extends LocPathIterator {
    static final long serialVersionUID = -5201153767396296474L;
    protected StepPattern m_pattern;
    protected int m_superAxis = -1;
    protected DTMAxisTraverser m_traverser;
    private static final boolean DEBUG = false;

    MatchPatternIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
        super(compiler, opPos, analysis, false);
        int firstStepPos = OpMap.getFirstChildPos(opPos);
        this.m_pattern = WalkerFactory.loadSteps(this, compiler, firstStepPos, 0);
        boolean fromRoot = false;
        boolean walkBack = false;
        boolean walkDescendants = false;
        boolean walkAttributes = false;
        if (0 != (analysis & 0x28000000)) {
            fromRoot = true;
        }
        if (0 != (analysis & 0x5D86000)) {
            walkBack = true;
        }
        if (0 != (analysis & 0x70000)) {
            walkDescendants = true;
        }
        if (0 != (analysis & 0x208000)) {
            walkAttributes = true;
        }
        this.m_superAxis = fromRoot || walkBack ? (walkAttributes ? 16 : 17) : (walkDescendants ? (walkAttributes ? 14 : 5) : 16);
    }

    @Override
    public void setRoot(int context, Object environment) {
        super.setRoot(context, environment);
        this.m_traverser = this.m_cdtm.getAxisTraverser(this.m_superAxis);
    }

    @Override
    public void detach() {
        if (this.m_allowDetach) {
            this.m_traverser = null;
            super.detach();
        }
    }

    protected int getNextNode() {
        this.m_lastFetched = -1 == this.m_lastFetched ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
        return this.m_lastFetched;
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
            while (-1 != (next = this.getNextNode()) && 1 != this.acceptNode(next, this.m_execContext) && next != -1) {
            }
            if (-1 != next) {
                this.incrementCurrentPos();
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

    public short acceptNode(int n, XPathContext xctxt) {
        try {
            xctxt.pushCurrentNode(n);
            xctxt.pushIteratorRoot(this.m_context);
            XObject score = this.m_pattern.execute(xctxt);
            short s = score == NodeTest.SCORE_NONE ? (short)3 : 1;
            return s;
        }
        catch (TransformerException se) {
            throw new RuntimeException(se.getMessage());
        }
        finally {
            xctxt.popCurrentNode();
            xctxt.popIteratorRoot();
        }
    }
}

