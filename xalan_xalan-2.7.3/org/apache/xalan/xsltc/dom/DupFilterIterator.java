/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.dom.KeyIndex;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class DupFilterIterator
extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;
    private IntegerArray _nodes = new IntegerArray();
    private int _current = 0;
    private int _nodesSize = 0;
    private int _lastNext = -1;
    private int _markedLastNext = -1;

    public DupFilterIterator(DTMAxisIterator source) {
        this._source = source;
        if (source instanceof KeyIndex) {
            this.setStartNode(0);
        }
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            boolean sourceIsKeyIndex = this._source instanceof KeyIndex;
            if (sourceIsKeyIndex && this._startNode == 0) {
                return this;
            }
            if (node != this._startNode) {
                this._startNode = node;
                this._source.setStartNode(this._startNode);
                this._nodes.clear();
                while ((node = this._source.next()) != -1) {
                    this._nodes.add(node);
                }
                if (!sourceIsKeyIndex) {
                    this._nodes.sort();
                }
                this._nodesSize = this._nodes.cardinality();
                this._current = 0;
                this._lastNext = -1;
                this.resetPosition();
            }
        }
        return this;
    }

    @Override
    public int next() {
        while (this._current < this._nodesSize) {
            int next;
            if ((next = this._nodes.at(this._current++)) == this._lastNext) continue;
            this._lastNext = next;
            return this.returnNode(this._lastNext);
        }
        return -1;
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            DupFilterIterator clone = (DupFilterIterator)super.clone();
            clone._nodes = (IntegerArray)this._nodes.clone();
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
    public void setRestartable(boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }

    @Override
    public void setMark() {
        this._markedNode = this._current;
        this._markedLastNext = this._lastNext;
    }

    @Override
    public void gotoMark() {
        this._current = this._markedNode;
        this._lastNext = this._markedLastNext;
    }

    @Override
    public DTMAxisIterator reset() {
        this._current = 0;
        this._lastNext = -1;
        return this.resetPosition();
    }
}

