/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueManager;
import org.w3c.dom.DOMException;

public abstract class AbstractValueManager
extends AbstractValueFactory
implements ValueManager {
    @Override
    public Value createFloatValue(short unitType, float floatValue) throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value createStringValue(short type, String value, CSSEngine engine) throws DOMException {
        throw this.createDOMException();
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        if (value.getCssValueType() == 1 && value.getPrimitiveType() == 20) {
            return new URIValue(value.getStringValue(), value.getStringValue());
        }
        return value;
    }
}

