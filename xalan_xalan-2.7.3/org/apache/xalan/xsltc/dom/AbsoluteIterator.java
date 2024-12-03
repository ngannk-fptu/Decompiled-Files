/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class AbsoluteIterator
extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;

    public AbsoluteIterator(DTMAxisIterator source) {
        this._source = source;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        this._startNode = 0;
        if (this._isRestartable) {
            this._source.setStartNode(this._startNode);
            this.resetPosition();
        }
        return this;
    }

    @Override
    public int next() {
        return this.returnNode(this._source.next());
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            AbsoluteIterator clone = (AbsoluteIterator)super.clone();
            clone._source = this._source.cloneIterator();
            clone.resetPosition();
            clone._isRestartable = false;
            return clone;
        }
        catch (CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }

    @Override
    public DTMAxisIterator reset() {
        this._source.reset();
        return this.resetPosition();
    }

    @Override
    public void setMark() {
        this._source.setMark();
    }

    @Override
    public void gotoMark() {
        this._source.gotoMark();
    }
}

