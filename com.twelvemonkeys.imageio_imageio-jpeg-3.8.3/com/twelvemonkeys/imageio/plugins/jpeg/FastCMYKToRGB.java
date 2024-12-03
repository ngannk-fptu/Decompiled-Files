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
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

class FastCMYKToRGB
implements RasterOp {
    @Override
    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        Validate.notNull((Object)raster, (String)"src may not be null");
        Validate.isTrue((raster != writableRaster ? 1 : 0) != 0, (String)"src and dest raster may not be same");
        Validate.isTrue((raster.getTransferType() == 0 ? 1 : 0) != 0, (Object)raster, (String)"only TYPE_BYTE rasters supported as src: %s");
        Validate.isTrue((raster.getNumDataElements() >= 4 ? 1 : 0) != 0, (Object)raster.getNumDataElements(), (String)"CMYK raster must have at least 4 data elements: %s");
        if (writableRaster == null) {
            writableRaster = this.createCompatibleDestRaster(raster);
        } else {
            Validate.isTrue((writableRaster.getTransferType() == 0 && writableRaster.getNumDataElements() >= 3 || writableRaster.getTransferType() == 3 && writableRaster.getNumDataElements() == 1 ? 1 : 0) != 0, (Object)raster, (String)"only 3 or 4 byte TYPE_BYTE or 1 int TYPE_INT rasters supported as dest: %s");
        }
        int n = raster.getHeight();
        int n2 = raster.getWidth();
        byte[] byArray = new byte[raster.getNumDataElements()];
        if (writableRaster.getTransferType() == 0) {
            byte[] byArray2 = new byte[writableRaster.getNumDataElements()];
            if (byArray2.length > 3) {
                byArray2[3] = -1;
            }
            for (int i = writableRaster.getMinY(); i < n; ++i) {
                for (int j = writableRaster.getMinX(); j < n2; ++j) {
                    raster.getDataElements(j, i, byArray);
                    this.convertCMYKToRGB(byArray, byArray2);
                    writableRaster.setDataElements(j, i, byArray2);
                }
            }
        } else if (writableRaster.getTransferType() == 3) {
            int[] nArray = new int[writableRaster.getNumDataElements()];
            byte[] byArray3 = new byte[3];
            SampleModel sampleModel = writableRaster.getSampleModel();
            int[] nArray2 = sampleModel instanceof SinglePixelPackedSampleModel ? ((SinglePixelPackedSampleModel)sampleModel).getBitOffsets() : new int[]{0, 8, 16};
            int n3 = nArray2.length > 3 ? 255 : 0;
            for (int i = writableRaster.getMinY(); i < n; ++i) {
                for (int j = writableRaster.getMinX(); j < n2; ++j) {
                    raster.getDataElements(j, i, byArray);
                    this.convertCMYKToRGB(byArray, byArray3);
                    nArray[0] = n3 << 24 | (byArray3[0] & 0xFF) << nArray2[0] | (byArray3[1] & 0xFF) << nArray2[1] | (byArray3[2] & 0xFF) << nArray2[2];
                    writableRaster.setDataElements(j, i, nArray);
                }
            }
        } else {
            throw new AssertionError();
        }
        return writableRaster;
    }

    private void convertCMYKToRGB(byte[] byArray, byte[] byArray2) {
        int n = byArray[3] & 0xFF;
        byArray2[0] = (byte)(255 - ((byArray[0] & 0xFF) * (255 - n) / 255 + n));
        byArray2[1] = (byte)(255 - ((byArray[1] & 0xFF) * (255 - n) / 255 + n));
        byArray2[2] = (byte)(255 - ((byArray[2] & 0xFF) * (255 - n) / 255 + n));
    }

    @Override
    public Rectangle2D getBounds2D(Raster raster) {
        return raster.getBounds();
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return raster.createCompatibleWritableRaster().createWritableChild(0, 0, raster.getWidth(), raster.getHeight(), 0, 0, new int[]{0, 1, 2});
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

