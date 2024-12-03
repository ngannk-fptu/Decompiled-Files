/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.pattern;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFRenderer;
import com.sun.pdfview.pattern.PDFPattern;
import com.sun.pdfview.pattern.PDFShader;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;

public class PatternType2
extends PDFPattern {
    public PDFShader shader;

    public PatternType2() {
        super(2);
    }

    @Override
    protected void parse(PDFObject patternObj, Map rsrc) throws IOException {
        this.shader = PDFShader.getShader(patternObj.getDictRef("Shading"), rsrc);
    }

    @Override
    public PDFPaint getPaint(PDFPaint basePaint) {
        return new TilingPatternPaint(this.shader.getPaint().getPaint(), this);
    }

    static class TilingPatternPaint
    extends PDFPaint {
        private PatternType2 pattern;

        public TilingPatternPaint(Paint paint, PatternType2 pattern) {
            super(paint);
            this.pattern = pattern;
        }

        @Override
        public Rectangle2D fill(PDFRenderer state, Graphics2D g, GeneralPath s) {
            AffineTransform at = g.getTransform();
            Shape xformed = s.createTransformedShape(at);
            state.push();
            state.setTransform(state.getInitialTransform());
            state.transform(this.pattern.getTransform());
            try {
                at = state.getTransform().createInverse();
            }
            catch (NoninvertibleTransformException noninvertibleTransformException) {
                // empty catch block
            }
            xformed = at.createTransformedShape(xformed);
            if (this.pattern.shader.getBackground() != null) {
                g.setComposite(AlphaComposite.getInstance(3));
                g.setPaint(this.pattern.shader.getBackground().getPaint());
                g.fill(xformed);
            }
            g.setComposite(AlphaComposite.getInstance(3));
            g.setPaint(this.getPaint());
            g.fill(xformed);
            state.pop();
            return s.createTransformedShape(g.getTransform()).getBounds2D();
        }
    }
}

