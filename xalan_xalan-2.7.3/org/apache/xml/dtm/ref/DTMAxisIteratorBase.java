/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.utils.WrappedRuntimeException;

public abstract class DTMAxisIteratorBase
implements DTMAxisIterator {
    protected int _last = -1;
    protected int _position = 0;
    protected int _markedNode;
    protected int _startNode = -1;
    protected boolean _includeSelf = false;
    protected boolean _isRestartable = true;

    @Override
    public int getStartNode() {
        return this._startNode;
    }

    @Override
    public DTMAxisIterator reset() {
        boolean temp = this._isRestartable;
        this._isRestartable = true;
        this.setStartNode(this._startNode);
        this._isRestartable = temp;
        return this;
    }

    public DTMAxisIterator includeSelf() {
        this._includeSelf = true;
        return this;
    }

    @Override
    public int getLast() {
        if (this._last == -1) {
            int temp = this._position;
            this.setMark();
            this.reset();
            do {
                ++this._last;
            } while (this.next() != -1);
            this.gotoMark();
            this._position = temp;
        }
        return this._last;
    }

    @Override
    public int getPosition() {
        return this._position == 0 ? 1 : this._position;
    }

    @Override
    public boolean isReverse() {
        return false;
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            DTMAxisIteratorBase clone = (DTMAxisIteratorBase)super.clone();
            clone._isRestartable = false;
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new WrappedRuntimeException(e);
        }
    }

    protected final int returnNode(int node) {
        ++this._position;
        return node;
    }

    protected final DTMAxisIterator resetPosition() {
        this._position = 0;
        return this;
    }

    public boolean isDocOrdered() {
        return true;
    }

    public int getAxis() {
        return -1;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
        this._isRestartable = isRestartable;
    }

    @Override
    public int getNodeByPosition(int position) {
        if (position > 0) {
            int node;
            int pos;
            int n = pos = this.isReverse() ? this.getLast() - position + 1 : position;
            while ((node = this.next()) != -1) {
                if (pos != this.getPosition()) continue;
                return node;
            }
        }
        return -1;
    }
}

