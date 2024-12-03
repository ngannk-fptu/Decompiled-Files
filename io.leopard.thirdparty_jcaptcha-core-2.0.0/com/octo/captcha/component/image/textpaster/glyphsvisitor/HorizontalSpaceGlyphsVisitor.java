/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import java.awt.geom.Rectangle2D;

public class HorizontalSpaceGlyphsVisitor
implements GlyphsVisitors {
    private int spaceBetweenGlyphs = 0;

    public HorizontalSpaceGlyphsVisitor(int spaceBetweenGlyphs) {
        this.spaceBetweenGlyphs = spaceBetweenGlyphs;
    }

    @Override
    public void visit(Glyphs glyphs, Rectangle2D backroundBounds) {
        for (int i = 1; i < glyphs.size(); ++i) {
            double tx = glyphs.getBoundsX(i - 1) + glyphs.getBoundsWidth(i - 1) - glyphs.getBoundsX(i) + (double)this.spaceBetweenGlyphs;
            double ty = 0.0;
            glyphs.translate(i, tx, ty);
        }
    }
}

