/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.dom.MultiValuedNodeHeapIterator;
import org.apache.xml.dtm.DTMAxisIterator;

public final class UnionIterator
extends MultiValuedNodeHeapIterator {
    private final DOM _dom;

    public UnionIterator(DOM dom) {
        this._dom = dom;
    }

    public UnionIterator addIterator(DTMAxisIterator iterator) {
        this.addHeapNode(new LookAheadIterator(iterator));
        return this;
    }

    private final class LookAheadIterator
    extends MultiValuedNodeHeapIterator.HeapNode {
        public DTMAxisIterator iterator;

        public LookAheadIterator(DTMAxisIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public int step() {
            this._node = this.iterator.next();
            return this._node;
        }

        @Override
        public MultiValuedNodeHeapIterator.HeapNode cloneHeapNode() {
            LookAheadIterator clone = (LookAheadIterator)super.cloneHeapNode();
            clone.iterator = this.iterator.cloneIterator();
            return clone;
        }

        @Override
        public void setMark() {
            super.setMark();
            this.iterator.setMark();
        }

        @Override
        public void gotoMark() {
            super.gotoMark();
            this.iterator.gotoMark();
        }

        @Override
        public boolean isLessThan(MultiValuedNodeHeapIterator.HeapNode heapNode) {
            LookAheadIterator comparand = (LookAheadIterator)heapNode;
            return UnionIterator.this._dom.lessThan(this._node, heapNode._node);
        }

        @Override
        public MultiValuedNodeHeapIterator.HeapNode setStartNode(int node) {
            this.iterator.setStartNode(node);
            return this;
        }

        @Override
        public MultiValuedNodeHeapIterator.HeapNode reset() {
            this.iterator.reset();
            return this;
        }
    }
}

