/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import java.awt.geom.Rectangle2D;
import java.security.SecureRandom;
import java.util.Random;

public class TranslateGlyphsVerticalRandomVisitor
implements GlyphsVisitors {
    private Random myRandom = new SecureRandom();
    private double verticalRange = 1.0;

    public TranslateGlyphsVerticalRandomVisitor(double verticalRange) {
        this.verticalRange = verticalRange;
    }

    @Override
    public void visit(Glyphs gv, Rectangle2D backroundBounds) {
        for (int i = 0; i < gv.size(); ++i) {
            double tx = 0.0;
            double ty = this.verticalRange * this.myRandom.nextGaussian();
            gv.translate(i, tx, ty);
        }
    }
}

