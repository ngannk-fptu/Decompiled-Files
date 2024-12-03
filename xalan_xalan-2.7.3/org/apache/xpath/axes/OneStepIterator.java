/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.ChildTestIterator;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpMap;

public class OneStepIterator
extends ChildTestIterator {
    static final long serialVersionUID = 4623710779664998283L;
    protected int m_axis = -1;
    protected DTMAxisIterator m_iterator;

    OneStepIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
        super(compiler, opPos, analysis);
        int firstStepPos = OpMap.getFirstChildPos(opPos);
        this.m_axis = WalkerFactory.getAxisFromStep(compiler, firstStepPos);
    }

    public OneStepIterator(DTMAxisIterator iterator, int axis) throws TransformerException {
        super((DTMAxisTraverser)null);
        this.m_iterator = iterator;
        this.m_axis = axis;
        int whatToShow = -1;
        this.initNodeTest(whatToShow);
    }

    @Override
    public void setRoot(int context, Object environment) {
        super.setRoot(context, environment);
        if (this.m_axis > -1) {
            this.m_iterator = this.m_cdtm.getAxisIterator(this.m_axis);
        }
        this.m_iterator.setStartNode(this.m_context);
    }

    @Override
    public void detach() {
        if (this.m_allowDetach) {
            if (this.m_axis > -1) {
                this.m_iterator = null;
            }
            super.detach();
        }
    }

    @Override
    protected int getNextNode() {
        this.m_lastFetched = this.m_iterator.next();
        return this.m_lastFetched;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        OneStepIterator clone = (OneStepIterator)super.clone();
        if (this.m_iterator != null) {
            clone.m_iterator = this.m_iterator.cloneIterator();
        }
        return clone;
    }

    @Override
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        OneStepIterator clone = (OneStepIterator)super.cloneWithReset();
        clone.m_iterator = this.m_iterator;
        return clone;
    }

    @Override
    public boolean isReverseAxes() {
        return this.m_iterator.isReverse();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int getProximityPosition(int predicateIndex) {
        if (!this.isReverseAxes()) {
            return super.getProximityPosition(predicateIndex);
        }
        if (predicateIndex < 0) {
            return -1;
        }
        if (this.m_proximityPositions[predicateIndex] <= 0) {
            XPathContext xctxt = this.getXPathContext();
            try {
                int next;
                OneStepIterator clone = (OneStepIterator)this.clone();
                int root = this.getRoot();
                xctxt.pushCurrentNode(root);
                clone.setRoot(root, xctxt);
                clone.m_predCount = predicateIndex;
                int count = 1;
                while (-1 != (next = clone.nextNode())) {
                    ++count;
                }
                int n = predicateIndex;
                this.m_proximityPositions[n] = this.m_proximityPositions[n] + count;
            }
            catch (CloneNotSupportedException cloneNotSupportedException) {
            }
            finally {
                xctxt.popCurrentNode();
            }
        }
        return this.m_proximityPositions[predicateIndex];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getLength() {
        if (!this.isReverseAxes()) {
            return super.getLength();
        }
        boolean isPredicateTest = this == this.m_execContext.getSubContextList();
        int predCount = this.getPredicateCount();
        if (-1 != this.m_length && isPredicateTest && this.m_predicateIndex < 1) {
            return this.m_length;
        }
        int count = 0;
        XPathContext xctxt = this.getXPathContext();
        try {
            int next;
            OneStepIterator clone = (OneStepIterator)this.cloneWithReset();
            int root = this.getRoot();
            xctxt.pushCurrentNode(root);
            clone.setRoot(root, xctxt);
            clone.m_predCount = this.m_predicateIndex;
            while (-1 != (next = clone.nextNode())) {
                ++count;
            }
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
        }
        finally {
            xctxt.popCurrentNode();
        }
        if (isPredicateTest && this.m_predicateIndex < 1) {
            this.m_length = count;
        }
        return count;
    }

    @Override
    protected void countProximityPosition(int i) {
        if (!this.isReverseAxes()) {
            super.countProximityPosition(i);
        } else if (i < this.m_proximityPositions.length) {
            int n = i;
            this.m_proximityPositions[n] = this.m_proximityPositions[n] - 1;
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (null != this.m_iterator) {
            this.m_iterator.reset();
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
        return this.m_axis == ((OneStepIterator)expr).m_axis;
    }
}

