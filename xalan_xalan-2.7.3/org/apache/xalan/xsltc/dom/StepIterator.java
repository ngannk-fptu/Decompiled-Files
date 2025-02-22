/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public class StepIterator
extends DTMAxisIteratorBase {
    protected DTMAxisIterator _source;
    protected DTMAxisIterator _iterator;
    private int _pos = -1;

    public StepIterator(DTMAxisIterator source, DTMAxisIterator iterator) {
        this._source = source;
        this._iterator = iterator;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
        this._iterator.setRestartable(true);
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        this._isRestartable = false;
        try {
            StepIterator clone = (StepIterator)super.clone();
            clone._source = this._source.cloneIterator();
            clone._iterator = this._iterator.cloneIterator();
            clone._iterator.setRestartable(true);
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
            this._startNode = node;
            this._source.setStartNode(this._startNode);
            this._iterator.setStartNode(this._includeSelf ? this._startNode : this._source.next());
            return this.resetPosition();
        }
        return this;
    }

    @Override
    public DTMAxisIterator reset() {
        this._source.reset();
        this._iterator.setStartNode(this._includeSelf ? this._startNode : this._source.next());
        return this.resetPosition();
    }

    @Override
    public int next() {
        int node;
        while ((node = this._iterator.next()) == -1) {
            node = this._source.next();
            if (node == -1) {
                return -1;
            }
            this._iterator.setStartNode(node);
        }
        return this.returnNode(node);
    }

    @Override
    public void setMark() {
        this._source.setMark();
        this._iterator.setMark();
    }

    @Override
    public void gotoMark() {
        this._source.gotoMark();
        this._iterator.gotoMark();
    }
}

