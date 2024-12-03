/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.ImageUtil;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

public class ConvolveWithEdgeOp
implements BufferedImageOp,
RasterOp {
    public static final int EDGE_ZERO_FILL = 0;
    public static final int EDGE_NO_OP = 1;
    public static final int EDGE_REFLECT = 2;
    public static final int EDGE_WRAP = 3;
    private final Kernel kernel;
    private final int edgeCondition;
    private final ConvolveOp convolve;

    public ConvolveWithEdgeOp(Kernel kernel, int n, RenderingHints renderingHints) {
        int n2;
        switch (n) {
            case 2: 
            case 3: {
                n2 = 1;
                break;
            }
            default: {
                n2 = n;
            }
        }
        this.kernel = kernel;
        this.edgeCondition = n;
        this.convolve = new ConvolveOp(kernel, n2, renderingHints);
    }

    public ConvolveWithEdgeOp(Kernel kernel) {
        this(kernel, 0, null);
    }

    @Override
    public BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        if (bufferedImage == null) {
            throw new NullPointerException("source image is null");
        }
        if (bufferedImage == bufferedImage2) {
            throw new IllegalArgumentException("source image cannot be the same as the destination image");
        }
        int n = this.kernel.getWidth() / 2;
        int n2 = this.kernel.getHeight() / 2;
        BufferedImage bufferedImage3 = this.addBorder(bufferedImage, n, n2);
        BufferedImage bufferedImage4 = bufferedImage2;
        if (bufferedImage3.getType() == 5) {
            bufferedImage4 = ImageUtil.createBuffered(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType(), bufferedImage.getColorModel().getTransparency(), null);
        }
        bufferedImage4 = this.convolve.filter(bufferedImage3, bufferedImage4);
        if (bufferedImage != bufferedImage3) {
            bufferedImage4 = bufferedImage4.getSubimage(n, n2, bufferedImage.getWidth(), bufferedImage.getHeight());
        }
        return bufferedImage4;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private BufferedImage addBorder(BufferedImage bufferedImage, int n, int n2) {
        if ((this.edgeCondition & 2) == 0) {
            return bufferedImage;
        }
        int n3 = bufferedImage.getWidth();
        int n4 = bufferedImage.getHeight();
        ColorModel colorModel = bufferedImage.getColorModel();
        WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(n3 + 2 * n, n4 + 2 * n2);
        BufferedImage bufferedImage2 = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
        Graphics2D graphics2D = bufferedImage2.createGraphics();
        try {
            graphics2D.setComposite(AlphaComposite.Src);
            graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            graphics2D.drawImage((Image)bufferedImage, n, n2, null);
            switch (this.edgeCondition) {
                case 2: {
                    graphics2D.drawImage(bufferedImage, n, 0, n + n3, n2, 0, 0, n3, 1, null);
                    graphics2D.drawImage(bufferedImage, -n3 + n, n2, n, n4 + n2, 0, 0, 1, n4, null);
                    graphics2D.drawImage(bufferedImage, n3 + n, n2, 2 * n + n3, n4 + n2, n3 - 1, 0, n3, n4, null);
                    graphics2D.drawImage(bufferedImage, n, n2 + n4, n + n3, 2 * n2 + n4, 0, n4 - 1, n3, n4, null);
                    return bufferedImage2;
                }
                case 3: {
                    graphics2D.drawImage((Image)bufferedImage, -n3 + n, -n4 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n, -n4 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n3 + n, -n4 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, -n3 + n, n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n3 + n, n2, null);
                    graphics2D.drawImage((Image)bufferedImage, -n3 + n, n4 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n, n4 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n3 + n, n4 + n2, null);
                    return bufferedImage2;
                }
                default: {
                    throw new IllegalArgumentException("Illegal edge operation " + this.edgeCondition);
                }
            }
        }
        finally {
            graphics2D.dispose();
        }
    }

    public int getEdgeCondition() {
        return this.edgeCondition;
    }

    @Override
    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        return this.convolve.filter(raster, writableRaster);
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        return this.convolve.createCompatibleDestImage(bufferedImage, colorModel);
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return this.convolve.createCompatibleDestRaster(raster);
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return this.convolve.getBounds2D(bufferedImage);
    }

    @Override
    public Rectangle2D getBounds2D(Raster raster) {
        return this.convolve.getBounds2D(raster);
    }

    @Override
    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        return this.convolve.getPoint2D(point2D, point2D2);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return this.convolve.getRenderingHints();
    }

    public Kernel getKernel() {
        return this.convolve.getKernel();
    }
}

