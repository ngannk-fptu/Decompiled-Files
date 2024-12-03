/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.dom.NodeSortRecord;
import org.apache.xalan.xsltc.dom.NodeSortRecordFactory;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public final class SortingIterator
extends DTMAxisIteratorBase {
    private static final int INIT_DATA_SIZE = 16;
    private DTMAxisIterator _source;
    private NodeSortRecordFactory _factory;
    private NodeSortRecord[] _data;
    private int _free = 0;
    private int _current;

    public SortingIterator(DTMAxisIterator source, NodeSortRecordFactory factory) {
        this._source = source;
        this._factory = factory;
    }

    @Override
    public int next() {
        return this._current < this._free ? this._data[this._current++].getNode() : -1;
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        try {
            this._startNode = node;
            this._source.setStartNode(this._startNode);
            this._data = new NodeSortRecord[16];
            this._free = 0;
            while ((node = this._source.next()) != -1) {
                this.addRecord(this._factory.makeNodeSortRecord(node, this._free));
            }
            this.quicksort(0, this._free - 1);
            this._current = 0;
            return this;
        }
        catch (Exception e) {
            return this;
        }
    }

    @Override
    public int getPosition() {
        return this._current == 0 ? 1 : this._current;
    }

    @Override
    public int getLast() {
        return this._free;
    }

    @Override
    public void setMark() {
        this._source.setMark();
        this._markedNode = this._current;
    }

    @Override
    public void gotoMark() {
        this._source.gotoMark();
        this._current = this._markedNode;
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            SortingIterator clone = (SortingIterator)super.clone();
            clone._source = this._source.cloneIterator();
            clone._factory = this._factory;
            clone._data = this._data;
            clone._free = this._free;
            clone._current = this._current;
            clone.setRestartable(false);
            return clone.reset();
        }
        catch (CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }

    private void addRecord(NodeSortRecord record) {
        if (this._free == this._data.length) {
            NodeSortRecord[] newArray = new NodeSortRecord[this._data.length * 2];
            System.arraycopy(this._data, 0, newArray, 0, this._free);
            this._data = newArray;
        }
        this._data[this._free++] = record;
    }

    private void quicksort(int p, int r) {
        while (p < r) {
            int q = this.partition(p, r);
            this.quicksort(p, q);
            p = q + 1;
        }
    }

    private int partition(int p, int r) {
        NodeSortRecord x = this._data[p + r >>> 1];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            if (x.compareTo(this._data[--j]) < 0) {
                continue;
            }
            while (x.compareTo(this._data[++i]) > 0) {
            }
            if (i >= j) break;
            NodeSortRecord t = this._data[i];
            this._data[i] = this._data[j];
            this._data[j] = t;
        }
        return j;
    }
}

