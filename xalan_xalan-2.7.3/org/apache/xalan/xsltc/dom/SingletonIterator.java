/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public class SingletonIterator
extends DTMAxisIteratorBase {
    private int _node;
    private final boolean _isConstant;

    public SingletonIterator() {
        this(Integer.MIN_VALUE, false);
    }

    public SingletonIterator(int node) {
        this(node, false);
    }

    public SingletonIterator(int node, boolean constant) {
        this._node = this._startNode = node;
        this._isConstant = constant;
    }

    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isConstant) {
            this._node = this._startNode;
            return this.resetPosition();
        }
        if (this._isRestartable) {
            if (this._node <= 0) {
                this._node = this._startNode = node;
            }
            return this.resetPosition();
        }
        return this;
    }

    @Override
    public DTMAxisIterator reset() {
        if (this._isConstant) {
            this._node = this._startNode;
            return this.resetPosition();
        }
        boolean temp = this._isRestartable;
        this._isRestartable = true;
        this.setStartNode(this._startNode);
        this._isRestartable = temp;
        return this;
    }

    @Override
    public int next() {
        int result = this._node;
        this._node = -1;
        return this.returnNode(result);
    }

    @Override
    public void setMark() {
        this._markedNode = this._node;
    }

    @Override
    public void gotoMark() {
        this._node = this._markedNode;
    }
}

