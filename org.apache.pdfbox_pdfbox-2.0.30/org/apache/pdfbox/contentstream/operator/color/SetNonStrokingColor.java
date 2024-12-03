/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.color;

import org.apache.pdfbox.contentstream.operator.color.SetColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public class SetNonStrokingColor
extends SetColor {
    @Override
    protected PDColor getColor() {
        return this.context.getGraphicsState().getNonStrokingColor();
    }

    @Override
    protected void setColor(PDColor color) {
        this.context.getGraphicsState().setNonStrokingColor(color);
    }

    @Override
    protected PDColorSpace getColorSpace() {
        return this.context.getGraphicsState().getNonStrokingColorSpace();
    }

    @Override
    public String getName() {
        return "sc";
    }
}

