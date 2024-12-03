/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.value.Messages;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;

public abstract class AbstractValue
implements Value {
    @Override
    public short getCssValueType() {
        return 1;
    }

    @Override
    public short getPrimitiveType() {
        throw this.createDOMException();
    }

    @Override
    public float getFloatValue() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public String getStringValue() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value getRed() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value getGreen() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value getBlue() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public int getLength() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value item(int index) throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value getTop() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value getRight() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value getBottom() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value getLeft() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public String getIdentifier() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public String getListStyle() throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public String getSeparator() throws DOMException {
        throw this.createDOMException();
    }

    protected DOMException createDOMException() {
        Object[] p = new Object[]{(int)this.getCssValueType()};
        String s = Messages.formatMessage("invalid.value.access", p);
        return new DOMException(15, s);
    }
}

