/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.BufferedImageIcon;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.ResampleOp;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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

public class PixelizeOp
implements BufferedImageOp,
RasterOp {
    private final int pixelSizeX;
    private final int pixelSizeY;
    private Rectangle sourceRegion;

    public PixelizeOp(int n) {
        this(n, n);
    }

    public PixelizeOp(int n, int n2) {
        this.pixelSizeX = n;
        this.pixelSizeY = n2;
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
        this.filterImpl(bufferedImage.getRaster(), bufferedImage3.getRaster());
        return bufferedImage3;
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
        n2 = (n4 + this.pixelSizeX - 1) / this.pixelSizeX;
        n = (n3 + this.pixelSizeY - 1) / this.pixelSizeY;
        bl = n4 % n2 != 0;
        boolean bl2 = n3 % n != 0;
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
        for (int i = 0; i < n; ++i) {
            int n8 = !bl2 || i + 1 < n ? this.pixelSizeY : n3 - i * this.pixelSizeY;
            for (int j = 0; j < n2; ++j) {
                int n9 = !bl || j + 1 < n2 ? this.pixelSizeX : n4 - j * this.pixelSizeX;
                int n10 = n9 * n8;
                int n11 = n10 * n5;
                object = raster.getDataElements(j * this.pixelSizeX, i * this.pixelSizeY, n9, n8, object);
                double d = 0.0;
                double d2 = 0.0;
                double d3 = 0.0;
                double d4 = 0.0;
                switch (n7) {
                    case 0: {
                        int n12;
                        byte[] byArray = (byte[])object;
                        for (n12 = 0; n12 < n11; n12 += n5) {
                            d += (double)(byArray[n12] & 0xFF);
                            if (n6 <= 1) continue;
                            d2 += (double)(byArray[n12 + 1] & 0xFF);
                            d3 += (double)(byArray[n12 + 2] & 0xFF);
                            if (n6 <= 3) continue;
                            d4 += (double)(byArray[n12 + 3] & 0xFF);
                        }
                        d /= (double)n10;
                        if (n6 > 1) {
                            d2 /= (double)n10;
                            d3 /= (double)n10;
                            if (n6 > 3) {
                                d4 /= (double)n10;
                            }
                        }
                        for (n12 = 0; n12 < n11; n12 += n5) {
                            byArray[n12] = (byte)PixelizeOp.clamp((int)d);
                            if (n6 <= 1) continue;
                            byArray[n12 + 1] = (byte)PixelizeOp.clamp((int)d2);
                            byArray[n12 + 2] = (byte)PixelizeOp.clamp((int)d3);
                            if (n6 <= 3) continue;
                            byArray[n12 + 3] = (byte)PixelizeOp.clamp((int)d4);
                        }
                        break;
                    }
                    case 3: {
                        int n13;
                        int[] nArray3 = (int[])object;
                        for (n13 = 0; n13 < n11; n13 += n5) {
                            d += (double)((nArray3[n13] & 0xFF000000) >> 24);
                            d2 += (double)((nArray3[n13] & 0xFF0000) >> 16);
                            d3 += (double)((nArray3[n13] & 0xFF00) >> 8);
                            d4 += (double)(nArray3[n13] & 0xFF);
                        }
                        d /= (double)n10;
                        d2 /= (double)n10;
                        d3 /= (double)n10;
                        d4 /= (double)n10;
                        for (n13 = 0; n13 < n11; n13 += n5) {
                            nArray3[n13] = PixelizeOp.clamp((int)d) << 24;
                            int n14 = n13;
                            nArray3[n14] = nArray3[n14] | PixelizeOp.clamp((int)d2) << 16;
                            int n15 = n13;
                            nArray3[n15] = nArray3[n15] | PixelizeOp.clamp((int)d3) << 8;
                            int n16 = n13;
                            nArray3[n16] = nArray3[n16] | PixelizeOp.clamp((int)d4);
                        }
                        break;
                    }
                    case 1: {
                        if (nArray != null) {
                            int n17;
                            short[] sArray = (short[])object;
                            for (n17 = 0; n17 < n11; n17 += n5) {
                                d += (double)((sArray[n17] & nArray[0]) >> nArray2[0]);
                                if (nArray.length <= 1) continue;
                                d2 += (double)((sArray[n17] & nArray[1]) >> nArray2[1]);
                                d3 += (double)((sArray[n17] & nArray[2]) >> nArray2[2]);
                                if (nArray.length <= 3) continue;
                                d4 += (double)((sArray[n17] & nArray[3]) >> nArray2[3]);
                            }
                            d /= (double)n10;
                            d2 /= (double)n10;
                            d3 /= (double)n10;
                            d4 /= (double)n10;
                            for (n17 = 0; n17 < n11; n17 += n5) {
                                sArray[n17] = (short)((int)d << nArray2[0] & nArray[0]);
                                if (nArray.length <= 1) continue;
                                int n18 = n17;
                                sArray[n18] = (short)(sArray[n18] | (short)((int)d2 << nArray2[1] & nArray[1]));
                                int n19 = n17;
                                sArray[n19] = (short)(sArray[n19] | (short)((int)d3 << nArray2[2] & nArray[2]));
                                if (nArray.length <= 3) continue;
                                int n20 = n17;
                                sArray[n20] = (short)(sArray[n20] | (short)((int)d4 << nArray2[3] & nArray[3]));
                            }
                            break;
                        }
                    }
                    default: {
                        throw new IllegalArgumentException("TransferType not supported: " + n7);
                    }
                }
                writableRaster.setDataElements(j * this.pixelSizeX, i * this.pixelSizeY, n9, n8, object);
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
        return new BufferedImage(colorModel2, ImageUtil.createCompatibleWritableRaster(bufferedImage, colorModel2, bufferedImage.getWidth(), bufferedImage.getHeight()), colorModel2.isAlphaPremultiplied(), null);
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster raster) {
        return raster.createCompatibleWritableRaster();
    }

    @Override
    public Rectangle2D getBounds2D(Raster raster) {
        return new Rectangle(raster.getWidth(), raster.getHeight());
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight());
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
        for (int i = 0; i < 10; ++i) {
            new ResampleOp(bufferedImage.getWidth() / 10, bufferedImage.getHeight() / 10, 9).filter(bufferedImage, null);
        }
        long l = System.currentTimeMillis();
        bufferedImage = new ResampleOp(bufferedImage.getWidth() / 4, bufferedImage.getHeight() / 4, 9).filter(bufferedImage, null);
        long l2 = System.currentTimeMillis() - l;
        System.out.println("time: " + l2 + " ms");
        JFrame jFrame = new JFrame("Test");
        jFrame.setDefaultCloseOperation(3);
        jFrame.setContentPane(new JScrollPane(new JLabel(new BufferedImageIcon(bufferedImage))));
        jFrame.pack();
        jFrame.setVisible(true);
    }
}

