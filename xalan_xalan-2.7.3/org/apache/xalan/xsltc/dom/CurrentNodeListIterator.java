/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.dom.CurrentNodeListFilter;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class CurrentNodeListIterator
extends DTMAxisIteratorBase {
    private boolean _docOrder;
    private DTMAxisIterator _source;
    private final CurrentNodeListFilter _filter;
    private IntegerArray _nodes = new IntegerArray();
    private int _currentIndex;
    private final int _currentNode;
    private AbstractTranslet _translet;

    public CurrentNodeListIterator(DTMAxisIterator source, CurrentNodeListFilter filter, int currentNode, AbstractTranslet translet) {
        this(source, !source.isReverse(), filter, currentNode, translet);
    }

    public CurrentNodeListIterator(DTMAxisIterator source, boolean docOrder, CurrentNodeListFilter filter, int currentNode, AbstractTranslet translet) {
        this._source = source;
        this._filter = filter;
        this._translet = translet;
        this._docOrder = docOrder;
        this._currentNode = currentNode;
    }

    public DTMAxisIterator forceNaturalOrder() {
        this._docOrder = true;
        return this;
    }

    @Override
    public void setRestartable(boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }

    @Override
    public boolean isReverse() {
        return !this._docOrder;
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            CurrentNodeListIterator clone = (CurrentNodeListIterator)super.clone();
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
    public DTMAxisIterator reset() {
        this._currentIndex = 0;
        return this.resetPosition();
    }

    @Override
    public int next() {
        int last = this._nodes.cardinality();
        int currentNode = this._currentNode;
        AbstractTranslet translet = this._translet;
        int index = this._currentIndex;
        while (index < last) {
            int node;
            int position;
            int n = position = this._docOrder ? index + 1 : last - index;
            if (!this._filter.test(node = this._nodes.at(index++), position, last, currentNode, translet, this)) continue;
            this._currentIndex = index;
            return this.returnNode(node);
        }
        return -1;
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            this._startNode = node;
            this._source.setStartNode(this._startNode);
            this._nodes.clear();
            while ((node = this._source.next()) != -1) {
                this._nodes.add(node);
            }
            this._currentIndex = 0;
            this.resetPosition();
        }
        return this;
    }

    @Override
    public int getLast() {
        if (this._last == -1) {
            this._last = this.computePositionOfLast();
        }
        return this._last;
    }

    @Override
    public void setMark() {
        this._markedNode = this._currentIndex;
    }

    @Override
    public void gotoMark() {
        this._currentIndex = this._markedNode;
    }

    private int computePositionOfLast() {
        int last = this._nodes.cardinality();
        int currNode = this._currentNode;
        AbstractTranslet translet = this._translet;
        int lastPosition = this._position;
        int index = this._currentIndex;
        while (index < last) {
            int nodeIndex;
            int position;
            int n = position = this._docOrder ? index + 1 : last - index;
            if (!this._filter.test(nodeIndex = this._nodes.at(index++), position, last, currNode, translet, this)) continue;
            ++lastPosition;
        }
        return lastPosition;
    }
}

