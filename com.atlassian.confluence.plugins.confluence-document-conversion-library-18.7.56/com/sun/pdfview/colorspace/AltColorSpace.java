/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.function.PDFFunction;
import java.awt.color.ColorSpace;

public class AltColorSpace
extends ColorSpace {
    private PDFFunction fkt;
    private ColorSpace origCs;

    public AltColorSpace(PDFFunction fkt, ColorSpace origCs) {
        super(origCs.getType(), fkt.getNumInputs());
        this.fkt = fkt;
        this.origCs = origCs;
    }

    @Override
    public float[] fromCIEXYZ(float[] p_colorvalue) {
        p_colorvalue = this.fkt.calculate(p_colorvalue);
        return this.origCs.fromCIEXYZ(p_colorvalue);
    }

    @Override
    public float[] fromRGB(float[] p_rgbvalue) {
        p_rgbvalue = this.fkt.calculate(p_rgbvalue);
        return this.origCs.fromCIEXYZ(p_rgbvalue);
    }

    @Override
    public float[] toCIEXYZ(float[] p_colorvalue) {
        float[] colorvalue = this.fkt.calculate(p_colorvalue);
        return this.origCs.toCIEXYZ(colorvalue);
    }

    @Override
    public float[] toRGB(float[] p_colorvalue) {
        float[] colorvalue = this.fkt.calculate(p_colorvalue);
        return this.origCs.toRGB(colorvalue);
    }
}

