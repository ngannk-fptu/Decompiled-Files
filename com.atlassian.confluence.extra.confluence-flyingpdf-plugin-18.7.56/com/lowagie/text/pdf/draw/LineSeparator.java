/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.draw;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.draw.VerticalPositionMark;
import java.awt.Color;

public class LineSeparator
extends VerticalPositionMark {
    protected float lineWidth = 1.0f;
    protected float percentage = 100.0f;
    protected Color lineColor;
    protected int alignment = 1;

    public LineSeparator(float lineWidth, float percentage, Color lineColor, int align, float offset) {
        this.lineWidth = lineWidth;
        this.percentage = percentage;
        this.lineColor = lineColor;
        this.alignment = align;
        this.offset = offset;
    }

    public LineSeparator() {
    }

    @Override
    public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
        canvas.saveState();
        this.drawLine(canvas, llx, urx, y);
        canvas.restoreState();
    }

    public void drawLine(PdfContentByte canvas, float leftX, float rightX, float y) {
        float s;
        float w = this.getPercentage() < 0.0f ? -this.getPercentage() : (rightX - leftX) * this.getPercentage() / 100.0f;
        switch (this.getAlignment()) {
            case 0: {
                s = 0.0f;
                break;
            }
            case 2: {
                s = rightX - leftX - w;
                break;
            }
            default: {
                s = (rightX - leftX - w) / 2.0f;
            }
        }
        canvas.setLineWidth(this.getLineWidth());
        if (this.getLineColor() != null) {
            canvas.setColorStroke(this.getLineColor());
        }
        canvas.moveTo(s + leftX, y + this.offset);
        canvas.lineTo(s + w + leftX, y + this.offset);
        canvas.stroke();
    }

    public float getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public float getPercentage() {
        return this.percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public Color getLineColor() {
        return this.lineColor;
    }

    public void setLineColor(Color color) {
        this.lineColor = color;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int align) {
        this.alignment = align;
    }
}

