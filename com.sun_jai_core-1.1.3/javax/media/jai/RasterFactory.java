/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.media.jai.ComponentSampleModelJAI;
import javax.media.jai.FloatDoubleColorModel;
import javax.media.jai.JaiI18N;

public class RasterFactory {
    public static WritableRaster createInterleavedRaster(int dataType, int width, int height, int numBands, Point location) {
        if (numBands < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory0"));
        }
        int[] bandOffsets = new int[numBands];
        for (int i = 0; i < numBands; ++i) {
            bandOffsets[i] = numBands - 1 - i;
        }
        return RasterFactory.createInterleavedRaster(dataType, width, height, width * numBands, numBands, bandOffsets, location);
    }

    /*
     * WARNING - void declaration
     */
    public static WritableRaster createInterleavedRaster(int dataType, int width, int height, int scanlineStride, int pixelStride, int[] bandOffsets, Point location) {
        void var7_12;
        if (bandOffsets == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory4"));
        }
        int bands = bandOffsets.length;
        int maxBandOff = bandOffsets[0];
        for (int i = 1; i < bands; ++i) {
            if (bandOffsets[i] <= maxBandOff) continue;
            maxBandOff = bandOffsets[i];
        }
        long lsize = (long)maxBandOff + (long)scanlineStride * (long)(height - 1) + (long)pixelStride * (long)(width - 1) + 1L;
        if (lsize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory16"));
        }
        int size = (int)lsize;
        switch (dataType) {
            case 0: {
                DataBuffer d = new DataBufferByte(size);
                break;
            }
            case 1: {
                DataBuffer d = new DataBufferUShort(size);
                break;
            }
            case 2: {
                DataBuffer d = new DataBufferShort(size);
                break;
            }
            case 3: {
                DataBuffer d = new DataBufferInt(size);
                break;
            }
            case 4: {
                DataBuffer d = DataBufferUtils.createDataBufferFloat(size);
                break;
            }
            case 5: {
                DataBuffer d = DataBufferUtils.createDataBufferDouble(size);
                break;
            }
            default: {
                throw new IllegalArgumentException(JaiI18N.getString("RasterFactory3"));
            }
        }
        return RasterFactory.createInterleavedRaster((DataBuffer)var7_12, width, height, scanlineStride, pixelStride, bandOffsets, location);
    }

    public static WritableRaster createBandedRaster(int dataType, int width, int height, int bands, Point location) {
        if (bands < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory0"));
        }
        int[] bankIndices = new int[bands];
        int[] bandOffsets = new int[bands];
        for (int i = 0; i < bands; ++i) {
            bankIndices[i] = i;
            bandOffsets[i] = 0;
        }
        return RasterFactory.createBandedRaster(dataType, width, height, width, bankIndices, bandOffsets, location);
    }

