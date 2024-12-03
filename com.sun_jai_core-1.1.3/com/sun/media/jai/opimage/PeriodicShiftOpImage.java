/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.opimage.TranslateIntOpImage;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.OpImage;

final class PeriodicShiftOpImage
extends OpImage {
    private int[] xTrans;
    private int[] yTrans;
    private TranslateIntOpImage[] images;
    private Rectangle[] bounds;

    public PeriodicShiftOpImage(RenderedImage source, Map config, ImageLayout layout, int shiftX, int shiftY) {
        super(PeriodicShiftOpImage.vectorize(source), layout == null ? new ImageLayout() : (ImageLayout)layout.clone(), config, false);
        this.xTrans = new int[]{-shiftX, -shiftX, this.width - shiftX, this.width - shiftX};
        this.yTrans = new int[]{-shiftY, this.height - shiftY, -shiftY, this.height - shiftY};
        this.images = new TranslateIntOpImage[4];
        for (int i = 0; i < 4; ++i) {
            this.images[i] = new TranslateIntOpImage(source, null, this.xTrans[i], this.yTrans[i]);
        }
        Rectangle destBounds = this.getBounds();
        this.bounds = new Rectangle[4];
        for (int i = 0; i < 4; ++i) {
            this.bounds[i] = destBounds.intersection(this.images[i].getBounds());
        }
    }

    public Raster computeTile(int tileX, int tileY) {
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle rect = new Rectangle(org.x, org.y, this.sampleModel.getWidth(), this.sampleModel.getHeight());
        Rectangle destRect = rect.intersection(this.getBounds());
        for (int i = 0; i < 4; ++i) {
            Rectangle overlap = destRect.intersection(this.bounds[i]);
            if (overlap.isEmpty()) continue;
            JDKWorkarounds.setRect(dest, this.images[i].getData(overlap));
        }
        return dest;
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("PeriodicShiftOpImage0"));
        }
        Rectangle destRect = null;
        for (int i = 0; i < 4; ++i) {
            Rectangle srcRect = sourceRect;
            srcRect.translate(this.xTrans[i], this.yTrans[i]);
            Rectangle overlap = srcRect.intersection(this.getBounds());
            if (overlap.isEmpty()) continue;
            destRect = destRect == null ? overlap : destRect.union(overlap);
        }
        return destRect;
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("PeriodicShiftOpImage0"));
        }
        Rectangle sourceRect = null;
        for (int i = 0; i < 4; ++i) {
            Rectangle overlap = destRect.intersection(this.bounds[i]);
            if (overlap.isEmpty()) continue;
            overlap.translate(-this.xTrans[i], -this.yTrans[i]);
            sourceRect = sourceRect == null ? overlap : sourceRect.union(overlap);
        }
        return sourceRect;
    }
}

