/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.colorspace.AltColorSpace;
import com.sun.pdfview.colorspace.PDFColorSpace;
import com.sun.pdfview.function.PDFFunction;
import java.awt.color.ColorSpace;

public class AlternateColorSpace
extends PDFColorSpace {
    private PDFColorSpace alternate;
    private PDFFunction function;
    private AltColorSpace altcolorspace;

    public AlternateColorSpace(PDFColorSpace alternate, PDFFunction function) {
        super(null);
        this.alternate = alternate;
        this.function = function;
    }

    @Override
    public int getNumComponents() {
        if (this.function != null) {
            return this.function.getNumInputs();
        }
        return this.alternate.getNumComponents();
    }

    @Override
    public PDFPaint getPaint(float[] components) {
        if (this.function != null) {
            components = this.function.calculate(components);
        }
        return this.alternate.getPaint(components);
    }

    @Override
    public ColorSpace getColorSpace() {
        if (this.altcolorspace == null) {
            this.altcolorspace = new AltColorSpace(this.function, this.alternate.getColorSpace());
        }
        return this.altcolorspace;
    }
}

