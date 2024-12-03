/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.lang.Validate;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

final class LuminanceToGray
implements RasterOp {
    LuminanceToGray() {
    }

    @Override
    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        int[] nArray;
        Validate.notNull((Object)raster, (String)"src may not be null");
        Validate.isTrue((raster != writableRaster ? 1 : 0) != 0, (String)"src and dest raster may not be same");
        Validate.isTrue((raster.getNumDataElements() >= 3 ? 1 : 0) != 0, (Object)raster.getNumDataElements(), (String)"luminance raster must have at least 3 data elements: %s");
        if (writableRaster == null) {
            writableRaster = this.createCompatibleDestRaster(raster);
        }
        if (raster.getNumBands() > 3 && writableRaster.getNumBands() > 1) {
            int[] nArray2 = new int[2];
            nArray2[0] = 0;
            nArray = nArray2;
            nArray2[1] = 3;
        } else {
            int[] nArray3 = new int[1];
            nArray = nArray3;
            nArray3[0] = 0;
        }
        int[] nArray4 = nArray;
        writableRaster.setRect(0, 0, raster.createChild(0, 0, raster.getWidth(), raster.getHeight(), 0, 0, nArray4));
        return writableRaster;
    }

    @Override
    public Rectangle2D getBounds2D(Raster raster) {
        return raster.getBounds();
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return raster.createCompatibleWritableRaster().createWritableChild(0, 0, raster.getWidth(), raster.getHeight(), 0, 0, new int[]{0});
    }

    @Override
    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            point2D2 = new Point2D.Double(point2D.getX(), point2D.getY());
        } else {
            point2D2.setLocation(point2D);
        }
        return point2D2;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }
}

