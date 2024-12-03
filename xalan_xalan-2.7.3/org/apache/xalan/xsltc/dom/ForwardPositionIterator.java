/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class ForwardPositionIterator
extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;

    public ForwardPositionIterator(DTMAxisIterator source) {
        this._source = source;
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            ForwardPositionIterator clone = (ForwardPositionIterator)super.clone();
            clone._source = this._source.cloneIterator();
            clone._isRestartable = false;
            return clone.reset();
        }
        catch (CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }

    @Override
    public int next() {
        return this.returnNode(this._source.next());
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        this._source.setStartNode(node);
        return this;
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

