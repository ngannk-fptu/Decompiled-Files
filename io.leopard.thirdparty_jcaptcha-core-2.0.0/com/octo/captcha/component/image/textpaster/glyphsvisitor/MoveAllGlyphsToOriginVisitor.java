/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import java.awt.geom.Rectangle2D;

public class MoveAllGlyphsToOriginVisitor
implements GlyphsVisitors {
    @Override
    public void visit(Glyphs glyphs, Rectangle2D backroundBounds) {
        for (int i = 0; i < glyphs.size(); ++i) {
            double tx = -glyphs.getX(i);
            double ty = -glyphs.getY(i);
            glyphs.translate(i, tx, ty);
        }
    }
}

