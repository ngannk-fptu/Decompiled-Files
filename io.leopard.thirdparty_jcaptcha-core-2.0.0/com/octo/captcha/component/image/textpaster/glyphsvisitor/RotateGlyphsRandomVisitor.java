/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import java.awt.geom.Rectangle2D;
import java.security.SecureRandom;
import java.util.Random;

public class RotateGlyphsRandomVisitor
implements GlyphsVisitors {
    private double maxAngle = 0.39269908169872414;
    private Random myRandom = new SecureRandom();

    public RotateGlyphsRandomVisitor() {
    }

    public RotateGlyphsRandomVisitor(double maxAngle) {
        this.maxAngle = maxAngle;
    }

    @Override
    public void visit(Glyphs gv, Rectangle2D backroundBounds) {
        for (int i = 0; i < gv.size(); ++i) {
            gv.rotate(i, this.maxAngle * this.myRandom.nextGaussian());
        }
    }
}

