/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.ColorManager;
import org.apache.batik.css.engine.value.svg.ICCColor;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.apache.batik.css.engine.value.svg12.CIELCHColor;
import org.apache.batik.css.engine.value.svg12.CIELabColor;
import org.apache.batik.css.engine.value.svg12.DeviceColor;
import org.apache.batik.css.engine.value.svg12.ICCNamedColor;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public class SVGColorManager
extends ColorManager {
    protected String property;
    protected Value defaultValue;

    public SVGColorManager(String prop) {
        this(prop, SVGValueConstants.BLACK_RGB_VALUE);
    }

    public SVGColorManager(String prop, Value v) {
        this.property = prop;
        this.defaultValue = v;
    }

    @Override
    public boolean isInheritedProperty() {
        return false;
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
        return 6;
    }

    @Override
    public String getPropertyName() {
        return this.property;
    }

    @Override
    public Value getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public Value createValue(LexicalUnit lu, CSSEngine engine) throws DOMException {
        if (lu.getLexicalUnitType() == 35 && lu.getStringValue().equalsIgnoreCase("currentcolor")) {
            return SVGValueConstants.CURRENTCOLOR_VALUE;
        }
        Value v = super.createValue(lu, engine);
        if ((lu = lu.getNextLexicalUnit()) == null) {
            return v;
        }
        if (lu.getLexicalUnitType() != 41) {
            throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        }
        ListValue result = new ListValue(' ');
        result.append(v);
        Value colorValue = this.parseColorFunction(lu, v);
        if (colorValue == null) {
            return v;
        }
        result.append(colorValue);
        return result;
    }

    private Value parseColorFunction(LexicalUnit lu, Value v) {
        String functionName = lu.getFunctionName();
        if (functionName.equalsIgnoreCase("icc-color")) {
            return this.createICCColorValue(lu, v);
        }
        return this.parseColor12Function(lu, v);
    }

    private Value parseColor12Function(LexicalUnit lu, Value v) {
        String functionName = lu.getFunctionName();
        if (functionName.equalsIgnoreCase("icc-named-color")) {
            return this.createICCNamedColorValue(lu, v);
        }
        if (functionName.equalsIgnoreCase("cielab")) {
            return this.createCIELabColorValue(lu, v);
        }
        if (functionName.equalsIgnoreCase("cielch")) {
            return this.createCIELCHColorValue(lu, v);
        }
        if (functionName.equalsIgnoreCase("device-cmyk")) {
            return this.createDeviceColorValue(lu, v, 4);
        }
        if (functionName.equalsIgnoreCase("device-rgb")) {
            return this.createDeviceColorValue(lu, v, 3);
        }
        if (functionName.equalsIgnoreCase("device-gray")) {
            return this.createDeviceColorValue(lu, v, 1);
        }
        if (functionName.equalsIgnoreCase("device-nchannel")) {
            return this.createDeviceColorValue(lu, v, 0);
        }
        return null;
    }

    private Value createICCColorValue(LexicalUnit lu, Value v) {
        lu = lu.getParameters();
        this.expectIdent(lu);
        ICCColor icc = new ICCColor(lu.getStringValue());
        for (lu = lu.getNextLexicalUnit(); lu != null; lu = lu.getNextLexicalUnit()) {
            this.expectComma(lu);
            lu = lu.getNextLexicalUnit();
            icc.append(this.getColorValue(lu));
        }
        return icc;
    }

    private Value createICCNamedColorValue(LexicalUnit lu, Value v) {
        lu = lu.getParameters();
        this.expectIdent(lu);
        String profileName = lu.getStringValue();
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        this.expectIdent(lu);
        String colorName = lu.getStringValue();
        ICCNamedColor icc = new ICCNamedColor(profileName, colorName);
        lu = lu.getNextLexicalUnit();
        return icc;
    }

    private Value createCIELabColorValue(LexicalUnit lu, Value v) {
        lu = lu.getParameters();
        float l = this.getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        float a = this.getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        float b = this.getColorValue(lu);
        CIELabColor icc = new CIELabColor(l, a, b);
        lu = lu.getNextLexicalUnit();
        return icc;
    }

    private Value createCIELCHColorValue(LexicalUnit lu, Value v) {
        lu = lu.getParameters();
        float l = this.getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        float c = this.getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        float h = this.getColorValue(lu);
        CIELCHColor icc = new CIELCHColor(l, c, h);
        lu = lu.getNextLexicalUnit();
        return icc;
    }

    private Value createDeviceColorValue(LexicalUnit lu, Value v, int expectedComponents) {
        lu = lu.getParameters();
        boolean nChannel = expectedComponents <= 0;
        DeviceColor col = new DeviceColor(nChannel);
        col.append(this.getColorValue(lu));
        LexicalUnit lastUnit = lu;
        for (lu = lu.getNextLexicalUnit(); lu != null; lu = lu.getNextLexicalUnit()) {
            this.expectComma(lu);
            lu = lu.getNextLexicalUnit();
            col.append(this.getColorValue(lu));
            lastUnit = lu;
        }
        if (!nChannel && expectedComponents != col.getNumberOfColors()) {
            throw this.createInvalidLexicalUnitDOMException(lastUnit.getLexicalUnitType());
        }
        return col;
    }

    private void expectIdent(LexicalUnit lu) {
        if (lu.getLexicalUnitType() != 35) {
            throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        }
    }

    private void expectComma(LexicalUnit lu) {
        if (lu.getLexicalUnitType() != 0) {
            throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        }
    }

    private void expectNonNull(LexicalUnit lu) {
        if (lu == null) {
            throw this.createInvalidLexicalUnitDOMException((short)-1);
        }
    }

    @Override
    public Value computeValue(CSSStylableElement elt, String pseudo, CSSEngine engine, int idx, StyleMap sm, Value value) {
        if (value == SVGValueConstants.CURRENTCOLOR_VALUE) {
            sm.putColorRelative(idx, true);
            int ci = engine.getColorIndex();
            return engine.getComputedStyle(elt, pseudo, ci);
        }
        if (value.getCssValueType() == 2) {
            ListValue lv = (ListValue)value;
            Value v = lv.item(0);
            Value t = super.computeValue(elt, pseudo, engine, idx, sm, v);
            if (t != v) {
                ListValue result = new ListValue(' ');
                result.append(t);
                result.append(lv.item(1));
                return result;
            }
            return value;
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }

    protected float getColorValue(LexicalUnit lu) {
        this.expectNonNull(lu);
        switch (lu.getLexicalUnitType()) {
            case 13: {
                return lu.getIntegerValue();
            }
            case 14: {
                return lu.getFloatValue();
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }
}

