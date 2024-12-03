/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;

public class RectPropertySet {
    public static final RectPropertySet ALL_ZEROS = new RectPropertySet(CSSName.MARGIN_SHORTHAND, 0.0f, 0.0f, 0.0f, 0.0f);
    protected float _top = 0.0f;
    protected float _right = 0.0f;
    protected float _bottom = 0.0f;
    protected float _left = 0.0f;

    protected RectPropertySet() {
    }

    public RectPropertySet(CSSName cssName, float top, float right, float bottom, float left) {
        this();
        this._top = top;
        this._right = right;
        this._bottom = bottom;
        this._left = left;
    }

    public static RectPropertySet newInstance(CalculatedStyle style, CSSName shortHandProperty, CSSName.CSSSideProperties sideProperties, float cbWidth, CssContext ctx) {
        RectPropertySet rect = new RectPropertySet(shortHandProperty, !style.isLengthOrNumber(sideProperties.top) ? 0.0f : style.getFloatPropertyProportionalHeight(sideProperties.top, cbWidth, ctx), !style.isLengthOrNumber(sideProperties.right) ? 0.0f : style.getFloatPropertyProportionalWidth(sideProperties.right, cbWidth, ctx), !style.isLengthOrNumber(sideProperties.bottom) ? 0.0f : style.getFloatPropertyProportionalHeight(sideProperties.bottom, cbWidth, ctx), !style.isLengthOrNumber(sideProperties.left) ? 0.0f : style.getFloatPropertyProportionalWidth(sideProperties.left, cbWidth, ctx));
        return rect;
    }

    public String toString() {
        return "RectPropertySet[top=" + this._top + ",right=" + this._right + ",bottom=" + this._bottom + ",left=" + this._left + "]";
    }

    public float top() {
        return this._top;
    }

    public float right() {
        return this._right;
    }

    public float bottom() {
        return this._bottom;
    }

    public float left() {
        return this._left;
    }

    public float getLeftRightDiff() {
        return this._left - this._right;
    }

    public float height() {
        return this._top + this._bottom;
    }

    public float width() {
        return this._left + this._right;
    }

    public void setTop(float _top) {
        this._top = _top;
    }

    public void setRight(float _right) {
        this._right = _right;
    }

    public void setBottom(float _bottom) {
        this._bottom = _bottom;
    }

    public void setLeft(float _left) {
        this._left = _left;
    }

    public RectPropertySet copyOf() {
        RectPropertySet newRect = new RectPropertySet();
        newRect._top = this._top;
        newRect._right = this._right;
        newRect._bottom = this._bottom;
        newRect._left = this._left;
        return newRect;
    }

    public boolean isAllZeros() {
        return this._top == 0.0f && this._right == 0.0f && this._bottom == 0.0f && this._left == 0.0f;
    }

    public boolean hasNegativeValues() {
        return this._top < 0.0f || this._right < 0.0f || this._bottom < 0.0f || this._left < 0.0f;
    }

    public void resetNegativeValues() {
        if (this.top() < 0.0f) {
            this.setTop(0.0f);
        }
        if (this.right() < 0.0f) {
            this.setRight(0.0f);
        }
        if (this.bottom() < 0.0f) {
            this.setBottom(0.0f);
        }
        if (this.left() < 0.0f) {
            this.setLeft(0.0f);
        }
    }
}

