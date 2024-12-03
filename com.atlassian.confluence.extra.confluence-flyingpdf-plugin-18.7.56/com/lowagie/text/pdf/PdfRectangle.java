/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;

public class PdfRectangle
extends PdfArray {
    private float llx = 0.0f;
    private float lly = 0.0f;
    private float urx = 0.0f;
    private float ury = 0.0f;

    public PdfRectangle(float llx, float lly, float urx, float ury, int rotation) {
        if (rotation == 90 || rotation == 270) {
            this.llx = lly;
            this.lly = llx;
            this.urx = ury;
            this.ury = urx;
        } else {
            this.llx = llx;
            this.lly = lly;
            this.urx = urx;
            this.ury = ury;
        }
        super.add(new PdfNumber(this.llx));
        super.add(new PdfNumber(this.lly));
        super.add(new PdfNumber(this.urx));
        super.add(new PdfNumber(this.ury));
    }

    public PdfRectangle(float llx, float lly, float urx, float ury) {
        this(llx, lly, urx, ury, 0);
    }

    public PdfRectangle(float urx, float ury, int rotation) {
        this(0.0f, 0.0f, urx, ury, rotation);
    }

    public PdfRectangle(float urx, float ury) {
        this(0.0f, 0.0f, urx, ury, 0);
    }

    public PdfRectangle(Rectangle rectangle, int rotation) {
        this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), rotation);
    }

    public PdfRectangle(Rectangle rectangle) {
        this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), 0);
    }

    public Rectangle getRectangle() {
        return new Rectangle(this.left(), this.bottom(), this.right(), this.top());
    }

    @Override
    public boolean add(PdfObject object) {
        return false;
    }

    @Override
    public boolean add(float[] values) {
        return false;
    }

    @Override
    public boolean add(int[] values) {
        return false;
    }

    @Override
    public void addFirst(PdfObject object) {
    }

    public float left() {
        return this.llx;
    }

    public float right() {
        return this.urx;
    }

    public float top() {
        return this.ury;
    }

    public float bottom() {
        return this.lly;
    }

    public float left(int margin) {
        return this.llx + (float)margin;
    }

    public float right(int margin) {
        return this.urx - (float)margin;
    }

    public float top(int margin) {
        return this.ury - (float)margin;
    }

    public float bottom(int margin) {
        return this.lly + (float)margin;
    }

    public float width() {
        return this.urx - this.llx;
    }

    public float height() {
        return this.ury - this.lly;
    }

    public PdfRectangle rotate() {
        return new PdfRectangle(this.lly, this.llx, this.ury, this.urx, 0);
    }
}

