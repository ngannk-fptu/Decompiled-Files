/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class FloodRed
extends AbstractRed {
    private WritableRaster raster;

    public FloodRed(Rectangle bounds) {
        this(bounds, new Color(0, 0, 0, 0));
    }

    public FloodRed(Rectangle bounds, Paint paint) {
        int th;
        ColorModel cm = GraphicsUtil.sRGB_Unpre;
        int defSz = AbstractTiledRed.getDefaultTileSize();
        int tw = bounds.width;
        if (tw > defSz) {
            tw = defSz;
        }
        if ((th = bounds.height) > defSz) {
            th = defSz;
        }
        SampleModel sm = cm.createCompatibleSampleModel(tw, th);
        this.init((CachableRed)null, bounds, cm, sm, 0, 0, null);
        this.raster = Raster.createWritableRaster(sm, new Point(0, 0));
        BufferedImage offScreen = new BufferedImage(cm, this.raster, cm.isAlphaPremultiplied(), null);
        Graphics2D g = GraphicsUtil.createGraphics(offScreen);
        g.setPaint(paint);
        g.fillRect(0, 0, bounds.width, bounds.height);
        g.dispose();
    }

    @Override
    public Raster getTile(int x, int y) {
        int tx = this.tileGridXOff + x * this.tileWidth;
        int ty = this.tileGridYOff + y * this.tileHeight;
        return this.raster.createTranslatedChild(tx, ty);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        int tx0 = this.getXTile(wr.getMinX());
        int ty0 = this.getYTile(wr.getMinY());
        int tx1 = this.getXTile(wr.getMinX() + wr.getWidth() - 1);
        int ty1 = this.getYTile(wr.getMinY() + wr.getHeight() - 1);
        boolean is_INT_PACK = GraphicsUtil.is_INT_PACK_Data(this.getSampleModel(), false);
        for (int y = ty0; y <= ty1; ++y) {
            for (int x = tx0; x <= tx1; ++x) {
                Raster r = this.getTile(x, y);
                if (is_INT_PACK) {
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                    continue;
                }
                GraphicsUtil.copyData_FALLBACK(r, wr);
            }
        }
        return wr;
    }
}

