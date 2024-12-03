/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import java.awt.Rectangle;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.style.BorderRadiusCorner;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.newtable.CollapsedBorderValue;

public class BorderPropertySet
extends RectPropertySet {
    public static final BorderPropertySet EMPTY_BORDER = new BorderPropertySet(0.0f, 0.0f, 0.0f, 0.0f);
    private IdentValue _topStyle;
    private IdentValue _rightStyle;
    private IdentValue _bottomStyle;
    private IdentValue _leftStyle;
    private FSColor _topColor;
    private FSColor _rightColor;
    private FSColor _bottomColor;
    private FSColor _leftColor;
    private BorderRadiusCorner _topLeft;
    private BorderRadiusCorner _topRight;
    private BorderRadiusCorner _bottomRight;
    private BorderRadiusCorner _bottomLeft;

    public BorderPropertySet(BorderPropertySet border) {
        this(border.top(), border.right(), border.bottom(), border.left());
        this._topStyle = border.topStyle();
        this._rightStyle = border.rightStyle();
        this._bottomStyle = border.bottomStyle();
        this._leftStyle = border.leftStyle();
        this._topColor = border.topColor();
        this._rightColor = border.rightColor();
        this._bottomColor = border.bottomColor();
        this._leftColor = border.leftColor();
        this._topLeft = border._topLeft;
        this._topRight = border._topRight;
        this._bottomLeft = border._bottomLeft;
        this._bottomRight = border._bottomRight;
    }

    public BorderPropertySet(float top, float right, float bottom, float left, BorderRadiusCorner topLeftCorner, BorderRadiusCorner topRightCorner, BorderRadiusCorner bottomRightCorner, BorderRadiusCorner bottomLeftCorner) {
        this._top = top;
        this._right = right;
        this._bottom = bottom;
        this._left = left;
        this._topLeft = topLeftCorner;
        this._topRight = topRightCorner;
        this._bottomLeft = bottomLeftCorner;
        this._bottomRight = bottomRightCorner;
    }

    public BorderPropertySet(float top, float right, float bottom, float left) {
        this._top = top;
        this._right = right;
        this._bottom = bottom;
        this._left = left;
        this._topLeft = new BorderRadiusCorner();
        this._topRight = new BorderRadiusCorner();
        this._bottomLeft = new BorderRadiusCorner();
        this._bottomRight = new BorderRadiusCorner();
    }

    public BorderPropertySet(CollapsedBorderValue top, CollapsedBorderValue right, CollapsedBorderValue bottom, CollapsedBorderValue left) {
        this(top.width(), right.width(), bottom.width(), left.width());
        this._topStyle = top.style();
        this._rightStyle = right.style();
        this._bottomStyle = bottom.style();
        this._leftStyle = left.style();
        this._topColor = top.color();
        this._rightColor = right.color();
        this._bottomColor = bottom.color();
        this._leftColor = left.color();
        this._topLeft = new BorderRadiusCorner();
        this._topRight = new BorderRadiusCorner();
        this._bottomLeft = new BorderRadiusCorner();
        this._bottomRight = new BorderRadiusCorner();
    }

    private BorderPropertySet(CalculatedStyle style, CssContext ctx) {
        this._top = style.isIdent(CSSName.BORDER_TOP_STYLE, IdentValue.NONE) || style.isIdent(CSSName.BORDER_TOP_STYLE, IdentValue.HIDDEN) ? 0.0f : style.getFloatPropertyProportionalHeight(CSSName.BORDER_TOP_WIDTH, 0.0f, ctx);
        this._right = style.isIdent(CSSName.BORDER_RIGHT_STYLE, IdentValue.NONE) || style.isIdent(CSSName.BORDER_RIGHT_STYLE, IdentValue.HIDDEN) ? 0.0f : style.getFloatPropertyProportionalHeight(CSSName.BORDER_RIGHT_WIDTH, 0.0f, ctx);
        this._bottom = style.isIdent(CSSName.BORDER_BOTTOM_STYLE, IdentValue.NONE) || style.isIdent(CSSName.BORDER_BOTTOM_STYLE, IdentValue.HIDDEN) ? 0.0f : style.getFloatPropertyProportionalHeight(CSSName.BORDER_BOTTOM_WIDTH, 0.0f, ctx);
        this._left = style.isIdent(CSSName.BORDER_LEFT_STYLE, IdentValue.NONE) || style.isIdent(CSSName.BORDER_LEFT_STYLE, IdentValue.HIDDEN) ? 0.0f : style.getFloatPropertyProportionalHeight(CSSName.BORDER_LEFT_WIDTH, 0.0f, ctx);
        this._topColor = style.asColor(CSSName.BORDER_TOP_COLOR);
        this._rightColor = style.asColor(CSSName.BORDER_RIGHT_COLOR);
        this._bottomColor = style.asColor(CSSName.BORDER_BOTTOM_COLOR);
        this._leftColor = style.asColor(CSSName.BORDER_LEFT_COLOR);
        this._topStyle = style.getIdent(CSSName.BORDER_TOP_STYLE);
        this._rightStyle = style.getIdent(CSSName.BORDER_RIGHT_STYLE);
        this._bottomStyle = style.getIdent(CSSName.BORDER_BOTTOM_STYLE);
        this._leftStyle = style.getIdent(CSSName.BORDER_LEFT_STYLE);
        this._topLeft = new BorderRadiusCorner(CSSName.BORDER_TOP_LEFT_RADIUS, style, ctx);
        this._topRight = new BorderRadiusCorner(CSSName.BORDER_TOP_RIGHT_RADIUS, style, ctx);
        this._bottomLeft = new BorderRadiusCorner(CSSName.BORDER_BOTTOM_LEFT_RADIUS, style, ctx);
        this._bottomRight = new BorderRadiusCorner(CSSName.BORDER_BOTTOM_RIGHT_RADIUS, style, ctx);
    }

    public BorderPropertySet lighten(IdentValue style) {
        BorderPropertySet bc = new BorderPropertySet(this);
        bc._topColor = this._topColor == null ? null : this._topColor.lightenColor();
        bc._bottomColor = this._bottomColor == null ? null : this._bottomColor.lightenColor();
        bc._leftColor = this._leftColor == null ? null : this._leftColor.lightenColor();
        bc._rightColor = this._rightColor == null ? null : this._rightColor.lightenColor();
        return bc;
    }

    public BorderPropertySet darken(IdentValue style) {
        BorderPropertySet bc = new BorderPropertySet(this);
        bc._topColor = this._topColor == null ? null : this._topColor.darkenColor();
        bc._bottomColor = this._bottomColor == null ? null : this._bottomColor.darkenColor();
        bc._leftColor = this._leftColor == null ? null : this._leftColor.darkenColor();
        bc._rightColor = this._rightColor == null ? null : this._rightColor.darkenColor();
        return bc;
    }

    public static BorderPropertySet newInstance(CalculatedStyle style, CssContext ctx) {
        return new BorderPropertySet(style, ctx);
    }

    @Override
    public String toString() {
        return "BorderPropertySet[top=" + this._top + ",right=" + this._right + ",bottom=" + this._bottom + ",left=" + this._left + "]";
    }

    public boolean noTop() {
        return this._topStyle == IdentValue.NONE || (int)this._top == 0;
    }

    public boolean noRight() {
        return this._rightStyle == IdentValue.NONE || (int)this._right == 0;
    }

    public boolean noBottom() {
        return this._bottomStyle == IdentValue.NONE || (int)this._bottom == 0;
    }

    public boolean noLeft() {
        return this._leftStyle == IdentValue.NONE || (int)this._left == 0;
    }

    public IdentValue topStyle() {
        return this._topStyle;
    }

    public IdentValue rightStyle() {
        return this._rightStyle;
    }

    public IdentValue bottomStyle() {
        return this._bottomStyle;
    }

    public IdentValue leftStyle() {
        return this._leftStyle;
    }

    public FSColor topColor() {
        return this._topColor;
    }

    public FSColor rightColor() {
        return this._rightColor;
    }

    public FSColor bottomColor() {
        return this._bottomColor;
    }

    public FSColor leftColor() {
        return this._leftColor;
    }

    public boolean hasHidden() {
        return this._topStyle == IdentValue.HIDDEN || this._rightStyle == IdentValue.HIDDEN || this._bottomStyle == IdentValue.HIDDEN || this._leftStyle == IdentValue.HIDDEN;
    }

    public boolean hasBorderRadius() {
        return this.getTopLeft().hasRadius() || this.getTopRight().hasRadius() || this.getBottomLeft().hasRadius() || this.getBottomRight().hasRadius();
    }

    public BorderRadiusCorner getBottomRight() {
        return this._bottomRight;
    }

    public void setBottomRight(BorderRadiusCorner bottomRight) {
        this._bottomRight = bottomRight;
    }

    public BorderRadiusCorner getBottomLeft() {
        return this._bottomLeft;
    }

    public void setBottomLeft(BorderRadiusCorner bottomLeft) {
        this._bottomLeft = bottomLeft;
    }

    public BorderRadiusCorner getTopRight() {
        return this._topRight;
    }

    public void setTopRight(BorderRadiusCorner topRight) {
        this._topRight = topRight;
    }

    public BorderRadiusCorner getTopLeft() {
        return this._topLeft;
    }

    public void setTopLeft(BorderRadiusCorner topLeft) {
        this._topLeft = topLeft;
    }

    public BorderPropertySet normalizedInstance(Rectangle bounds) {
        float factor = 1.0f;
        factor = Math.min(factor, (float)bounds.width / this.getSideWidth(this._topLeft, this._topRight, bounds.width));
        factor = Math.min(factor, (float)bounds.width / this.getSideWidth(this._bottomRight, this._bottomLeft, bounds.width));
        factor = Math.min(factor, (float)bounds.height / this.getSideWidth(this._topRight, this._bottomRight, bounds.height));
        factor = Math.min(factor, (float)bounds.height / this.getSideWidth(this._bottomLeft, this._bottomRight, bounds.height));
        BorderPropertySet newPropSet = new BorderPropertySet(this._top, this._right, this._bottom, this._left, new BorderRadiusCorner(factor * this._topLeft.getMaxLeft(bounds.height), factor * this._topLeft.getMaxRight(bounds.width)), new BorderRadiusCorner(factor * this._topRight.getMaxLeft(bounds.width), factor * this._topRight.getMaxRight(bounds.height)), new BorderRadiusCorner(factor * this._bottomRight.getMaxLeft(bounds.height), factor * this._bottomRight.getMaxRight(bounds.width)), new BorderRadiusCorner(factor * this._bottomLeft.getMaxLeft(bounds.width), factor * this._bottomLeft.getMaxRight(bounds.height)));
        newPropSet._topColor = this._topColor;
        newPropSet._rightColor = this._rightColor;
        newPropSet._bottomColor = this._bottomColor;
        newPropSet._leftColor = this._leftColor;
        newPropSet._topStyle = this._topStyle;
        newPropSet._rightStyle = this._rightStyle;
        newPropSet._bottomStyle = this._bottomStyle;
        newPropSet._leftStyle = this._leftStyle;
        return newPropSet;
    }

    private float getSideWidth(BorderRadiusCorner left, BorderRadiusCorner right, float sideWidth) {
        return Math.max(sideWidth, left.getMaxRight(sideWidth) + right.getMaxLeft(sideWidth));
    }
}

