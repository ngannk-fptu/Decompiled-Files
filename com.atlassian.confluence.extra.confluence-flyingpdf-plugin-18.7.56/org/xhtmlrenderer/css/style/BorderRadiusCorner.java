/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.ListValue;

public class BorderRadiusCorner {
    private boolean _leftPercent = false;
    private boolean _rightPercent = false;
    private float _left;
    private float _right;

    public BorderRadiusCorner() {
    }

    public BorderRadiusCorner(float left, float right) {
        this._left = left;
        this._right = right;
        this._rightPercent = false;
        this._rightPercent = false;
    }

    public BorderRadiusCorner(CSSName fromVal, CalculatedStyle style, CssContext ctx) {
        FSDerivedValue value = style.valueByName(fromVal);
        PropertyValue first = null;
        PropertyValue second = null;
        if (value instanceof ListValue) {
            ListValue lValues = (ListValue)value;
            first = (PropertyValue)lValues.getValues().get(0);
            second = lValues.getValues().size() > 1 ? (PropertyValue)lValues.getValues().get(1) : first;
            if (fromVal.equals(CSSName.BORDER_TOP_LEFT_RADIUS) || fromVal.equals(CSSName.BORDER_BOTTOM_RIGHT_RADIUS)) {
                this.setRight(fromVal, style, first, ctx);
                this.setLeft(fromVal, style, second, ctx);
            } else {
                this.setLeft(fromVal, style, first, ctx);
                this.setRight(fromVal, style, second, ctx);
            }
        } else if (value instanceof LengthValue) {
            LengthValue lv = (LengthValue)value;
            if (lv.getStringValue().contains("%")) {
                this._rightPercent = true;
                this._leftPercent = true;
                this._left = this._right = value.asFloat() / 100.0f;
            } else {
                this._left = this._right = (float)((int)lv.getFloatProportionalTo(fromVal, 0.0f, ctx));
            }
        }
    }

    private void setLeft(CSSName fromVal, CalculatedStyle style, PropertyValue value, CssContext ctx) {
        if (value.getPrimitiveType() == 2) {
            this._leftPercent = true;
            this._left = value.getFloatValue() / 100.0f;
        } else {
            this._left = (int)LengthValue.calcFloatProportionalValue(style, fromVal, value.getCssText(), value.getFloatValue(), value.getPrimitiveType(), 0.0f, ctx);
        }
    }

    private void setRight(CSSName fromVal, CalculatedStyle style, PropertyValue value, CssContext ctx) {
        if (value.getPrimitiveType() == 2) {
            float percent = value.getFloatValue() / 100.0f;
            this._rightPercent = true;
            this._right = value.getFloatValue() / 100.0f;
        } else {
            this._right = (int)LengthValue.calcFloatProportionalValue(style, fromVal, value.getCssText(), value.getFloatValue(), value.getPrimitiveType(), 0.0f, ctx);
        }
    }

    public boolean hasRadius() {
        return this._left > 0.0f || this._right > 0.0f;
    }

    public float getMaxLeft(float max) {
        if (this._leftPercent) {
            return max * this._left;
        }
        if (this._left > max) {
            return max;
        }
        return this._left;
    }

    public float getMaxRight(float max) {
        if (this._rightPercent) {
            return max * this._right;
        }
        if (this._right > max) {
            return max;
        }
        return this._right;
    }

    public float left() {
        return this._left;
    }

    public float right() {
        return this._right;
    }
}

