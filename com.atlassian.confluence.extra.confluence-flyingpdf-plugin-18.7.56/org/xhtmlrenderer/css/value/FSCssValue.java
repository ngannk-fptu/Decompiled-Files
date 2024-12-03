/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.value;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;
import org.xhtmlrenderer.util.XRRuntimeException;

public class FSCssValue
implements CSSPrimitiveValue {
    private String _cssText;
    private Counter counter;
    private float floatValue;
    private short primitiveType;
    private Rect rectValue;
    private RGBColor rgbColorValue;

    public FSCssValue(CSSPrimitiveValue primitive) {
        this.primitiveType = primitive.getPrimitiveType();
        this._cssText = this.primitiveType == 19 ? primitive.getStringValue() : primitive.getCssText();
        switch (this.primitiveType) {
            case 25: {
                this.rgbColorValue = primitive.getRGBColorValue();
                break;
            }
            case 21: {
                break;
            }
            case 19: {
                break;
            }
            case 23: {
                this.counter = primitive.getCounterValue();
                break;
            }
            case 24: {
                this.rectValue = primitive.getRectValue();
                break;
            }
            case 20: {
                this._cssText = primitive.getStringValue();
                break;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: {
                this.floatValue = primitive.getFloatValue(this.primitiveType);
                break;
            }
        }
        if (this._cssText == null) {
            throw new XRRuntimeException("CSSText is null for " + primitive + "   csstext " + primitive.getCssText() + "   string value " + primitive.getStringValue());
        }
    }

    public FSCssValue(CSSPrimitiveValue primitive, String newValue) {
        this(primitive);
        this._cssText = newValue;
    }

    FSCssValue(short primitiveType, String value) {
        this.primitiveType = primitiveType;
        this._cssText = value;
    }

    public static FSCssValue getNewIdentValue(String identValue) {
        return new FSCssValue(21, identValue);
    }

    public String toString() {
        return this.getCssText();
    }

    @Override
    public void setCssText(String cssText) {
        this._cssText = cssText;
    }

    @Override
    public void setFloatValue(short unitType, float floatValue) {
        throw new XRRuntimeException("FSCssValue is immutable.");
    }

    @Override
    public void setStringValue(short stringType, String stringValue) {
        throw new XRRuntimeException("FSCssValue is immutable.");
    }

    @Override
    public String getCssText() {
        return this._cssText;
    }

    @Override
    public short getCssValueType() {
        return 1;
    }

    @Override
    public Counter getCounterValue() {
        return this.counter;
    }

    @Override
    public float getFloatValue(short unitType) {
        return this.floatValue;
    }

    @Override
    public short getPrimitiveType() {
        return this.primitiveType;
    }

    @Override
    public Rect getRectValue() {
        return this.rectValue;
    }

    @Override
    public RGBColor getRGBColorValue() {
        return this.rgbColorValue;
    }

    @Override
    public String getStringValue() {
        return this._cssText;
    }
}

