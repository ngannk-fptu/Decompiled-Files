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
import com.sun.media.jai.mlib.MlibAffineOpImage;
import com.sun.media.jai.mlib.MlibUtils;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import com.sun.medialib.mlib.mediaLibImageInterpTable;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationTable;

public class MlibAffineTableOpImage
extends MlibAffineOpImage {
    public MlibAffineTableOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform tr, Interpolation interp, double[] backgroundValues) {
        super(source, layout, config, extender, tr, interp, backgroundValues);
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        InterpolationTable jtable = (InterpolationTable)this.interp;
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        double[] medialib_tr = (double[])this.medialib_tr.clone();
        medialib_tr[2] = this.m_transform[0] * (double)srcRect.x + this.m_transform[1] * (double)srcRect.y + this.m_transform[2] - (double)destRect.x;
        medialib_tr[5] = this.m_transform[3] * (double)srcRect.x + this.m_transform[4] * (double)srcRect.y + this.m_transform[5] - (double)destRect.y;
        switch (dstAccessor.getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(3, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), (Object)jtable.getHorizontalTableData(), (Object)jtable.getVerticalTableData());
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                if (this.setBackground) {
                    Image.AffineTable2((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (double[])medialib_tr, (mediaLibImageInterpTable)mlibInterpTable, (int)0, (int[])this.intBackgroundValues);
                } else {
                    Image.AffineTable((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (double[])medialib_tr, (mediaLibImageInterpTable)mlibInterpTable, (int)0);
                }
                MlibUtils.clampImage(dstML[0], this.getColorModel());
                break;
            }
            case 4: {
                mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(4, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), (Object)jtable.getHorizontalTableDataFloat(), (Object)jtable.getVerticalTableDataFloat());
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                if (this.setBackground) {
                    Image.AffineTable2_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (double[])medialib_tr, (mediaLibImageInterpTable)mlibInterpTable, (int)0, (double[])this.backgroundValues);
                    break;
                }
                Image.AffineTable_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (double[])medialib_tr, (mediaLibImageInterpTable)mlibInterpTable, (int)0);
                break;
            }
            case 5: {
                mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(5, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), (Object)jtable.getHorizontalTableDataDouble(), (Object)jtable.getVerticalTableDataDouble());
                mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
                mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
                if (this.setBackground) {
                    Image.AffineTable2_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (double[])medialib_tr, (mediaLibImageInterpTable)mlibInterpTable, (int)0, (double[])this.backgroundValues);
                    break;
                }
                Image.AffineTable_Fp((mediaLibImage)dstML[0], (mediaLibImage)srcML[0], (double[])medialib_tr, (mediaLibImageInterpTable)mlibInterpTable, (int)0);
                break;
            }
            default: {
                String className = this.getClass().getName();
                throw new RuntimeException(JaiI18N.getString("Generic2"));
            }
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.copyDataToRaster();
        }
    }
}

