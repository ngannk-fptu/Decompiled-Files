/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import java.awt.geom.Rectangle2D;

public class OverlapGlyphsVisitor
implements GlyphsVisitors {
    private double overlapPixels = 0.0;

    public OverlapGlyphsVisitor(double overlapPixels) {
        this.overlapPixels = overlapPixels;
    }

    @Override
    public void visit(Glyphs gv, Rectangle2D backroundBounds) {
        for (int i = 1; i < gv.size(); ++i) {
            double tx = gv.getBoundsX(i - 1) + gv.getBoundsWidth(i - 1) - gv.getBoundsX(i) - Math.abs(gv.getRSB(i - 1)) - Math.abs(gv.getLSB(i)) - this.overlapPixels;
            double ty = 0.0;
            gv.translate(i, tx, ty);
        }
    }
}

