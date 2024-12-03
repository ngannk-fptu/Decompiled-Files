/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class FilterIterator
extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;
    private final DTMFilter _filter;
    private final boolean _isReverse;

    public FilterIterator(DTMAxisIterator source, DTMFilter filter) {
        this._source = source;
        this._filter = filter;
        this._isReverse = source.isReverse();
    }

    @Override
    public boolean isReverse() {
        return this._isReverse;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            FilterIterator clone = (FilterIterator)super.clone();
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
    public DTMAxisIterator reset() {
        this._source.reset();
        return this.resetPosition();
    }

    @Override
    public int next() {
        int node;
        while ((node = this._source.next()) != -1) {
            if (this._filter.acceptNode(node, -1) != 1) continue;
            return this.returnNode(node);
        }
        return -1;
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            this._startNode = node;
            this._source.setStartNode(this._startNode);
            return this.resetPosition();
        }
        return this;
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

