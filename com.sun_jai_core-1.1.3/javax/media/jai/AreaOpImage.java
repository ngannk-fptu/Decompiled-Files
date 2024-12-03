/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.IntegerSequence;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

public abstract class AreaOpImage
extends OpImage {
    protected int leftPadding;
    protected int rightPadding;
    protected int topPadding;
    protected int bottomPadding;
    protected BorderExtender extender = null;
    private Rectangle theDest;

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source) {
        if (layout != null && source != null && (layout.getValidMask() & 0xF) != 0) {
            Rectangle sourceRect = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
            Rectangle dstRect = new Rectangle(layout.getMinX(source), layout.getMinY(source), layout.getWidth(source), layout.getHeight(source));
            if (dstRect.intersection(sourceRect).isEmpty()) {
                throw new IllegalArgumentException(JaiI18N.getString("AreaOpImage0"));
            }
        }
        return layout;
    }

    private static Map configHelper(Map configuration) {
        Map config;
        if (configuration == null) {
            config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
        } else {
            config = configuration;
            if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
                RenderingHints hints = new RenderingHints(null);
                hints.putAll((Map<?, ?>)configuration);
                config = hints;
                config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
            }
        }
        return config;
    }

    public AreaOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, int leftPadding, int rightPadding, int topPadding, int bottomPadding) {
        super(AreaOpImage.vectorize(source), AreaOpImage.layoutHelper(layout, source), AreaOpImage.configHelper(configuration), cobbleSources);
        this.extender = extender;
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
        this.topPadding = topPadding;
        this.bottomPadding = bottomPadding;
        if (extender == null) {
            int d_x0 = this.getMinX() + leftPadding;
            int d_y0 = this.getMinY() + topPadding;
            int d_w = this.getWidth() - leftPadding - rightPadding;
            d_w = Math.max(d_w, 0);
            int d_h = this.getHeight() - topPadding - bottomPadding;
            d_h = Math.max(d_h, 0);
            this.theDest = new Rectangle(d_x0, d_y0, d_w, d_h);
        } else {
            this.theDest = this.getBounds();
        }
    }

    public int getLeftPadding() {
        return this.leftPadding;
    }

    public int getRightPadding() {
        return this.rightPadding;
    }

    public int getTopPadding() {
        return this.topPadding;
    }

    public int getBottomPadding() {
        return this.bottomPadding;
    }

    public BorderExtender getBorderExtender() {
        return this.extender;
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int lpad = this.getLeftPadding();
        int rpad = this.getRightPadding();
        int tpad = this.getTopPadding();
        int bpad = this.getBottomPadding();
        return new Rectangle(sourceRect.x + lpad, sourceRect.y + tpad, sourceRect.width - lpad - rpad, sourceRect.height - tpad - bpad);
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int lpad = this.getLeftPadding();
        int rpad = this.getRightPadding();
        int tpad = this.getTopPadding();
        int bpad = this.getBottomPadding();
        return new Rectangle(destRect.x - lpad, destRect.y - tpad, destRect.width + lpad + rpad, destRect.height + tpad + bpad);
    }

    public Raster computeTile(int tileX, int tileY) {
        if (!this.cobbleSources) {
            return super.computeTile(tileX, tileY);
        }
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle rect = new Rectangle(org.x, org.y, this.sampleModel.getWidth(), this.sampleModel.getHeight());
        Rectangle destRect = rect.intersection(this.theDest);
        if (destRect.width <= 0 || destRect.height <= 0) {
            return dest;
        }
        PlanarImage s = this.getSource(0);
        destRect = destRect.intersection(s.getBounds());
        Rectangle srcRect = new Rectangle(destRect);
        srcRect.x -= this.getLeftPadding();
        srcRect.width += this.getLeftPadding() + this.getRightPadding();
        srcRect.y -= this.getTopPadding();
        srcRect.height += this.getTopPadding() + this.getBottomPadding();
        IntegerSequence srcXSplits = new IntegerSequence();
        IntegerSequence srcYSplits = new IntegerSequence();
        s.getSplits(srcXSplits, srcYSplits, srcRect);
        IntegerSequence xSplits = new IntegerSequence(destRect.x, destRect.x + destRect.width);
        xSplits.insert(destRect.x);
        xSplits.insert(destRect.x + destRect.width);
        srcXSplits.startEnumeration();
        while (srcXSplits.hasMoreElements()) {
            int xsplit = srcXSplits.nextElement();
            int lsplit = xsplit - this.getLeftPadding();
            int rsplit = xsplit + this.getRightPadding();
            xSplits.insert(lsplit);
            xSplits.insert(rsplit);
        }
        IntegerSequence ySplits = new IntegerSequence(destRect.y, destRect.y + destRect.height);
        ySplits.insert(destRect.y);
        ySplits.insert(destRect.y + destRect.height);
        srcYSplits.startEnumeration();
        while (srcYSplits.hasMoreElements()) {
            int ysplit = srcYSplits.nextElement();
            int tsplit = ysplit - this.getBottomPadding();
            int bsplit = ysplit + this.getTopPadding();
            ySplits.insert(tsplit);
            ySplits.insert(bsplit);
        }
        Raster[] sources = new Raster[1];
        ySplits.startEnumeration();
        int y1 = ySplits.nextElement();
        while (ySplits.hasMoreElements()) {
            int y2 = ySplits.nextElement();
            int h = y2 - y1;
            int py1 = y1 - this.getTopPadding();
            int py2 = y2 + this.getBottomPadding();
            int ph = py2 - py1;
            xSplits.startEnumeration();
            int x1 = xSplits.nextElement();
            while (xSplits.hasMoreElements()) {
                int x2 = xSplits.nextElement();
                int w = x2 - x1;
                int px1 = x1 - this.getLeftPadding();
                int px2 = x2 + this.getRightPadding();
                int pw = px2 - px1;
                Rectangle srcSubRect = new Rectangle(px1, py1, pw, ph);
                sources[0] = this.extender != null ? s.getExtendedData(srcSubRect, this.extender) : s.getData(srcSubRect);
                Rectangle dstSubRect = new Rectangle(x1, y1, w, h);
                this.computeRect(sources, dest, dstSubRect);
                if (s.overlapsMultipleTiles(srcSubRect)) {
                    this.recycleTile(sources[0]);
                }
                x1 = x2;
            }
            y1 = y2;
        }
        return dest;
    }
}

