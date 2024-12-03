/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.Color;

public class RectangleReadOnly
extends Rectangle {
    public RectangleReadOnly(float llx, float lly, float urx, float ury) {
        super(llx, lly, urx, ury);
    }

    public RectangleReadOnly(float urx, float ury) {
        super(0.0f, 0.0f, urx, ury);
    }

    public RectangleReadOnly(Rectangle rect) {
        super(rect.llx, rect.lly, rect.urx, rect.ury);
        super.cloneNonPositionParameters(rect);
    }

    private void throwReadOnlyError() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("rectanglereadonly.this.rectangle.is.read.only"));
    }

    @Override
    public void setLeft(float llx) {
        this.throwReadOnlyError();
    }

    @Override
    public void setRight(float urx) {
        this.throwReadOnlyError();
    }

    @Override
    public void setTop(float ury) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBottom(float lly) {
        this.throwReadOnlyError();
    }

    @Override
    public void normalize() {
        this.throwReadOnlyError();
    }

    @Override
    public void setBackgroundColor(Color value) {
        this.throwReadOnlyError();
    }

    @Override
    public void setGrayFill(float value) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorder(int border) {
        this.throwReadOnlyError();
    }

    @Override
    public void setUseVariableBorders(boolean useVariableBorders) {
        this.throwReadOnlyError();
    }

    @Override
    public void enableBorderSide(int side) {
        this.throwReadOnlyError();
    }

    @Override
    public void disableBorderSide(int side) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderWidth(float borderWidth) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderWidthLeft(float borderWidthLeft) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderWidthRight(float borderWidthRight) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderWidthTop(float borderWidthTop) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderWidthBottom(float borderWidthBottom) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderColor(Color borderColor) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderColorLeft(Color borderColorLeft) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderColorRight(Color borderColorRight) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderColorTop(Color borderColorTop) {
        this.throwReadOnlyError();
    }

    @Override
    public void setBorderColorBottom(Color borderColorBottom) {
        this.throwReadOnlyError();
    }

    @Override
    public void cloneNonPositionParameters(Rectangle rect) {
        this.throwReadOnlyError();
    }

    @Override
    public void softCloneNonPositionParameters(Rectangle rect) {
        this.throwReadOnlyError();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("RectangleReadOnly: ");
        buf.append(this.getWidth());
        buf.append('x');
        buf.append(this.getHeight());
        buf.append(" (rot: ");
        buf.append(this.rotation);
        buf.append(" degrees)");
        return buf.toString();
    }
}

