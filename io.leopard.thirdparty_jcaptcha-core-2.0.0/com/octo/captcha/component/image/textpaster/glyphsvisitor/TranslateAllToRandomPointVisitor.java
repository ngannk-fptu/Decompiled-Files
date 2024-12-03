/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import java.awt.geom.Rectangle2D;
import java.security.SecureRandom;
import java.util.Random;

public class TranslateAllToRandomPointVisitor
implements GlyphsVisitors {
    private Random myRandom = new SecureRandom();
    private double horizontalMargins = 0.0;
    private double verticalMargins = 0.0;

    public TranslateAllToRandomPointVisitor() {
    }

    public TranslateAllToRandomPointVisitor(double horizontalmargins, double verticalmargins) {
        this.horizontalMargins = horizontalmargins;
        this.verticalMargins = verticalmargins;
    }

    @Override
    public void visit(Glyphs glyphs, Rectangle2D backroundBounds) {
        double xRange = backroundBounds.getWidth() - glyphs.getBoundsWidth() - this.horizontalMargins;
        double yRange = backroundBounds.getHeight() - glyphs.getBoundsHeight() - this.verticalMargins;
        double tx = xRange * this.myRandom.nextDouble() - glyphs.getBoundsX() + this.horizontalMargins / 2.0;
        double ty = yRange * this.myRandom.nextDouble() - glyphs.getBoundsY() + this.verticalMargins / 2.0;
        glyphs.translate(tx, ty);
    }
}

