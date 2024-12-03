/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 *  com.sun.medialib.mlib.mediaLibImageInterpTable
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibUtils;
import com.sun.media.jai.util.ImageUtil;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import com.sun.medialib.mlib.mediaLibImageInterpTable;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationTable;
import javax.media.jai.WarpOpImage;
import javax.media.jai.WarpPolynomial;

final class MlibWarpPolynomialTableOpImage
extends WarpOpImage {
    private double[] xCoeffs;
    private double[] yCoeffs;
    private mediaLibImageInterpTable mlibInterpTableI = null;
    private mediaLibImageInterpTable mlibInterpTableF = null;
    private mediaLibImageInterpTable mlibInterpTableD = null;
    private double preScaleX;
    private double preScaleY;
    private double postScaleX;
    private double postScaleY;

    public MlibWarpPolynomialTableOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, WarpPolynomial warp, Interpolation interp, double[] backgroundValues) {
        super(source, layout, config, true, extender, interp, warp, backgroundValues);
        float[] xc = warp.getXCoeffs();
        float[] yc = warp.getYCoeffs();
        int size = xc.length;
        this.xCoeffs = new double[size];
        this.yCoeffs = new double[size];
        for (int i = 0; i < size; ++i) {
            this.xCoeffs[i] = xc[i];
            this.yCoeffs[i] = yc[i];
        }
        this.preScaleX = warp.getPreScaleX();
        this.preScaleY = warp.getPreScaleY();
        this.postScaleX = warp.getPostScaleX();
        this.postScaleY = warp.getPostScaleY();
    }

    protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex) {
        Rectangle wrect = super.backwardMapRect(destRect, sourceIndex);
        wrect.setBounds(wrect.x - 1, wrect.y - 1, wrect.width + 2, wrect.height + 2);
        return wrect;
    }

    public Raster computeTile(int tileX, int tileY) {
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle rect = new Rectangle(org.x, org.y, this.tileWidth, this.tileHeight);
        Rectangle destRect = rect.intersection(this.computableBounds);
        Rectangle destRect1 = rect.intersection(this.getBounds());
        if (destRect.isEmpty()) {
            if (this.setBackground) {
                ImageUtil.fillBackground(dest, destRect1, this.backgroundValues);
            }
            return dest;
        }
        Rectangle srcRect = this.backwardMapRect(destRect, 0).intersection(this.getSourceImage(0).getBounds());
        if (srcRect.isEmpty()) {
            if (this.setBackground) {
                ImageUtil.fillBackground(dest, destRect1, this.backgroundValues);
            }
            return dest;
        }
        if (!destRect1.equals(destRect)) {
            ImageUtil.fillBordersWithBackgroundValues(destRect1, destRect, dest, this.backgroundValues);
        }
        int l = this.interp == null ? 0 : this.interp.getLeftPadding();
        int r = this.interp == null ? 0 : this.interp.getRightPadding();
        int t = this.interp == null ? 0 : this.interp.getTopPadding();
        int b = this.interp == null ? 0 : this.interp.getBottomPadding();
        srcRect = new Rectangle(srcRect.x - l, srcRect.y - t, srcRect.width + l + r, srcRect.height + t + b);
        Raster[] sources = new Raster[]{this.getBorderExtender() != null ? this.getSourceImage(0).getExtendedData(srcRect, this.extender) : this.getSourceImage(0).getData(srcRect)};
        this.computeRect(sources, dest, destRect);
        if (this.getSourceImage(0).overlapsMultipleTiles(srcRect)) {
            this.recycleTile(sources[0]);
        }
        return dest;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcMA = new MediaLibAccessor(source, source.getBounds(), formatTag);
        MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
        mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
        switch (dstMA.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                if (this.mlibInterpTableI == null) {
                    InterpolationTable jtable = (InterpolationTable)this.interp;
                    this.mlibInterpTableI = new mediaLibImageInterpTable(3, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), (Object)jtable.getHorizontalTableData(), (Object)jtable.getVerticalTableData());
                }
                if (this.setBackground) {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.PolynomialWarpTable2((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (double[])this.xCoeffs, (double[])this.yCoeffs, (double)destRect.x, (double)destRect.y, (double)source.getMinX(), (double)source.getMinY(), (double)this.preScaleX, (double)this.preScaleY, (double)this.postScaleX, (double)this.postScaleY, (mediaLibImageInterpTable)this.mlibInterpTableI, (int)0, (int[])this.intBackgroundValues);
                    }
                } else {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.PolynomialWarpTable((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (double[])this.xCoeffs, (double[])this.yCoeffs, (double)destRect.x, (double)destRect.y, (double)source.getMinX(), (double)source.getMinY(), (double)this.preScaleX, (double)this.preScaleY, (double)this.postScaleX, (double)this.postScaleY, (mediaLibImageInterpTable)this.mlibInterpTableI, (int)0);
                        MlibUtils.clampImage(dstMLI[i], this.getColorModel());
                    }
                }
                break;
            }
            case 4: {
                if (this.mlibInterpTableF == null) {
                    InterpolationTable jtable = (InterpolationTable)this.interp;
                    this.mlibInterpTableF = new mediaLibImageInterpTable(4, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), (Object)jtable.getHorizontalTableDataFloat(), (Object)jtable.getVerticalTableDataFloat());
                }
                if (this.setBackground) {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.PolynomialWarpTable2_Fp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (double[])this.xCoeffs, (double[])this.yCoeffs, (double)destRect.x, (double)destRect.y, (double)source.getMinX(), (double)source.getMinY(), (double)this.preScaleX, (double)this.preScaleY, (double)this.postScaleX, (double)this.postScaleY, (mediaLibImageInterpTable)this.mlibInterpTableD, (int)0, (double[])this.backgroundValues);
                    }
                } else {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.PolynomialWarpTable_Fp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (double[])this.xCoeffs, (double[])this.yCoeffs, (double)destRect.x, (double)destRect.y, (double)source.getMinX(), (double)source.getMinY(), (double)this.preScaleX, (double)this.preScaleY, (double)this.postScaleX, (double)this.postScaleY, (mediaLibImageInterpTable)this.mlibInterpTableD, (int)0);
                    }
                }
                break;
            }
            case 5: {
                if (this.mlibInterpTableD == null) {
                    InterpolationTable jtable = (InterpolationTable)this.interp;
                    this.mlibInterpTableD = new mediaLibImageInterpTable(5, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), (Object)jtable.getHorizontalTableDataDouble(), (Object)jtable.getVerticalTableDataDouble());
                }
                if (this.setBackground) {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.PolynomialWarpTable2_Fp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (double[])this.xCoeffs, (double[])this.yCoeffs, (double)destRect.x, (double)destRect.y, (double)source.getMinX(), (double)source.getMinY(), (double)this.preScaleX, (double)this.preScaleY, (double)this.postScaleX, (double)this.postScaleY, (mediaLibImageInterpTable)this.mlibInterpTableD, (int)0, (double[])this.backgroundValues);
                    }
                } else {
                    for (int i = 0; i < dstMLI.length; ++i) {
                        Image.PolynomialWarpTable_Fp((mediaLibImage)dstMLI[i], (mediaLibImage)srcMLI[i], (double[])this.xCoeffs, (double[])this.yCoeffs, (double)destRect.x, (double)destRect.y, (double)source.getMinX(), (double)source.getMinY(), (double)this.preScaleX, (double)this.preScaleY, (double)this.postScaleX, (double)this.postScaleY, (mediaLibImageInterpTable)this.mlibInterpTableD, (int)0);
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("Generic2"));
            }
        }
        if (dstMA.isDataCopy()) {
            dstMA.clampDataArrays();
            dstMA.copyDataToRaster();
        }
    }
}

