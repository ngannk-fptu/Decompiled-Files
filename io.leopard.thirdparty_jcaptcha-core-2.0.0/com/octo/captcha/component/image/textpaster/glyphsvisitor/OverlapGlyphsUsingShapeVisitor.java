/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import com.octo.captcha.component.image.textpaster.Glyphs;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsVisitor;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class OverlapGlyphsUsingShapeVisitor
extends OverlapGlyphsVisitor {
    private double overlapPixels;

    public OverlapGlyphsUsingShapeVisitor(double overlapPixels) {
        super(0.0);
        this.overlapPixels = overlapPixels;
    }

    @Override
    public void visit(Glyphs gv, Rectangle2D backroundBounds) {
        for (int i = 1; i < gv.size(); ++i) {
            gv.translate(i, this.getSidingPosition(gv, i), 0.0);
            if (this.mayGlyphsOverlapAtIndex(gv, i)) {
                double realPossibleOverlap = this.getMaximumPossibleOverlap(gv, i);
                double currentOverlapWidth = this.intersectAndGetOverlapWidth(gv, i);
                double currentOverlapStatus = currentOverlapWidth - realPossibleOverlap;
                double bestReacheadOverlapStatus = Math.abs(currentOverlapStatus);
                boolean stillOk = true;
                while (Math.abs(currentOverlapStatus) >= this.overlapPixels / 10.0 && stillOk) {
                    double step = currentOverlapStatus / 2.0;
                    gv.translate(i, step, 0.0);
                    currentOverlapWidth = this.intersectAndGetOverlapWidth(gv, i);
                    currentOverlapStatus = currentOverlapWidth - realPossibleOverlap;
                    if (Math.abs(currentOverlapStatus) >= bestReacheadOverlapStatus && (currentOverlapWidth != 0.0 || gv.getMaxX(i - 1) - gv.getMinX(i) > gv.getBoundsWidth(i - 1))) {
                        if (currentOverlapWidth == 0.0) {
                            gv.translate(i, this.getSidingPosition(gv, i), 0.0);
                        } else {
                            gv.translate(i, -step, 0.0);
                        }
                        stillOk = false;
                    }
                    bestReacheadOverlapStatus = Math.min(Math.abs(currentOverlapStatus), bestReacheadOverlapStatus);
                }
                continue;
            }
            System.out.println("NOT POSSIBLE");
        }
    }

    private double getSidingPosition(Glyphs gv, int i) {
        return gv.getBoundsX(i - 1) + gv.getBoundsWidth(i - 1) - gv.getBoundsX(i) - Math.abs(gv.getRSB(i - 1)) - Math.abs(gv.getLSB(i));
    }

    private double intersectAndGetOverlapWidth(Glyphs gv, int i) {
        return this.getIntesection(gv, i).getBounds2D().getWidth();
    }

    private Area getIntesection(Glyphs gv, int index) {
        Area intersect = new Area(gv.getOutline(index - 1));
        intersect.intersect(new Area(gv.getOutline(index)));
        return intersect;
    }

    private double getMaximumPossibleOverlap(Glyphs gv, int index) {
        return Math.min(Math.min(this.overlapPixels, gv.getBoundsWidth(index)), gv.getBoundsWidth(index - 1));
    }

    private boolean mayGlyphsOverlapAtIndex(Glyphs gv, int index) {
        return gv.getMinY(index - 1) > gv.getMaxY(index) || gv.getMinY(index) > gv.getMaxY(index - 1);
    }
}

