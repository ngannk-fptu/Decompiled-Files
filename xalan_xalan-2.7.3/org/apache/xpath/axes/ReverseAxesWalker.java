/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.AxesWalker;
import org.apache.xpath.axes.LocPathIterator;

public class ReverseAxesWalker
extends AxesWalker {
    static final long serialVersionUID = 2847007647832768941L;
    protected DTMAxisIterator m_iterator;

    ReverseAxesWalker(LocPathIterator locPathIterator, int axis) {
        super(locPathIterator, axis);
    }

    @Override
    public void setRoot(int root) {
        super.setRoot(root);
        this.m_iterator = this.getDTM(root).getAxisIterator(this.m_axis);
        this.m_iterator.setStartNode(root);
    }

    @Override
    public void detach() {
        this.m_iterator = null;
        super.detach();
    }

    @Override
    protected int getNextNode() {
        if (this.m_foundLast) {
            return -1;
        }
        int next = this.m_iterator.next();
        if (this.m_isFresh) {
            this.m_isFresh = false;
        }
        if (-1 == next) {
            this.m_foundLast = true;
        }
        return next;
    }

    @Override
    public boolean isReverseAxes() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int getProximityPosition(int predicateIndex) {
        if (predicateIndex < 0) {
            return -1;
        }
        int count = this.m_proximityPositions[predicateIndex];
        if (count <= 0) {
            AxesWalker savedWalker = this.wi().getLastUsedWalker();
            try {
                int next;
                ReverseAxesWalker clone = (ReverseAxesWalker)this.clone();
                clone.setRoot(this.getRoot());
                clone.setPredicateCount(predicateIndex);
                clone.setPrevWalker(null);
                clone.setNextWalker(null);
                this.wi().setLastUsedWalker(clone);
                ++count;
                while (-1 != (next = clone.nextNode())) {
                    ++count;
                }
                this.m_proximityPositions[predicateIndex] = count;
            }
            catch (CloneNotSupportedException cloneNotSupportedException) {
            }
            finally {
                this.wi().setLastUsedWalker(savedWalker);
            }
        }
        return count;
    }

    @Override
    protected void countProximityPosition(int i) {
        if (i < this.m_proximityPositions.length) {
            int n = i;
            this.m_proximityPositions[n] = this.m_proximityPositions[n] - 1;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getLastPos(XPathContext xctxt) {
        int count = 0;
        AxesWalker savedWalker = this.wi().getLastUsedWalker();
        try {
            int next;
            ReverseAxesWalker clone = (ReverseAxesWalker)this.clone();
            clone.setRoot(this.getRoot());
            clone.setPredicateCount(this.m_predicateIndex);
            clone.setPrevWalker(null);
            clone.setNextWalker(null);
            this.wi().setLastUsedWalker(clone);
            while (-1 != (next = clone.nextNode())) {
                ++count;
            }
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
        }
        finally {
            this.wi().setLastUsedWalker(savedWalker);
        }
        return count;
    }

    @Override
    public boolean isDocOrdered() {
        return false;
    }
}

