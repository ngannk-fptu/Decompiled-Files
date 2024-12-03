/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

public class AffineTransformOp
implements BufferedImageOp,
RasterOp {
    final java.awt.image.AffineTransformOp delegate;
    public static final int TYPE_NEAREST_NEIGHBOR = 1;
    public static final int TYPE_BILINEAR = 2;
    public static final int TYPE_BICUBIC = 3;

    public AffineTransformOp(AffineTransform affineTransform, RenderingHints renderingHints) {
        this.delegate = new java.awt.image.AffineTransformOp(affineTransform, renderingHints);
    }

    public AffineTransformOp(AffineTransform affineTransform, int n) {
        this.delegate = new java.awt.image.AffineTransformOp(affineTransform, n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        try {
            return this.delegate.filter(bufferedImage, bufferedImage2);
        }
        catch (ImagingOpException imagingOpException) {
            if (bufferedImage2 == null) {
                bufferedImage2 = this.createCompatibleDestImage(bufferedImage, bufferedImage.getColorModel());
            }
            Graphics2D graphics2D = bufferedImage2.createGraphics();
            try {
                Object object;
                int n = this.delegate.getInterpolationType();
                if (n > 0) {
                    object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                    switch (n) {
                        case 2: {
                            object = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                            break;
                        }
                        case 3: {
                            object = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                        }
                    }
                    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
                } else if (this.getRenderingHints() != null) {
                    graphics2D.setRenderingHints(this.getRenderingHints());
                }
                graphics2D.drawImage(bufferedImage, this.delegate.getTransform(), null);
                object = bufferedImage2;
                return object;
            }
            finally {
                graphics2D.dispose();
            }
        }
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return this.delegate.getBounds2D(bufferedImage);
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        return this.delegate.createCompatibleDestImage(bufferedImage, colorModel);
    }

    @Override
    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        return this.delegate.filter(raster, writableRaster);
    }

    @Override
    public Rectangle2D getBounds2D(Raster raster) {
        return this.delegate.getBounds2D(raster);
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return this.delegate.createCompatibleDestRaster(raster);
    }

    @Override
    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        return this.delegate.getPoint2D(point2D, point2D2);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return this.delegate.getRenderingHints();
    }
}

