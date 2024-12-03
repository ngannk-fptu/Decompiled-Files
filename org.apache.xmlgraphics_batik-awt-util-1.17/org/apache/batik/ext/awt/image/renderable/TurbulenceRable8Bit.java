/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.renderable.AbstractColorInterpolationRable;
import org.apache.batik.ext.awt.image.renderable.TurbulenceRable;
import org.apache.batik.ext.awt.image.rendered.TurbulencePatternRed;

public class TurbulenceRable8Bit
extends AbstractColorInterpolationRable
implements TurbulenceRable {
    int seed = 0;
    int numOctaves = 1;
    double baseFreqX = 0.0;
    double baseFreqY = 0.0;
    boolean stitched = false;
    boolean fractalNoise = false;
    Rectangle2D region;

    public TurbulenceRable8Bit(Rectangle2D region) {
        this.region = region;
    }

    public TurbulenceRable8Bit(Rectangle2D region, int seed, int numOctaves, double baseFreqX, double baseFreqY, boolean stitched, boolean fractalNoise) {
        this.seed = seed;
        this.numOctaves = numOctaves;
        this.baseFreqX = baseFreqX;
        this.baseFreqY = baseFreqY;
        this.stitched = stitched;
        this.fractalNoise = fractalNoise;
        this.region = region;
    }

    @Override
    public Rectangle2D getTurbulenceRegion() {
        return (Rectangle2D)this.region.clone();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.region.clone();
    }

    @Override
    public int getSeed() {
        return this.seed;
    }

    @Override
    public int getNumOctaves() {
        return this.numOctaves;
    }

    @Override
    public double getBaseFrequencyX() {
        return this.baseFreqX;
    }

    @Override
    public double getBaseFrequencyY() {
        return this.baseFreqY;
    }

    @Override
    public boolean isStitched() {
        return this.stitched;
    }

    @Override
    public boolean isFractalNoise() {
        return this.fractalNoise;
    }

    @Override
    public void setTurbulenceRegion(Rectangle2D turbulenceRegion) {
        this.touch();
        this.region = turbulenceRegion;
    }

    @Override
    public void setSeed(int seed) {
        this.touch();
        this.seed = seed;
    }

    @Override
    public void setNumOctaves(int numOctaves) {
        this.touch();
        this.numOctaves = numOctaves;
    }

    @Override
    public void setBaseFrequencyX(double baseFreqX) {
        this.touch();
        this.baseFreqX = baseFreqX;
    }

    @Override
    public void setBaseFrequencyY(double baseFreqY) {
        this.touch();
        this.baseFreqY = baseFreqY;
    }

    @Override
    public void setStitched(boolean stitched) {
        this.touch();
        this.stitched = stitched;
    }

    @Override
    public void setFractalNoise(boolean fractalNoise) {
        this.touch();
        this.fractalNoise = fractalNoise;
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Rectangle2D aoiRect;
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoiRect = this.getBounds2D();
        } else {
            Rectangle2D rect = this.getBounds2D();
            aoiRect = aoi.getBounds2D();
            if (!aoiRect.intersects(rect)) {
                return null;
            }
            Rectangle2D.intersect(aoiRect, rect, aoiRect);
        }
        AffineTransform usr2dev = rc.getTransform();
        Rectangle devRect = usr2dev.createTransformedShape(aoiRect).getBounds();
        if (devRect.width <= 0 || devRect.height <= 0) {
            return null;
        }
        ColorSpace cs = this.getOperationColorSpace();
        Rectangle2D tile = null;
        if (this.stitched) {
            tile = (Rectangle2D)this.region.clone();
        }
        AffineTransform patternTxf = new AffineTransform();
        try {
            patternTxf = usr2dev.createInverse();
        }
        catch (NoninvertibleTransformException noninvertibleTransformException) {
            // empty catch block
        }
        return new TurbulencePatternRed(this.baseFreqX, this.baseFreqY, this.numOctaves, this.seed, this.fractalNoise, tile, patternTxf, devRect, cs, true);
    }
}

