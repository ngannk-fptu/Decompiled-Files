/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.constants.ValueConstants;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.util.XRRuntimeException;

public abstract class DerivedValue
implements FSDerivedValue {
    private String _asString;
    private short _cssSacUnitType;

    protected DerivedValue() {
    }

    protected DerivedValue(CSSName name, short cssSACUnitType, String cssText, String cssStringValue) {
        this._cssSacUnitType = cssSACUnitType;
        if (cssText == null) {
            throw new XRRuntimeException("CSSValue for '" + name + "' is null after resolving CSS identifier for value '" + cssStringValue + "'");
        }
        this._asString = this.deriveStringValue(cssText, cssStringValue);
    }

    private String deriveStringValue(String cssText, String cssStringValue) {
        switch (this._cssSacUnitType) {
            case 19: 
            case 20: 
            case 21: 
            case 22: {
                return cssStringValue == null ? cssText : cssStringValue;
            }
        }
        return cssText;
    }

    public String getStringValue() {
        return this._asString;
    }

    @Override
    public boolean isDeclaredInherit() {
        return false;
    }

    public short getCssSacUnitType() {
        return this._cssSacUnitType;
    }

    public boolean isAbsoluteUnit() {
        return ValueConstants.isAbsoluteUnit(this._cssSacUnitType);
    }

    @Override
    public float asFloat() {
        throw new XRRuntimeException("asFloat() needs to be overridden in subclass.");
    }

    @Override
    public FSColor asColor() {
        throw new XRRuntimeException("asColor() needs to be overridden in subclass.");
    }

    @Override
    public float getFloatProportionalTo(CSSName cssName, float baseValue, CssContext ctx) {
        throw new XRRuntimeException("getFloatProportionalTo() needs to be overridden in subclass.");
    }

    @Override
    public String asString() {
        return this.getStringValue();
    }

    @Override
    public String[] asStringArray() {
        throw new XRRuntimeException("asStringArray() needs to be overridden in subclass.");
    }

    @Override
    public IdentValue asIdentValue() {
        throw new XRRuntimeException("asIdentValue() needs to be overridden in subclass.");
    }

    @Override
    public boolean hasAbsoluteUnit() {
        throw new XRRuntimeException("hasAbsoluteUnit() needs to be overridden in subclass.");
    }

    @Override
    public boolean isIdent() {
        return false;
    }

    @Override
    public boolean isDependentOnFontSize() {
        return false;
    }
}

