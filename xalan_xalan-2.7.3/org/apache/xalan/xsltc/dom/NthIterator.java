/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class NthIterator
extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;
    private final int _position;
    private boolean _ready;

    public NthIterator(DTMAxisIterator source, int n) {
        this._source = source;
        this._position = n;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            NthIterator clone = (NthIterator)super.clone();
            clone._source = this._source.cloneIterator();
            clone._isRestartable = false;
            return clone;
        }
        catch (CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }

    @Override
    public int next() {
        if (this._ready) {
            this._ready = false;
            return this._source.getNodeByPosition(this._position);
        }
        return -1;
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            this._source.setStartNode(node);
            this._ready = true;
        }
        return this;
    }

    @Override
    public DTMAxisIterator reset() {
        this._source.reset();
        this._ready = true;
        return this;
    }

    @Override
    public int getLast() {
        return 1;
    }

    @Override
    public int getPosition() {
        return 1;
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

