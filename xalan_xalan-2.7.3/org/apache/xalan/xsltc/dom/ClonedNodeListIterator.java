/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.dom.CachedNodeListIterator;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class ClonedNodeListIterator
extends DTMAxisIteratorBase {
    private CachedNodeListIterator _source;
    private int _index = 0;

    public ClonedNodeListIterator(CachedNodeListIterator source) {
        this._source = source;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        return this;
    }

    @Override
    public int next() {
        return this._source.getNode(this._index++);
    }

    @Override
    public int getPosition() {
        return this._index == 0 ? 1 : this._index;
    }

    @Override
    public int getNodeByPosition(int pos) {
        return this._source.getNode(pos);
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        return this._source.cloneIterator();
    }

    @Override
    public DTMAxisIterator reset() {
        this._index = 0;
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

