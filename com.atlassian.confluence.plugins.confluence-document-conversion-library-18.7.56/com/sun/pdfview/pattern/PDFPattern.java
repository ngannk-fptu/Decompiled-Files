/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.pattern;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.pattern.PatternType1;
import com.sun.pdfview.pattern.PatternType2;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Map;

public abstract class PDFPattern {
    private int type;
    private AffineTransform xform;

    protected PDFPattern(int type) {
        this.type = type;
    }

    public static PDFPattern getPattern(PDFObject patternObj, Map resources) throws IOException {
        PDFPattern pattern = (PDFPattern)patternObj.getCache();
        if (pattern != null) {
            return pattern;
        }
        int type = patternObj.getDictRef("PatternType").getIntValue();
        PDFObject matrix = patternObj.getDictRef("Matrix");
        AffineTransform xform = null;
        if (matrix == null) {
            xform = new AffineTransform();
        } else {
            float[] elts = new float[6];
            for (int i = 0; i < elts.length; ++i) {
                elts[i] = matrix.getAt(i).getFloatValue();
            }
            xform = new AffineTransform(elts);
        }
        switch (type) {
            case 1: {
                pattern = new PatternType1();
                break;
            }
            case 2: {
                pattern = new PatternType2();
                break;
            }
            default: {
                throw new PDFParseException("Unknown pattern type " + type);
            }
        }
        pattern.setTransform(xform);
        pattern.parse(patternObj, resources);
        patternObj.setCache(pattern);
        return pattern;
    }

    public int getPatternType() {
        return this.type;
    }

    public AffineTransform getTransform() {
        return this.xform;
    }

    protected void setTransform(AffineTransform xform) {
        this.xform = xform;
    }

    protected abstract void parse(PDFObject var1, Map var2) throws IOException;

    public abstract PDFPaint getPaint(PDFPaint var1);
}

