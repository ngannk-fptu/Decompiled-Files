/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.dom.NodeCounter;
import org.apache.xml.dtm.DTMAxisIterator;

public abstract class AnyNodeCounter
extends NodeCounter {
    public AnyNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
        super(translet, document, iterator);
    }

    @Override
    public NodeCounter setStartNode(int node) {
        this._node = node;
        this._nodeType = this._document.getExpandedTypeID(node);
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
        int root = this._document.getDocument();
        int result = 0;
        for (int next = this._node; next >= root && !this.matchesFrom(next); --next) {
            if (!this.matchesCount(next)) continue;
            ++result;
        }
        return this.formatNumbers(result);
    }

    public static NodeCounter getDefaultNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
        return new DefaultAnyNodeCounter(translet, document, iterator);
    }

    static class DefaultAnyNodeCounter
    extends AnyNodeCounter {
        public DefaultAnyNodeCounter(Translet translet, DOM document, DTMAxisIterator iterator) {
            super(translet, document, iterator);
        }

        @Override
        public String getCounter() {
            int result;
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
                result = (int)this._value;
            } else {
                result = 0;
                int ntype = this._document.getExpandedTypeID(this._node);
                int root = this._document.getDocument();
                for (int next = this._node; next >= 0; --next) {
                    if (ntype == this._document.getExpandedTypeID(next)) {
                        ++result;
                    }
                    if (next == root) break;
                }
            }
            return this.formatNumbers(result);
        }
    }
}

