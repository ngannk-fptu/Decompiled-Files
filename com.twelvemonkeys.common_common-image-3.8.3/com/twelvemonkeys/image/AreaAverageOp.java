/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.BufferedImageIcon;
import com.twelvemonkeys.image.ImageUtil;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class AreaAverageOp
implements BufferedImageOp,
RasterOp {
    private final int width;
    private final int height;
    private Rectangle sourceRegion;

    public AreaAverageOp(int n, int n2) {
        this.width = n;
        this.height = n2;
    }

    public Rectangle getSourceRegion() {
        if (this.sourceRegion == null) {
            return null;
        }
        return new Rectangle(this.sourceRegion);
    }

    public void setSourceRegion(Rectangle rectangle) {
        if (rectangle == null) {
            this.sourceRegion = null;
        } else if (this.sourceRegion == null) {
            this.sourceRegion = new Rectangle(rectangle);
        } else {
            this.sourceRegion.setBounds(rectangle);
        }
    }

    @Override
    public BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        BufferedImage bufferedImage3 = bufferedImage2 != null ? bufferedImage2 : this.createCompatibleDestImage(bufferedImage, null);
        long l = System.currentTimeMillis();
        this.filterImpl(bufferedImage.getRaster(), bufferedImage3.getRaster());
        long l2 = System.currentTimeMillis() - l;
        System.out.println("time: " + l2);
        return bufferedImage3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void resample(BufferedImage bufferedImage, BufferedImage bufferedImage2, AffineTransform affineTransform) {
        Graphics2D graphics2D = bufferedImage2.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        try {
            graphics2D.drawImage(bufferedImage, affineTransform, null);
        }
        finally {
            graphics2D.dispose();
        }
    }

    @Override
    public WritableRaster filter(Raster raster, WritableRaster writableRaster) {
        WritableRaster writableRaster2 = writableRaster != null ? writableRaster : this.createCompatibleDestRaster(raster);
        return this.filterImpl(raster, writableRaster2);
    }

    private WritableRaster filterImpl(Raster raster, WritableRaster writableRaster) {
        boolean bl;
        int n;
        int n2;
        int n3;
        int n4;
        if (this.sourceRegion != null) {
            n4 = this.sourceRegion.x;
            n3 = this.sourceRegion.y;
            n2 = this.sourceRegion.width;
            n = this.sourceRegion.height;
            bl = raster == writableRaster;
            writableRaster = writableRaster.createWritableChild(n4, n3, n2, n, 0, 0, null);
            raster = bl ? writableRaster : raster.createChild(n4, n3, n2, n, 0, 0, null);
        }
        n4 = raster.getWidth();
        n3 = raster.getHeight();
        n2 = (n4 + this.width - 1) / this.width;
        n = (n3 + this.height - 1) / this.height;
        bl = n4 % this.width != 0;
        boolean bl2 = n3 % this.height != 0;
        int n5 = raster.getNumDataElements();
        int n6 = raster.getNumBands();
        int n7 = raster.getTransferType();
        Object object = null;
        int[] nArray = null;
        int[] nArray2 = null;
        if (raster.getTransferType() == 1) {
            if (raster.getSampleModel() instanceof SinglePixelPackedSampleModel) {
                SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)raster.getSampleModel();
                nArray = singlePixelPackedSampleModel.getBitMasks();
                nArray2 = singlePixelPackedSampleModel.getBitOffsets();
            } else {
                nArray = new int[]{65535};
                nArray2 = new int[]{0};
            }
        }
        for (int i = 0; i < this.height; ++i) {
            int n8 = !bl2 || i < this.height ? n : n3 - i * n;
            for (int j = 0; j < this.width; ++j) {
                int n9 = !bl || j < this.width ? n2 : n4 - j * n2;
                int n10 = n9 * n8;
                int n11 = n10 * n5;
                try {
                    object = raster.getDataElements(j * n2, i * n, n9, n8, object);
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    continue;
                }
                double d = 0.0;
                double d2 = 0.0;
                double d3 = 0.0;
                double d4 = 0.0;
                switch (n7) {
                    case 0: {
                        byte[] byArray = (byte[])object;
                        for (int k = 0; k < n11; k += n5) {
                            d += (double)(byArray[k] & 0xFF);
                            if (n6 <= 1) continue;
                            d2 += (double)(byArray[k + 1] & 0xFF);
                            d3 += (double)(byArray[k + 2] & 0xFF);
                            if (n6 <= 3) continue;
                            d4 += (double)(byArray[k + 3] & 0xFF);
                        }
                        d /= (double)n10;
                        if (n6 > 1) {
                            d2 /= (double)n10;
                            d3 /= (double)n10;
                            if (n6 > 3) {
                                d4 /= (double)n10;
                            }
                        }
                        byArray[0] = (byte)AreaAverageOp.clamp((int)d);
                        if (n6 <= 1) break;
                        byArray[1] = (byte)AreaAverageOp.clamp((int)d2);
                        byArray[2] = (byte)AreaAverageOp.clamp((int)d3);
                        if (n6 <= 3) break;
                        byArray[3] = (byte)AreaAverageOp.clamp((int)d4);
                        break;
                    }
                    case 3: {
                        int[] nArray3 = (int[])object;
                        for (int k = 0; k < n11; k += n5) {
                            d += (double)((nArray3[k] & 0xFF000000) >> 24);
                            d2 += (double)((nArray3[k] & 0xFF0000) >> 16);
                            d3 += (double)((nArray3[k] & 0xFF00) >> 8);
                            d4 += (double)(nArray3[k] & 0xFF);
                        }
                        nArray3[0] = AreaAverageOp.clamp((int)(d /= (double)n10)) << 24;
                        nArray3[0] = nArray3[0] | AreaAverageOp.clamp((int)(d2 /= (double)n10)) << 16;
                        nArray3[0] = nArray3[0] | AreaAverageOp.clamp((int)(d3 /= (double)n10)) << 8;
                        nArray3[0] = nArray3[0] | AreaAverageOp.clamp((int)(d4 /= (double)n10));
                        break;
                    }
                    case 1: {
                        if (nArray != null) {
                            short[] sArray = (short[])object;
                            for (int k = 0; k < n11; k += n5) {
                                d += (double)((sArray[k] & nArray[0]) >> nArray2[0]);
                                if (nArray.length <= 1) continue;
                                d2 += (double)((sArray[k] & nArray[1]) >> nArray2[1]);
                                d3 += (double)((sArray[k] & nArray[2]) >> nArray2[2]);
                                if (nArray.length <= 3) continue;
                                d4 += (double)((sArray[k] & nArray[3]) >> nArray2[3]);
                            }
                            d2 /= (double)n10;
                            d3 /= (double)n10;
                            d4 /= (double)n10;
                            sArray[0] = (short)((int)(d /= (double)n10) << nArray2[0] & nArray[0]);
                            if (nArray.length <= 1) break;
                            sArray[0] = (short)(sArray[0] | (short)((int)d2 << nArray2[1] & nArray[1]));
                            sArray[0] = (short)(sArray[0] | (short)((int)d3 << nArray2[2] & nArray[2]));
                            if (nArray.length <= 3) break;
                            sArray[0] = (short)(sArray[0] | (short)((int)d4 << nArray2[3] & nArray[3]));
                            break;
                        }
                    }
                    default: {
                        throw new IllegalArgumentException("TransferType not supported: " + n7);
                    }
                }
                writableRaster.setDataElements(j, i, 1, 1, object);
            }
        }
        return writableRaster;
    }

    private static int clamp(int n) {
        return n > 255 ? 255 : n;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        ColorModel colorModel2 = colorModel != null ? colorModel : bufferedImage.getColorModel();
        return new BufferedImage(colorModel2, ImageUtil.createCompatibleWritableRaster(bufferedImage, colorModel2, this.width, this.height), colorModel2.isAlphaPremultiplied(), null);
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return raster.createCompatibleWritableRaster(this.width, this.height);
    }

    @Override
    public Rectangle2D getBounds2D(Raster raster) {
        return new Rectangle(this.width, this.height);
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return new Rectangle(this.width, this.height);
    }

    @Override
    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            point2D2 = point2D instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
        }
        point2D2.setLocation(point2D);
        return point2D2;
    }

    public static void main(String[] stringArray) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("2006-Lamborghini-Gallardo-Spyder-Y-T-1600x1200.png"));
        for (int i = 0; i < 100; ++i) {
        }
        long l = System.currentTimeMillis();
        bufferedImage = new AreaAverageOp(500, 600).filter(bufferedImage, null);
        long l2 = System.currentTimeMillis() - l;
        System.out.println("time: " + l2 + " ms");
        JFrame jFrame = new JFrame("Test");
        jFrame.setDefaultCloseOperation(3);
        jFrame.setContentPane(new JScrollPane(new JLabel(new BufferedImageIcon(bufferedImage))));
        jFrame.pack();
        jFrame.setVisible(true);
    }
}

