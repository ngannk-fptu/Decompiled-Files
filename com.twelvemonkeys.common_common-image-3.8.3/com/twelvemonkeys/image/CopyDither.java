/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.ImageFilterException;
import com.twelvemonkeys.image.IndexImage;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

public class CopyDither
implements BufferedImageOp,
RasterOp {
    protected IndexColorModel indexColorModel = null;

    public CopyDither(IndexColorModel indexColorModel) {
        this.indexColorModel = indexColorModel;
    }

    public CopyDither() {
    }

    @Override
    public final BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        if (colorModel == null) {
            return new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 13, this.indexColorModel);
        }
        if (colorModel instanceof IndexColorModel) {
            return new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 13, (IndexColorModel)colorModel);
        }
        throw new ImageFilterException("Only IndexColorModel allowed.");
    }

    @Override
    public final WritableRaster createCompatibleDestRaster(Raster raster) {
        return this.createCompatibleDestRaster(raster, this.getICM(raster));
    }

    public final WritableRaster createCompatibleDestRaster(Raster raster, IndexColorModel indexColorModel) {
        return indexColorModel.createCompatibleWritableRaster(raster.getWidth(), raster.getHeight());
    }

    @Override
    public final Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return this.getBounds2D(bufferedImage.getRaster());
    }

    @Override
    public final Rectangle2D getBounds2D(Raster raster) {
        return raster.getBounds();
    }

    @Override
    public final Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            point2D2 = new Point2D.Float();
        }
        point2D2.setLocation(point2D.getX(), point2D.getY());
        return point2D2;
    }

    @Override
    public final RenderingHints getRenderingHints() {
        return null;
    }

    private static int toIntARGB(int[] nArray) {
        return 0xFF000000 | nArray[0] << 16 | nArray[1] << 8 | nArray[2];
    }

    @Override
    public final BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        if (bufferedImage2 == null) {
            bufferedImage2 = this.createCompatibleDestImage(bufferedImage, this.getICM(bufferedImage));
        } else if (!(bufferedImage2.getColorModel() instanceof IndexColorModel)) {
            throw new ImageFilterException("Only IndexColorModel allowed.");
        }
        this.filter(bufferedImage.getRaster(), bufferedImage2.getRaster(), (IndexColorModel)bufferedImage2.getColorModel());
        return bufferedImage2;
    }

    @Override
    public final WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        return this.filter(raster, writableRaster, this.getICM(raster));
    }

    private IndexColorModel getICM(BufferedImage bufferedImage) {
        return this.indexColorModel != null ? this.indexColorModel : IndexImage.getIndexColorModel((Image)bufferedImage, 256, 131584);
    }

    private IndexColorModel getICM(Raster raster) {
        return this.indexColorModel != null ? this.indexColorModel : this.createIndexColorModel(raster);
    }

    private IndexColorModel createIndexColorModel(Raster raster) {
        BufferedImage bufferedImage = new BufferedImage(raster.getWidth(), raster.getHeight(), 2);
        bufferedImage.setData(raster);
        return IndexImage.getIndexColorModel((Image)bufferedImage, 256, 131584);
    }

    public final WritableRaster filter(Raster raster, WritableRaster writableRaster, IndexColorModel indexColorModel) {
        int n = raster.getWidth();
        int n2 = raster.getHeight();
        if (writableRaster == null) {
            writableRaster = this.createCompatibleDestRaster(raster, indexColorModel);
        }
        int[] nArray = new int[4];
        Object object = null;
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n; ++j) {
                raster.getPixel(j, i, nArray);
                object = indexColorModel.getDataElements(CopyDither.toIntARGB(nArray), object);
                writableRaster.setDataElements(j, i, object);
            }
        }
        return writableRaster;
    }
}

