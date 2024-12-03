/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.media.jai.util.MathJAI;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterFactory;
import javax.media.jai.UntiledOpImage;
import javax.media.jai.operator.DFTDescriptor;

final class MlibDFTOpImage
extends UntiledOpImage {
    private int DFTMode;

    private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, EnumeratedParameter dataNature) {
        int dataType;
        int newWidth;
        int newHeight;
        boolean isComplexSource = !dataNature.equals(DFTDescriptor.REAL_TO_COMPLEX);
        boolean isComplexDest = !dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL);
        SampleModel srcSampleModel = source.getSampleModel();
        int numSourceBands = srcSampleModel.getNumBands();
        if (isComplexSource && numSourceBands != 2 || !isComplexSource && numSourceBands != 1) {
            throw new RuntimeException(JaiI18N.getString("MlibDFTOpImage0"));
        }
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        il.setMinX(source.getMinX());
        il.setMinY(source.getMinY());
        int currentWidth = il.getWidth(source);
        int currentHeight = il.getHeight(source);
        if (currentWidth == 1 && currentHeight == 1) {
            newHeight = 1;
            newWidth = 1;
        } else if (currentWidth == 1 && currentHeight > 1) {
            newWidth = 1;
            newHeight = MathJAI.nextPositivePowerOf2(currentHeight);
        } else if (currentWidth > 1 && currentHeight == 1) {
            newWidth = MathJAI.nextPositivePowerOf2(currentWidth);
            newHeight = 1;
        } else {
            newWidth = MathJAI.nextPositivePowerOf2(currentWidth);
            newHeight = MathJAI.nextPositivePowerOf2(currentHeight);
        }
        il.setWidth(newWidth);
        il.setHeight(newHeight);
        boolean createNewSampleModel = false;
        int requiredNumBands = numSourceBands;
        if (isComplexSource && !isComplexDest) {
            requiredNumBands /= 2;
        } else if (!isComplexSource && isComplexDest) {
            requiredNumBands *= 2;
        }
        SampleModel sm = il.getSampleModel(source);
        int numBands = sm.getNumBands();
        if (numBands != requiredNumBands) {
            numBands = requiredNumBands;
            createNewSampleModel = true;
        }
        if ((dataType = sm.getTransferType()) != 4 && dataType != 5) {
            dataType = 4;
            createNewSampleModel = true;
        }
        if (createNewSampleModel) {
            int[] bandOffsets = new int[numBands];
            for (int b = 0; b < numBands; ++b) {
                bandOffsets[b] = b;
            }
            int lineStride = newWidth * numBands;
            sm = RasterFactory.createPixelInterleavedSampleModel(dataType, newWidth, newHeight, numBands, lineStride, bandOffsets);
            il.setSampleModel(sm);
            ColorModel cm = il.getColorModel(null);
            if (cm != null && !JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
                il.unsetValid(512);
            }
        }
        return il;
    }

    public MlibDFTOpImage(RenderedImage source, Map config, ImageLayout layout, EnumeratedParameter dataNature, boolean isForward, EnumeratedParameter scaleType) {
        super(source, config, MlibDFTOpImage.layoutHelper(layout, source, dataNature));
        if (scaleType.equals(DFTDescriptor.SCALING_NONE)) {
            this.DFTMode = isForward ? 0 : 3;
        } else if (scaleType.equals(DFTDescriptor.SCALING_UNITARY)) {
            this.DFTMode = isForward ? 2 : 5;
        } else if (scaleType.equals(DFTDescriptor.SCALING_DIMENSIONS)) {
            this.DFTMode = isForward ? 1 : 4;
        } else {
            throw new RuntimeException(JaiI18N.getString("MlibDFTOpImage1"));
        }
    }

    public static boolean isAcceptableSampleModel(SampleModel sm) {
        if (!(sm instanceof ComponentSampleModel)) {
            return true;
        }
        ComponentSampleModel csm = (ComponentSampleModel)sm;
        int[] bandOffsets = csm.getBandOffsets();
        return bandOffsets.length == 2 && bandOffsets[1] == bandOffsets[0] + 1;
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }

    protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        Raster source = sources[0];
        int formatTag = MediaLibAccessor.findCompatibleTag(new Raster[]{source}, dest);
        MediaLibAccessor srcAccessor = new MediaLibAccessor(source, this.mapDestRect(destRect, 0), formatTag);
        MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
        mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
        mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
        for (int i = 0; i < dstML.length; ++i) {
            Image.FourierTransform((mediaLibImage)dstML[i], (mediaLibImage)srcML[i], (int)this.DFTMode);
        }
        if (dstAccessor.isDataCopy()) {
            dstAccessor.clampDataArrays();
            dstAccessor.copyDataToRaster();
        }
    }
}

