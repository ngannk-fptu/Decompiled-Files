/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.dom.NodeCounter;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xml.dtm.DTMAxisIterator;

public abstract class MultipleNodeCounter
extends NodeCounter {
    private DTMAxisIterator _precSiblings = null;

    public MultipleNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
        super(translet, document, iterator);
    }

    @Override
    public NodeCounter setStartNode(int node) {
        this._node = node;
        this._nodeType = this._document.getExpandedTypeID(node);
        this._precSiblings = this._document.getAxisIterator(12);
        return this;
    }

    @Override
    public String getCounter() {
        if (this._value != -2.147483648E9) {
            if (this._value == 0.0) {
                return "0";
            }
            if (Double.isNaN(this._value)) {
                return "NaN";
            }
            if (this._value < 0.0 && Double.isInfinite(this._value)) {
                return "-Infinity";
            }
            if (Double.isInfinite(this._value)) {
                return "Infinity";
            }
            return this.formatNumbers((int)this._value);
        }
        IntegerArray ancestors = new IntegerArray();
        int next = this._node;
        ancestors.add(next);
        while ((next = this._document.getParent(next)) > -1 && !this.matchesFrom(next)) {
            ancestors.add(next);
        }
        int nAncestors = ancestors.cardinality();
        int[] counters = new int[nAncestors];
        for (int i = 0; i < nAncestors; ++i) {
            counters[i] = Integer.MIN_VALUE;
        }
        int j = 0;
        int i = nAncestors - 1;
        while (i >= 0) {
            int counter = counters[j];
            int ancestor = ancestors.at(i);
            if (this.matchesCount(ancestor)) {
                this._precSiblings.setStartNode(ancestor);
                while ((next = this._precSiblings.next()) != -1) {
                    if (!this.matchesCount(next)) continue;
                    counters[j] = counters[j] == Integer.MIN_VALUE ? 1 : counters[j] + 1;
                }
                counters[j] = counters[j] == Integer.MIN_VALUE ? 1 : counters[j] + 1;
            }
            --i;
            ++j;
        }
        return this.formatNumbers(counters);
    }

    public static NodeCounter getDefaultNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
        return new DefaultMultipleNodeCounter(translet, document, iterator);
    }

    static class DefaultMultipleNodeCounter
    extends MultipleNodeCounter {
        public DefaultMultipleNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
            super(translet, document, iterator);
        }
    }
}

