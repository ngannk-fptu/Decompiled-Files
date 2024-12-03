/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xml.dtm.DTMAxisIterator;

public class ArrayNodeListIterator
implements DTMAxisIterator {
    private int _pos = 0;
    private int _mark = 0;
    private int[] _nodes;
    private static final int[] EMPTY = new int[0];

    public ArrayNodeListIterator(int[] nodes) {
        this._nodes = nodes;
    }

    @Override
    public int next() {
        return this._pos < this._nodes.length ? this._nodes[this._pos++] : -1;
    }

    @Override
    public DTMAxisIterator reset() {
        this._pos = 0;
        return this;
    }

    @Override
    public int getLast() {
        return this._nodes.length;
    }

    @Override
    public int getPosition() {
        return this._pos;
    }

    @Override
    public void setMark() {
        this._mark = this._pos;
    }

    @Override
    public void gotoMark() {
        this._pos = this._mark;
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (node == -1) {
            this._nodes = EMPTY;
        }
        return this;
    }

    @Override
    public int getStartNode() {
        return -1;
    }

    @Override
    public boolean isReverse() {
        return false;
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        return new ArrayNodeListIterator(this._nodes);
    }

    @Override
    public void setRestartable(boolean isRestartable) {
    }

    @Override
    public int getNodeByPosition(int position) {
        return this._nodes[position - 1];
    }
}

