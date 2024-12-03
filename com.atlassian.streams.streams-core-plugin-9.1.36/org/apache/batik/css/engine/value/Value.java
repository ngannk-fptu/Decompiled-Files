/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;

public interface Value {
    public String getCssText();

    public short getCssValueType();

    public short getPrimitiveType();

    public float getFloatValue() throws DOMException;

    public String getStringValue() throws DOMException;

    public Value getRed() throws DOMException;

    public Value getGreen() throws DOMException;

    public Value getBlue() throws DOMException;

    public int getLength() throws DOMException;

    public Value item(int var1) throws DOMException;

    public Value getTop() throws DOMException;

    public Value getRight() throws DOMException;

    public Value getBottom() throws DOMException;

    public Value getLeft() throws DOMException;

    public String getIdentifier() throws DOMException;

    public String getListStyle() throws DOMException;

    public String getSeparator() throws DOMException;
}

