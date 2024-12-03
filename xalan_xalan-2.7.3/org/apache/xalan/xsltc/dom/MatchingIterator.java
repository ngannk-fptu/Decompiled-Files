/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class MatchingIterator
extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;
    private final int _match;

    public MatchingIterator(int match, DTMAxisIterator source) {
        this._source = source;
        this._match = match;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            MatchingIterator clone = (MatchingIterator)super.clone();
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
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            this._source.setStartNode(node);
            this._position = 1;
            while ((node = this._source.next()) != -1 && node != this._match) {
                ++this._position;
            }
        }
        return this;
    }

    @Override
    public DTMAxisIterator reset() {
        this._source.reset();
        return this.resetPosition();
    }

    @Override
    public int next() {
        return this._source.next();
    }

    @Override
    public int getLast() {
        if (this._last == -1) {
            this._last = this._source.getLast();
        }
        return this._last;
    }

    @Override
    public int getPosition() {
        return this._position;
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

