/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

public abstract class GeometricOpImage
extends OpImage {
    protected Interpolation interp;
    protected BorderExtender extender = null;
    protected Rectangle computableBounds;
    protected boolean setBackground;
    protected double[] backgroundValues;
    protected int[] intBackgroundValues;

    public GeometricOpImage(Vector sources, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp) {
        this(sources, layout, configuration, cobbleSources, extender, interp, null);
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

    public GeometricOpImage(Vector sources, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp, double[] backgroundValues) {
        super(sources, layout, GeometricOpImage.configHelper(configuration), cobbleSources);
        this.extender = extender;
        Interpolation interpolation = this.interp = interp != null ? interp : new InterpolationNearest();
        if (backgroundValues == null) {
            backgroundValues = new double[]{0.0};
        }
        this.setBackground = false;
        for (int i = 0; i < backgroundValues.length; ++i) {
            if (backgroundValues[i] == 0.0) continue;
            this.setBackground = true;
        }
        this.backgroundValues = backgroundValues;
        int numBands = this.getSampleModel().getNumBands();
        if (backgroundValues.length < numBands) {
            this.backgroundValues = new double[numBands];
            for (int i = 0; i < numBands; ++i) {
                this.backgroundValues[i] = backgroundValues[0];
            }
        }
        if (this.sampleModel.getDataType() <= 3) {
            int length = this.backgroundValues.length;
            this.intBackgroundValues = new int[length];
            for (int i = 0; i < length; ++i) {
                this.intBackgroundValues[i] = (int)this.backgroundValues[i];
            }
        }
        this.computableBounds = this.getBounds();
    }

    public Interpolation getInterpolation() {
        return this.interp;
    }

    public BorderExtender getBorderExtender() {
        return this.extender;
    }

    public Point2D mapDestPoint(Point2D destPt, int sourceIndex) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        Rectangle destRect = new Rectangle((int)destPt.getX(), (int)destPt.getY(), 1, 1);
        Rectangle sourceRect = this.backwardMapRect(destRect, sourceIndex);
        if (sourceRect == null) {
            return null;
        }
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation((double)sourceRect.x + ((double)sourceRect.width - 1.0) / 2.0, (double)sourceRect.y + ((double)sourceRect.height - 1.0) / 2.0);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        Rectangle sourceRect = new Rectangle((int)sourcePt.getX(), (int)sourcePt.getY(), 1, 1);
        Rectangle destRect = this.forwardMapRect(sourceRect, sourceIndex);
        if (destRect == null) {
            return null;
        }
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation((double)destRect.x + ((double)destRect.width - 1.0) / 2.0, (double)destRect.y + ((double)destRect.height - 1.0) / 2.0);
        return pt;
    }

    protected abstract Rectangle forwardMapRect(Rectangle var1, int var2);

    protected abstract Rectangle backwardMapRect(Rectangle var1, int var2);

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        int lpad = this.interp.getLeftPadding();
        int tpad = this.interp.getTopPadding();
        Rectangle srcRect = (Rectangle)sourceRect.clone();
        srcRect.x += lpad;
        srcRect.y += tpad;
        srcRect.width -= lpad + this.interp.getRightPadding();
        srcRect.height -= tpad + this.interp.getBottomPadding();
        Rectangle destRect = this.forwardMapRect(srcRect, sourceIndex);
        return destRect == null ? this.getBounds() : destRect;
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        Rectangle sourceRect = this.backwardMapRect(destRect, sourceIndex);
        if (sourceRect == null) {
            return this.getSource(sourceIndex).getBounds();
        }
        int lpad = this.interp.getLeftPadding();
        int tpad = this.interp.getTopPadding();
        return new Rectangle(sourceRect.x - lpad, sourceRect.y - tpad, sourceRect.width + lpad + this.interp.getRightPadding(), sourceRect.height + tpad + this.interp.getBottomPadding());
    }

    public Raster computeTile(int tileX, int tileY) {
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle destRect = this.getTileRect(tileX, tileY).intersection(this.getBounds());
        if (destRect.isEmpty()) {
            if (this.setBackground) {
                ImageUtil.fillBackground(dest, destRect, this.backgroundValues);
            }
            return dest;
        }
        int numSources = this.getNumSources();
        if (this.cobbleSources) {
            int i;
            Raster[] rasterSources = new Raster[numSources];
            for (i = 0; i < numSources; ++i) {
                PlanarImage source = this.getSource(i);
                Rectangle srcBounds = source.getBounds();
                Rectangle srcRect = this.mapDestRect(destRect, i);
                if (srcRect == null) {
                    srcRect = srcBounds;
                } else {
                    if (this.extender == null && !srcBounds.contains(srcRect)) {
                        srcRect = srcBounds.intersection(srcRect);
                    }
                    if (!srcRect.intersects(srcBounds)) {
                        if (this.setBackground) {
                            ImageUtil.fillBackground(dest, destRect, this.backgroundValues);
                        }
                        return dest;
                    }
                }
                rasterSources[i] = this.extender != null ? source.getExtendedData(srcRect, this.extender) : source.getData(srcRect);
            }
            this.computeRect(rasterSources, dest, destRect);
            for (i = 0; i < numSources; ++i) {
                PlanarImage source;
                Raster sourceData = rasterSources[i];
                if (sourceData == null || !(source = this.getSourceImage(i)).overlapsMultipleTiles(sourceData.getBounds())) continue;
                this.recycleTile(sourceData);
            }
        } else {
            PlanarImage[] imageSources = new PlanarImage[numSources];
            for (int i = 0; i < numSources; ++i) {
                imageSources[i] = this.getSource(i);
            }
            this.computeRect(imageSources, dest, destRect);
        }
        return dest;
    }
}

