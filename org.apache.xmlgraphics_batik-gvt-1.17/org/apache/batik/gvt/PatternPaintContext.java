/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.GraphicsUtil
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.TileRable8Bit
 *  org.apache.batik.ext.awt.image.rendered.TileCacheRed
 */
package org.apache.batik.gvt;

import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.TileRable8Bit;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;

public class PatternPaintContext
implements PaintContext {
    private ColorModel rasterCM;
    private WritableRaster raster;
    private RenderedImage tiled;
    protected AffineTransform usr2dev;
    private static Rectangle EVERYTHING = new Rectangle(-536870912, -536870912, 0x3FFFFFFF, 0x3FFFFFFF);

    public AffineTransform getUsr2Dev() {
        return this.usr2dev;
    }

    public PatternPaintContext(ColorModel destCM, AffineTransform usr2dev, RenderingHints hints, Filter tile, Rectangle2D patternRegion, boolean overflow) {
        if (usr2dev == null) {
            throw new IllegalArgumentException();
        }
        if (hints == null) {
            hints = new RenderingHints(null);
        }
        if (tile == null) {
            throw new IllegalArgumentException();
        }
        this.usr2dev = usr2dev;
        TileRable8Bit tileRable = new TileRable8Bit(tile, (Rectangle2D)EVERYTHING, patternRegion, overflow);
        ColorSpace destCS = destCM.getColorSpace();
        if (destCS == ColorSpace.getInstance(1000)) {
            tileRable.setColorSpaceLinear(false);
        } else if (destCS == ColorSpace.getInstance(1004)) {
            tileRable.setColorSpaceLinear(true);
        }
        RenderContext rc = new RenderContext(usr2dev, EVERYTHING, hints);
        this.tiled = tileRable.createRendering(rc);
        if (this.tiled != null) {
            Rectangle devRgn = usr2dev.createTransformedShape(patternRegion).getBounds();
            if (((RectangularShape)devRgn).getWidth() > 128.0 || ((RectangularShape)devRgn).getHeight() > 128.0) {
                this.tiled = new TileCacheRed(GraphicsUtil.wrap((RenderedImage)this.tiled), 256, 64);
            }
        } else {
            this.rasterCM = ColorModel.getRGBdefault();
            WritableRaster wr = this.rasterCM.createCompatibleWritableRaster(32, 32);
            this.tiled = GraphicsUtil.wrap((RenderedImage)new BufferedImage(this.rasterCM, wr, false, null));
            return;
        }
        this.rasterCM = this.tiled.getColorModel();
        if (this.rasterCM.hasAlpha()) {
            this.rasterCM = destCM.hasAlpha() ? GraphicsUtil.coerceColorModel((ColorModel)this.rasterCM, (boolean)destCM.isAlphaPremultiplied()) : GraphicsUtil.coerceColorModel((ColorModel)this.rasterCM, (boolean)false);
        }
    }

    @Override
    public void dispose() {
        this.raster = null;
    }

    @Override
    public ColorModel getColorModel() {
        return this.rasterCM;
    }

    @Override
    public Raster getRaster(int x, int y, int width, int height) {
        if (this.raster == null || this.raster.getWidth() < width || this.raster.getHeight() < height) {
            this.raster = this.rasterCM.createCompatibleWritableRaster(width, height);
        }
        WritableRaster wr = this.raster.createWritableChild(0, 0, width, height, x, y, null);
        this.tiled.copyData(wr);
        GraphicsUtil.coerceData((WritableRaster)wr, (ColorModel)this.tiled.getColorModel(), (boolean)this.rasterCM.isAlphaPremultiplied());
        if (this.raster.getWidth() == width && this.raster.getHeight() == height) {
            return this.raster;
        }
        return wr.createTranslatedChild(0, 0);
    }
}

