/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.colorspace.PDFColorSpace;
import com.sun.pdfview.pattern.PDFPattern;
import java.io.IOException;
import java.util.Map;

public class PatternSpace
extends PDFColorSpace {
    private PDFColorSpace base;

    public PatternSpace() {
        super(null);
    }

    public PatternSpace(PDFColorSpace base) {
        super(null);
        this.base = base;
    }

    public PDFColorSpace getBase() {
        return this.base;
    }

    @Override
    public int getNumComponents() {
        if (this.base == null) {
            return 0;
        }
        return this.base.getNumComponents();
    }

    @Override
    public PDFPaint getPaint(float[] components) {
        throw new IllegalArgumentException("Pattern spaces require a pattern name!");
    }

    public PDFPaint getPaint(PDFObject patternObj, float[] components, Map resources) throws IOException {
        PDFPattern pattern;
        PDFPaint basePaint = null;
        if (this.getBase() != null) {
            basePaint = this.getBase().getPaint(components);
        }
        if ((pattern = (PDFPattern)patternObj.getCache()) == null) {
            pattern = PDFPattern.getPattern(patternObj, resources);
            patternObj.setCache(pattern);
        }
        return pattern.getPaint(basePaint);
    }
}

