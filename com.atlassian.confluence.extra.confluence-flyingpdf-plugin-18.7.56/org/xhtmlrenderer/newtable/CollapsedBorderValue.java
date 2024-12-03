/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.newtable;

import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;

public class CollapsedBorderValue {
    private IdentValue _style;
    private int _width;
    private FSColor _color;
    private int _precedence;

    public CollapsedBorderValue(IdentValue style, int width, FSColor color, int precedence) {
        this._style = style;
        this._width = width;
        this._color = color;
        this._precedence = precedence;
    }

    public FSColor color() {
        return this._color;
    }

    public void setColor(FSColor color) {
        this._color = color;
    }

    public IdentValue style() {
        return this._style;
    }

    public void setStyle(IdentValue style) {
        this._style = style;
    }

    public int width() {
        return this._width;
    }

    public void setWidth(int width) {
        this._width = width;
    }

    public int precedence() {
        return this._precedence;
    }

    public void setPrecedence(int precedence) {
        this._precedence = precedence;
    }

    public boolean defined() {
        return this._style != null;
    }

    public boolean exists() {
        return this._style != null && this._style != IdentValue.NONE && this._style != IdentValue.HIDDEN;
    }

    public boolean hidden() {
        return this._style == IdentValue.HIDDEN;
    }

    public static CollapsedBorderValue borderLeft(BorderPropertySet border, int precedence) {
        return new CollapsedBorderValue(border.leftStyle(), (int)border.left(), border.leftColor(), precedence);
    }

    public static CollapsedBorderValue borderRight(BorderPropertySet border, int precedence) {
        return new CollapsedBorderValue(border.rightStyle(), (int)border.right(), border.rightColor(), precedence);
    }

    public static CollapsedBorderValue borderTop(BorderPropertySet border, int precedence) {
        return new CollapsedBorderValue(border.topStyle(), (int)border.top(), border.topColor(), precedence);
    }

    public static CollapsedBorderValue borderBottom(BorderPropertySet border, int precedence) {
        return new CollapsedBorderValue(border.bottomStyle(), (int)border.bottom(), border.bottomColor(), precedence);
    }
}

