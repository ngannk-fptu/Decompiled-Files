/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class OpacityManager
extends AbstractValueManager {
    protected boolean inherited;
    protected String property;

    public OpacityManager(String prop, boolean inherit) {
        this.property = prop;
        this.inherited = inherit;
    }

    @Override
    public boolean isInheritedProperty() {
        return this.inherited;
    }

    @Override
    public boolean isAnimatableProperty() {
        return true;
    }

    @Override
    public boolean isAdditiveProperty() {
        return true;
    }

    @Override
    public int getPropertyType() {
        return 25;
    }

    @Override
    public String getPropertyName() {
        return this.property;
    }

    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.NUMBER_1;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 13: {
                return new FloatValue(1, lu.getIntegerValue());
            }
            case 14: {
                return new FloatValue(1, lu.getFloatValue());
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    @Override
    public Value createFloatValue(short type, float floatValue) throws DOMException {
        if (type == 1) {
            return new FloatValue(type, floatValue);
        }
        throw this.createInvalidFloatTypeDOMException(type);
    }
}