    /*
     * WARNING - void declaration
     */
    public static WritableRaster createBandedRaster(int dataType, int width, int height, int scanlineStride, int[] bankIndices, int[] bandOffsets, Point location) {
        void var7_13;
        int bands = bandOffsets.length;
        if (bankIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory1"));
        }
        if (bandOffsets == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory4"));
        }
        if (bandOffsets.length != bankIndices.length) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory2"));
        }
        int maxBank = bankIndices[0];
        int maxBandOff = bandOffsets[0];
        for (int i = 1; i < bands; ++i) {
            if (bankIndices[i] > maxBank) {
                maxBank = bankIndices[i];
            }
            if (bandOffsets[i] <= maxBandOff) continue;
            maxBandOff = bandOffsets[i];
        }
        int banks = maxBank + 1;
        long lsize = (long)maxBandOff + (long)scanlineStride * (long)(height - 1) + (long)(width - 1) + 1L;
        if (lsize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory16"));
        }
        int size = (int)lsize;
        switch (dataType) {
            case 0: {
                DataBuffer d = new DataBufferByte(size, banks);
                break;
            }
            case 1: {
                DataBuffer d = new DataBufferUShort(size, banks);
                break;
            }
            case 2: {
                DataBuffer d = new DataBufferShort(size, banks);
                break;
            }
            case 3: {
                DataBuffer d = new DataBufferInt(size, banks);
                break;
            }
            case 4: {
                DataBuffer d = DataBufferUtils.createDataBufferFloat(size, banks);
                break;
            }
            case 5: {
                DataBuffer d = DataBufferUtils.createDataBufferDouble(size, banks);
                break;
            }
            default: {
                throw new IllegalArgumentException(JaiI18N.getString("RasterFactory3"));
            }
        }
        return RasterFactory.createBandedRaster((DataBuffer)var7_13, width, height, scanlineStride, bankIndices, bandOffsets, location);
    }

    public static WritableRaster createPackedRaster(int dataType, int width, int height, int[] bandMasks, Point location) {
        return Raster.createPackedRaster(dataType, width, height, bandMasks, location);
    }

    public static WritableRaster createPackedRaster(int dataType, int width, int height, int numBands, int bitsPerBand, Point location) {
        if (bitsPerBand <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory15"));
        }
        return Raster.createPackedRaster(dataType, width, height, numBands, bitsPerBand, location);
    }

    public static WritableRaster createInterleavedRaster(DataBuffer dataBuffer, int width, int height, int scanlineStride, int pixelStride, int[] bandOffsets, Point location) {
        if (bandOffsets == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory4"));
        }
        if (location == null) {
            location = new Point(0, 0);
        }
        int dataType = dataBuffer.getDataType();
        switch (dataType) {
            case 0: 
            case 1: {
                PixelInterleavedSampleModel csm = new PixelInterleavedSampleModel(dataType, width, height, pixelStride, scanlineStride, bandOffsets);
                return Raster.createWritableRaster(csm, dataBuffer, location);
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                int minBandOff = bandOffsets[0];
                int maxBandOff = bandOffsets[0];
                for (int i = 1; i < bandOffsets.length; ++i) {
                    minBandOff = Math.min(minBandOff, bandOffsets[i]);
                    maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
                }
                if ((maxBandOff -= minBandOff) > scanlineStride) {
                    throw new IllegalArgumentException(JaiI18N.getString("RasterFactory5"));
                }
                if (pixelStride * width > scanlineStride) {
                    throw new IllegalArgumentException(JaiI18N.getString("RasterFactory6"));
                }
                if (pixelStride < maxBandOff) {
                    throw new IllegalArgumentException(JaiI18N.getString("RasterFactory7"));
                }
                ComponentSampleModelJAI sm = new ComponentSampleModelJAI(dataType, width, height, pixelStride, scanlineStride, bandOffsets);
                return Raster.createWritableRaster(sm, dataBuffer, location);
            }
        }
        throw new IllegalArgumentException(JaiI18N.getString("RasterFactory3"));
    }

    public static WritableRaster createBandedRaster(DataBuffer dataBuffer, int width, int height, int scanlineStride, int[] bankIndices, int[] bandOffsets, Point location) {
        if (location == null) {
            location = new Point(0, 0);
        }
        int dataType = dataBuffer.getDataType();
        if (bankIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory1"));
        }
        if (bandOffsets == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory4"));
        }
        int bands = bankIndices.length;
        if (bandOffsets.length != bands) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory2"));
        }
        ComponentSampleModelJAI bsm = new ComponentSampleModelJAI(dataType, width, height, 1, scanlineStride, bankIndices, bandOffsets);
        switch (dataType) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                return Raster.createWritableRaster(bsm, dataBuffer, location);
            }
        }
        throw new IllegalArgumentException(JaiI18N.getString("RasterFactory3"));
    }

    public static WritableRaster createPackedRaster(DataBuffer dataBuffer, int width, int height, int scanlineStride, int[] bandMasks, Point location) {
        return Raster.createPackedRaster(dataBuffer, width, height, scanlineStride, bandMasks, location);
    }

    public static WritableRaster createPackedRaster(DataBuffer dataBuffer, int width, int height, int bitsPerPixel, Point location) {
        return Raster.createPackedRaster(dataBuffer, width, height, bitsPerPixel, location);
    }

    public static Raster createRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point location) {
        return Raster.createRaster(sampleModel, dataBuffer, location);
    }

    public static WritableRaster createWritableRaster(SampleModel sampleModel, Point location) {
        if (location == null) {
            location = new Point(0, 0);
        }
        return RasterFactory.createWritableRaster(sampleModel, sampleModel.createDataBuffer(), location);
    }

    public static WritableRaster createWritableRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point location) {
        return Raster.createWritableRaster(sampleModel, dataBuffer, location);
    }

    public static WritableRaster createWritableChild(WritableRaster raster, int parentX, int parentY, int width, int height, int childMinX, int childMinY, int[] bandList) {
        return raster.createWritableChild(parentX, parentY, width, height, childMinX, childMinY, bandList);
    }

    public static SampleModel createBandedSampleModel(int dataType, int width, int height, int numBands, int[] bankIndices, int[] bandOffsets) {
        int i;
        if (numBands < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory0"));
        }
        if (bankIndices == null) {
            bankIndices = new int[numBands];
            for (i = 0; i < numBands; ++i) {
                bankIndices[i] = i;
            }
        }
        if (bandOffsets == null) {
            bandOffsets = new int[numBands];
            for (i = 0; i < numBands; ++i) {
                bandOffsets[i] = 0;
            }
        }
        if (bandOffsets.length != bankIndices.length) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory2"));
        }
        return new ComponentSampleModelJAI(dataType, width, height, 1, width, bankIndices, bandOffsets);
    }

    public static SampleModel createBandedSampleModel(int dataType, int width, int height, int numBands) {
        return RasterFactory.createBandedSampleModel(dataType, width, height, numBands, null, null);
    }

    public static SampleModel createPixelInterleavedSampleModel(int dataType, int width, int height, int pixelStride, int scanlineStride, int[] bandOffsets) {
        if (bandOffsets == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory4"));
        }
        int minBandOff = bandOffsets[0];
        int maxBandOff = bandOffsets[0];
        for (int i = 1; i < bandOffsets.length; ++i) {
            minBandOff = Math.min(minBandOff, bandOffsets[i]);
            maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
        }
        if ((maxBandOff -= minBandOff) > scanlineStride) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory5"));
        }
        if (pixelStride * width > scanlineStride) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory6"));
        }
        if (pixelStride < maxBandOff) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory7"));
        }
        switch (dataType) {
            case 0: 
            case 1: {
                return new PixelInterleavedSampleModel(dataType, width, height, pixelStride, scanlineStride, bandOffsets);
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                return new ComponentSampleModelJAI(dataType, width, height, pixelStride, scanlineStride, bandOffsets);
            }
        }
        throw new IllegalArgumentException(JaiI18N.getString("RasterFactory3"));
    }

    public static SampleModel createPixelInterleavedSampleModel(int dataType, int width, int height, int numBands) {
        if (numBands < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory0"));
        }
        int[] bandOffsets = new int[numBands];
        for (int i = 0; i < numBands; ++i) {
            bandOffsets[i] = numBands - 1 - i;
        }
        return RasterFactory.createPixelInterleavedSampleModel(dataType, width, height, numBands, numBands * width, bandOffsets);
    }

    public static SampleModel createComponentSampleModel(SampleModel sm, int dataType, int width, int height, int numBands) {
        if (sm instanceof BandedSampleModel) {
            return RasterFactory.createBandedSampleModel(dataType, width, height, numBands);
        }
        return RasterFactory.createPixelInterleavedSampleModel(dataType, width, height, numBands);
    }

    public static ComponentColorModel createComponentColorModel(int dataType, ColorSpace colorSpace, boolean useAlpha, boolean premultiplied, int transparency) {
        if (colorSpace == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (transparency != 1 && transparency != 2 && transparency != 3) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory13"));
        }
        if (useAlpha && transparency == 1) {
            throw new IllegalArgumentException(JaiI18N.getString("RasterFactory14"));
        }
        if (!useAlpha) {
            premultiplied = false;
            transparency = 1;
        }
        int bands = colorSpace.getNumComponents();
        if (useAlpha) {
            ++bands;
        }
        int dataTypeSize = DataBuffer.getDataTypeSize(dataType);
        int[] bits = new int[bands];
        for (int i = 0; i < bands; ++i) {
            bits[i] = dataTypeSize;
        }
        switch (dataType) {
            case 0: {
                return new ComponentColorModel(colorSpace, bits, useAlpha, premultiplied, transparency, dataType);
            }
            case 1: {
                return new ComponentColorModel(colorSpace, bits, useAlpha, premultiplied, transparency, dataType);
            }
            case 3: {
                return new ComponentColorModel(colorSpace, bits, useAlpha, premultiplied, transparency, dataType);
            }
            case 4: {
                return new FloatDoubleColorModel(colorSpace, useAlpha, premultiplied, transparency, dataType);
            }
            case 5: {
                return new FloatDoubleColorModel(colorSpace, useAlpha, premultiplied, transparency, dataType);
            }
        }
        throw new IllegalArgumentException(JaiI18N.getString("RasterFactory8"));
    }
}

