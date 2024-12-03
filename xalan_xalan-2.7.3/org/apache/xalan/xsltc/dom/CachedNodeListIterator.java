/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.dom.ClonedNodeListIterator;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class CachedNodeListIterator
extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;
    private IntegerArray _nodes = new IntegerArray();
    private int _numCachedNodes = 0;
    private int _index = 0;
    private boolean _isEnded = false;

    public CachedNodeListIterator(DTMAxisIterator source) {
        this._source = source;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            this._startNode = node;
            this._source.setStartNode(node);
            this.resetPosition();
            this._isRestartable = false;
        }
        return this;
    }

    @Override
    public int next() {
        return this.getNode(this._index++);
    }

    @Override
    public int getPosition() {
        return this._index == 0 ? 1 : this._index;
    }

    @Override
    public int getNodeByPosition(int pos) {
        return this.getNode(pos);
    }

    public int getNode(int index) {
        if (index < this._numCachedNodes) {
            return this._nodes.at(index);
        }
        if (!this._isEnded) {
            int node = this._source.next();
            if (node != -1) {
                this._nodes.add(node);
                ++this._numCachedNodes;
            } else {
                this._isEnded = true;
            }
            return node;
        }
        return -1;
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        ClonedNodeListIterator clone = new ClonedNodeListIterator(this);
        return clone;
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

