/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.media.jai.JaiI18N;
import javax.media.jai.PixelAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.UnpackedImageData;

public abstract class ColorSpaceJAI
extends ColorSpace {
    private static final double maxXYZ = 1.999969482421875;
    private static final double power1 = 0.4166666666666667;
    private static double[] LUT = new double[256];
    private boolean isRGBPreferredIntermediary;

    public static WritableRaster CIEXYZToRGB(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        ColorSpaceJAI.checkParameters(src, srcComponentSize, dest, destComponentSize);
        SampleModel srcSampleModel = src.getSampleModel();
        if (srcComponentSize == null) {
            srcComponentSize = srcSampleModel.getSampleSize();
        }
        if (dest == null) {
            Point origin = new Point(src.getMinX(), src.getMinY());
            dest = RasterFactory.createWritableRaster(srcSampleModel, origin);
        }
        SampleModel dstSampleModel = dest.getSampleModel();
        if (destComponentSize == null) {
            destComponentSize = dstSampleModel.getSampleSize();
        }
        PixelAccessor srcAcc = new PixelAccessor(srcSampleModel, null);
        UnpackedImageData srcUid = srcAcc.getPixels(src, src.getBounds(), srcSampleModel.getDataType(), false);
        switch (srcSampleModel.getDataType()) {
            case 0: {
                ColorSpaceJAI.CIEXYZToRGBByte(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 1: 
            case 2: {
                ColorSpaceJAI.CIEXYZToRGBShort(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 3: {
                ColorSpaceJAI.CIEXYZToRGBInt(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 4: {
                ColorSpaceJAI.CIEXYZToRGBFloat(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 5: {
                ColorSpaceJAI.CIEXYZToRGBDouble(srcUid, srcComponentSize, dest, destComponentSize);
            }
        }
        return dest;
    }

    protected static void checkParameters(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        if (src == null) {
            throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI0"));
        }
        if (src.getNumBands() != 3) {
            throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI1"));
        }
        if (dest != null && dest.getNumBands() != 3) {
            throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI2"));
        }
        if (srcComponentSize != null && srcComponentSize.length != 3) {
            throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI3"));
        }
        if (destComponentSize != null && destComponentSize.length != 3) {
            throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI4"));
        }
    }

    static void convertToSigned(double[] buf, int dataType) {
        block3: {
            block2: {
                if (dataType != 2) break block2;
                for (int i = 0; i < buf.length; ++i) {
                    short temp = (short)((int)buf[i] & 0xFFFF);
                    buf[i] = temp;
                }
                break block3;
            }
            if (dataType != 3) break block3;
            for (int i = 0; i < buf.length; ++i) {
                int temp = (int)((long)buf[i] & 0xFFFFFFFFL);
                buf[i] = temp;
            }
        }
    }

    static void XYZ2RGB(float[] XYZ, float[] RGB) {
        RGB[0] = 2.9311228f * XYZ[0] - 1.4111496f * XYZ[1] - 0.6038046f * XYZ[2];
        RGB[1] = -0.8763701f * XYZ[0] + 1.7219844f * XYZ[1] + 0.0502565f * XYZ[2];
        RGB[2] = 0.05038065f * XYZ[0] - 0.187272f * XYZ[1] + 1.280027f * XYZ[2];
        for (int i = 0; i < 3; ++i) {
            float v = RGB[i];
            if (v < 0.0f) {
                v = 0.0f;
            }
            if (v < 0.0031308f) {
                RGB[i] = 12.92f * v;
                continue;
            }
            if (v > 1.0f) {
                v = 1.0f;
            }
            RGB[i] = (float)(1.055 * Math.pow(v, 0.4166666666666667) - 0.055);
        }
    }

    private static void roundValues(double[] data) {
        for (int i = 0; i < data.length; ++i) {
            data[i] = (long)(data[i] + 0.5);
        }
    }

    static void CIEXYZToRGBByte(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        byte[] xBuf = src.getByteData(0);
        byte[] yBuf = src.getByteData(1);
        byte[] zBuf = src.getByteData(2);
        float normx = (float)(1.999969482421875 / (double)((1L << srcComponentSize[0]) - 1L));
        float normy = (float)(1.999969482421875 / (double)((1L << srcComponentSize[1]) - 1L));
        float normz = (float)(1.999969482421875 / (double)((1L << srcComponentSize[2]) - 1L));
        double upperr = 1.0;
        double upperg = 1.0;
        double upperb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            upperr = (1L << destComponentSize[0]) - 1L;
            upperg = (1L << destComponentSize[1]) - 1L;
            upperb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int xStart = src.bandOffsets[0];
        int yStart = src.bandOffsets[1];
        int zStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int xIndex = xStart;
            int yIndex = yStart;
            int zIndex = zStart;
            while (i < width) {
                XYZ[0] = (float)(xBuf[xIndex] & 0xFF) * normx;
                XYZ[1] = (float)(yBuf[yIndex] & 0xFF) * normy;
                XYZ[2] = (float)(zBuf[zIndex] & 0xFF) * normz;
                ColorSpaceJAI.XYZ2RGB(XYZ, RGB);
                dstPixels[dIndex++] = upperr * (double)RGB[0];
                dstPixels[dIndex++] = upperg * (double)RGB[1];
                dstPixels[dIndex++] = upperb * (double)RGB[2];
                ++i;
                xIndex += srcPixelStride;
                yIndex += srcPixelStride;
                zIndex += srcPixelStride;
            }
            ++j;
            xStart += srcLineStride;
            yStart += srcLineStride;
            zStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private static void CIEXYZToRGBShort(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        short[] xBuf = src.getShortData(0);
        short[] yBuf = src.getShortData(1);
        short[] zBuf = src.getShortData(2);
        float normx = (float)(1.999969482421875 / (double)((1L << srcComponentSize[0]) - 1L));
        float normy = (float)(1.999969482421875 / (double)((1L << srcComponentSize[1]) - 1L));
        float normz = (float)(1.999969482421875 / (double)((1L << srcComponentSize[2]) - 1L));
        double upperr = 1.0;
        double upperg = 1.0;
        double upperb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            upperr = (1L << destComponentSize[0]) - 1L;
            upperg = (1L << destComponentSize[1]) - 1L;
            upperb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int xStart = src.bandOffsets[0];
        int yStart = src.bandOffsets[1];
        int zStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int xIndex = xStart;
            int yIndex = yStart;
            int zIndex = zStart;
            while (i < width) {
                XYZ[0] = (float)(xBuf[xIndex] & 0xFFFF) * normx;
                XYZ[1] = (float)(yBuf[yIndex] & 0xFFFF) * normy;
                XYZ[2] = (float)(zBuf[zIndex] & 0xFFFF) * normz;
                ColorSpaceJAI.XYZ2RGB(XYZ, RGB);
                dstPixels[dIndex++] = upperr * (double)RGB[0];
                dstPixels[dIndex++] = upperg * (double)RGB[1];
                dstPixels[dIndex++] = upperb * (double)RGB[2];
                ++i;
                xIndex += srcPixelStride;
                yIndex += srcPixelStride;
                zIndex += srcPixelStride;
            }
            ++j;
            xStart += srcLineStride;
            yStart += srcLineStride;
            zStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private static void CIEXYZToRGBInt(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        int[] xBuf = src.getIntData(0);
        int[] yBuf = src.getIntData(1);
        int[] zBuf = src.getIntData(2);
        float normx = (float)(1.999969482421875 / (double)((1L << srcComponentSize[0]) - 1L));
        float normy = (float)(1.999969482421875 / (double)((1L << srcComponentSize[1]) - 1L));
        float normz = (float)(1.999969482421875 / (double)((1L << srcComponentSize[2]) - 1L));
        double upperr = 1.0;
        double upperg = 1.0;
        double upperb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            upperr = (1L << destComponentSize[0]) - 1L;
            upperg = (1L << destComponentSize[1]) - 1L;
            upperb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int xStart = src.bandOffsets[0];
        int yStart = src.bandOffsets[1];
        int zStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int xIndex = xStart;
            int yIndex = yStart;
            int zIndex = zStart;
            while (i < width) {
                XYZ[0] = (float)((long)xBuf[xIndex] & 0xFFFFFFFFL) * normx;
                XYZ[1] = (float)((long)yBuf[yIndex] & 0xFFFFFFFFL) * normy;
                XYZ[2] = (float)((long)zBuf[zIndex] & 0xFFFFFFFFL) * normz;
                ColorSpaceJAI.XYZ2RGB(XYZ, RGB);
                dstPixels[dIndex++] = upperr * (double)RGB[0];
                dstPixels[dIndex++] = upperg * (double)RGB[1];
                dstPixels[dIndex++] = upperb * (double)RGB[2];
                ++i;
                xIndex += srcPixelStride;
                yIndex += srcPixelStride;
                zIndex += srcPixelStride;
            }
            ++j;
            xStart += srcLineStride;
            yStart += srcLineStride;
            zStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private static void CIEXYZToRGBFloat(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        float[] xBuf = src.getFloatData(0);
        float[] yBuf = src.getFloatData(1);
        float[] zBuf = src.getFloatData(2);
        double upperr = 1.0;
        double upperg = 1.0;
        double upperb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            upperr = (1L << destComponentSize[0]) - 1L;
            upperg = (1L << destComponentSize[1]) - 1L;
            upperb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int xStart = src.bandOffsets[0];
        int yStart = src.bandOffsets[1];
        int zStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int xIndex = xStart;
            int yIndex = yStart;
            int zIndex = zStart;
            while (i < width) {
                XYZ[0] = xBuf[xIndex];
                XYZ[1] = yBuf[yIndex];
                XYZ[2] = zBuf[zIndex];
                ColorSpaceJAI.XYZ2RGB(XYZ, RGB);
                dstPixels[dIndex++] = upperr * (double)RGB[0];
                dstPixels[dIndex++] = upperg * (double)RGB[1];
                dstPixels[dIndex++] = upperb * (double)RGB[2];
                ++i;
                xIndex += srcPixelStride;
                yIndex += srcPixelStride;
                zIndex += srcPixelStride;
            }
            ++j;
            xStart += srcLineStride;
            yStart += srcLineStride;
            zStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private static void CIEXYZToRGBDouble(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        double[] xBuf = src.getDoubleData(0);
        double[] yBuf = src.getDoubleData(1);
        double[] zBuf = src.getDoubleData(2);
        double upperr = 1.0;
        double upperg = 1.0;
        double upperb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            upperr = (1L << destComponentSize[0]) - 1L;
            upperg = (1L << destComponentSize[1]) - 1L;
            upperb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int xStart = src.bandOffsets[0];
        int yStart = src.bandOffsets[1];
        int zStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int xIndex = xStart;
            int yIndex = yStart;
            int zIndex = zStart;
            while (i < width) {
                XYZ[0] = (float)xBuf[xIndex];
                XYZ[1] = (float)yBuf[yIndex];
                XYZ[2] = (float)zBuf[zIndex];
                ColorSpaceJAI.XYZ2RGB(XYZ, RGB);
                dstPixels[dIndex++] = upperr * (double)RGB[0];
                dstPixels[dIndex++] = upperg * (double)RGB[1];
                dstPixels[dIndex++] = upperb * (double)RGB[2];
                ++i;
                xIndex += srcPixelStride;
                yIndex += srcPixelStride;
                zIndex += srcPixelStride;
            }
            ++j;
            xStart += srcLineStride;
            yStart += srcLineStride;
            zStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    public static WritableRaster RGBToCIEXYZ(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        ColorSpaceJAI.checkParameters(src, srcComponentSize, dest, destComponentSize);
        SampleModel srcSampleModel = src.getSampleModel();
        if (srcComponentSize == null) {
            srcComponentSize = srcSampleModel.getSampleSize();
        }
        if (dest == null) {
            Point origin = new Point(src.getMinX(), src.getMinY());
            dest = RasterFactory.createWritableRaster(srcSampleModel, origin);
        }
        SampleModel dstSampleModel = dest.getSampleModel();
        if (destComponentSize == null) {
            destComponentSize = dstSampleModel.getSampleSize();
        }
        PixelAccessor srcAcc = new PixelAccessor(srcSampleModel, null);
        UnpackedImageData srcUid = srcAcc.getPixels(src, src.getBounds(), srcSampleModel.getDataType(), false);
        switch (srcSampleModel.getDataType()) {
            case 0: {
                ColorSpaceJAI.RGBToCIEXYZByte(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 1: 
            case 2: {
                ColorSpaceJAI.RGBToCIEXYZShort(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 3: {
                ColorSpaceJAI.RGBToCIEXYZInt(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 4: {
                ColorSpaceJAI.RGBToCIEXYZFloat(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 5: {
                ColorSpaceJAI.RGBToCIEXYZDouble(srcUid, srcComponentSize, dest, destComponentSize);
            }
        }
        return dest;
    }

    static void RGB2XYZ(float[] RGB, float[] XYZ) {
        for (int i = 0; i < 3; ++i) {
            if (RGB[i] < 0.040449936f) {
                int n = i;
                RGB[n] = RGB[n] / 12.92f;
                continue;
            }
            RGB[i] = (float)Math.pow(((double)RGB[i] + 0.055) / 1.055, 2.4);
        }
        XYZ[0] = 0.45593762f * RGB[0] + 0.39533818f * RGB[1] + 0.19954965f * RGB[2];
        XYZ[1] = 0.23157515f * RGB[0] + 0.7790526f * RGB[1] + 0.07864978f * RGB[2];
        XYZ[2] = 0.01593493f * RGB[0] + 0.09841772f * RGB[1] + 0.7848861f * RGB[2];
    }

    private static void RGBToCIEXYZByte(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isInt;
        double normx;
        byte[] rBuf = src.getByteData(0);
        byte[] gBuf = src.getByteData(1);
        byte[] bBuf = src.getByteData(2);
        int normr = 8 - srcComponentSize[0];
        int normg = 8 - srcComponentSize[1];
        int normb = 8 - srcComponentSize[2];
        double normy = normx = 1.0;
        double normz = normx;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isInt = dstType < 4;
        if (isInt) {
            normx = (double)((1L << destComponentSize[0]) - 1L) / 1.999969482421875;
            normy = (double)((1L << destComponentSize[1]) - 1L) / 1.999969482421875;
            normz = (double)((1L << destComponentSize[2]) - 1L) / 1.999969482421875;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int rStart = src.bandOffsets[0];
        int gStart = src.bandOffsets[1];
        int bStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int rIndex = rStart;
            int gIndex = gStart;
            int bIndex = bStart;
            while (i < width) {
                double R = LUT[(rBuf[rIndex] & 0xFF) << normr];
                double G = LUT[(gBuf[gIndex] & 0xFF) << normg];
                double B = LUT[(bBuf[bIndex] & 0xFF) << normb];
                if (isInt) {
                    dstPixels[dIndex++] = (0.45593763 * R + 0.39533819 * G + 0.19954964 * B) * normx;
                    dstPixels[dIndex++] = (0.23157515 * R + 0.77905262 * G + 0.07864978 * B) * normy;
                    dstPixels[dIndex++] = (0.01593493 * R + 0.09841772 * G + 0.78488615 * B) * normz;
                } else {
                    dstPixels[dIndex++] = 0.45593763 * R + 0.39533819 * G + 0.19954964 * B;
                    dstPixels[dIndex++] = 0.23157515 * R + 0.77905262 * G + 0.07864978 * B;
                    dstPixels[dIndex++] = 0.01593493 * R + 0.09841772 * G + 0.78488615 * B;
                }
                ++i;
                rIndex += srcPixelStride;
                gIndex += srcPixelStride;
                bIndex += srcPixelStride;
            }
            ++j;
            rStart += srcLineStride;
            gStart += srcLineStride;
            bStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private static void RGBToCIEXYZShort(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isInt;
        short[] rBuf = src.getShortData(0);
        short[] gBuf = src.getShortData(1);
        short[] bBuf = src.getShortData(2);
        float normr = (1 << srcComponentSize[0]) - 1;
        float normg = (1 << srcComponentSize[1]) - 1;
        float normb = (1 << srcComponentSize[2]) - 1;
        double normx = 1.0;
        double normy = 1.0;
        double normz = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isInt = dstType < 4;
        if (isInt) {
            normx = (double)((1L << destComponentSize[0]) - 1L) / 1.999969482421875;
            normy = (double)((1L << destComponentSize[1]) - 1L) / 1.999969482421875;
            normz = (double)((1L << destComponentSize[2]) - 1L) / 1.999969482421875;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int rStart = src.bandOffsets[0];
        int gStart = src.bandOffsets[1];
        int bStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int rIndex = rStart;
            int gIndex = gStart;
            int bIndex = bStart;
            while (i < width) {
                RGB[0] = (float)(rBuf[rIndex] & 0xFFFF) / normr;
                RGB[1] = (float)(gBuf[gIndex] & 0xFFFF) / normg;
                RGB[2] = (float)(bBuf[bIndex] & 0xFFFF) / normb;
                ColorSpaceJAI.RGB2XYZ(RGB, XYZ);
                if (isInt) {
                    dstPixels[dIndex++] = (double)XYZ[0] * normx;
                    dstPixels[dIndex++] = (double)XYZ[1] * normy;
                    dstPixels[dIndex++] = (double)XYZ[2] * normz;
                } else {
                    dstPixels[dIndex++] = XYZ[0];
                    dstPixels[dIndex++] = XYZ[1];
                    dstPixels[dIndex++] = XYZ[2];
                }
                ++i;
                rIndex += srcPixelStride;
                gIndex += srcPixelStride;
                bIndex += srcPixelStride;
            }
            ++j;
            rStart += srcLineStride;
            gStart += srcLineStride;
            bStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private static void RGBToCIEXYZInt(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isInt;
        int[] rBuf = src.getIntData(0);
        int[] gBuf = src.getIntData(1);
        int[] bBuf = src.getIntData(2);
        float normr = (1L << srcComponentSize[0]) - 1L;
        float normg = (1L << srcComponentSize[1]) - 1L;
        float normb = (1L << srcComponentSize[2]) - 1L;
        double normx = 1.0;
        double normy = 1.0;
        double normz = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isInt = dstType < 4;
        if (isInt) {
            normx = (double)((1L << destComponentSize[0]) - 1L) / 1.999969482421875;
            normy = (double)((1L << destComponentSize[1]) - 1L) / 1.999969482421875;
            normz = (double)((1L << destComponentSize[2]) - 1L) / 1.999969482421875;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int rStart = src.bandOffsets[0];
        int gStart = src.bandOffsets[1];
        int bStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int rIndex = rStart;
            int gIndex = gStart;
            int bIndex = bStart;
            while (i < width) {
                RGB[0] = (float)((long)rBuf[rIndex] & 0xFFFFFFFFL) / normr;
                RGB[1] = (float)((long)gBuf[gIndex] & 0xFFFFFFFFL) / normg;
                RGB[2] = (float)((long)bBuf[bIndex] & 0xFFFFFFFFL) / normb;
                ColorSpaceJAI.RGB2XYZ(RGB, XYZ);
                if (isInt) {
                    dstPixels[dIndex++] = (double)XYZ[0] * normx;
                    dstPixels[dIndex++] = (double)XYZ[1] * normx;
                    dstPixels[dIndex++] = (double)XYZ[2] * normx;
                } else {
                    dstPixels[dIndex++] = XYZ[0];
                    dstPixels[dIndex++] = XYZ[1];
                    dstPixels[dIndex++] = XYZ[2];
                }
                ++i;
                rIndex += srcPixelStride;
                gIndex += srcPixelStride;
                bIndex += srcPixelStride;
            }
            ++j;
            rStart += srcLineStride;
            gStart += srcLineStride;
            bStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private static void RGBToCIEXYZFloat(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isInt;
        float[] rBuf = src.getFloatData(0);
        float[] gBuf = src.getFloatData(1);
        float[] bBuf = src.getFloatData(2);
        double normx = 1.0;
        double normy = 1.0;
        double normz = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isInt = dstType < 4;
        if (isInt) {
            normx = (double)((1L << destComponentSize[0]) - 1L) / 1.999969482421875;
            normy = (double)((1L << destComponentSize[1]) - 1L) / 1.999969482421875;
            normz = (double)((1L << destComponentSize[2]) - 1L) / 1.999969482421875;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int rStart = src.bandOffsets[0];
        int gStart = src.bandOffsets[1];
        int bStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int rIndex = rStart;
            int gIndex = gStart;
            int bIndex = bStart;
            while (i < width) {
                RGB[0] = rBuf[rIndex];
                RGB[1] = gBuf[gIndex];
                RGB[2] = bBuf[bIndex];
                ColorSpaceJAI.RGB2XYZ(RGB, XYZ);
                if (isInt) {
                    dstPixels[dIndex++] = (double)XYZ[0] * normx;
                    dstPixels[dIndex++] = (double)XYZ[1] * normx;
                    dstPixels[dIndex++] = (double)XYZ[2] * normx;
                } else {
                    dstPixels[dIndex++] = XYZ[0];
                    dstPixels[dIndex++] = XYZ[1];
                    dstPixels[dIndex++] = XYZ[2];
                }
                ++i;
                rIndex += srcPixelStride;
                gIndex += srcPixelStride;
                bIndex += srcPixelStride;
            }
            ++j;
            rStart += srcLineStride;
            gStart += srcLineStride;
            bStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private static void RGBToCIEXYZDouble(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isInt;
        double[] rBuf = src.getDoubleData(0);
        double[] gBuf = src.getDoubleData(1);
        double[] bBuf = src.getDoubleData(2);
        double normx = 1.0;
        double normy = 1.0;
        double normz = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isInt = dstType < 4;
        if (isInt) {
            normx = (double)((1L << destComponentSize[0]) - 1L) / 1.999969482421875;
            normy = (double)((1L << destComponentSize[1]) - 1L) / 1.999969482421875;
            normz = (double)((1L << destComponentSize[2]) - 1L) / 1.999969482421875;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int rStart = src.bandOffsets[0];
        int gStart = src.bandOffsets[1];
        int bStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        float[] XYZ = new float[3];
        float[] RGB = new float[3];
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int rIndex = rStart;
            int gIndex = gStart;
            int bIndex = bStart;
            while (i < width) {
                RGB[0] = (float)rBuf[rIndex];
                RGB[1] = (float)gBuf[gIndex];
                RGB[2] = (float)bBuf[bIndex];
                ColorSpaceJAI.RGB2XYZ(RGB, XYZ);
                if (isInt) {
                    dstPixels[dIndex++] = (double)XYZ[0] * normx;
                    dstPixels[dIndex++] = (double)XYZ[1] * normx;
                    dstPixels[dIndex++] = (double)XYZ[2] * normx;
                } else {
                    dstPixels[dIndex++] = XYZ[0];
                    dstPixels[dIndex++] = XYZ[1];
                    dstPixels[dIndex++] = XYZ[2];
                }
                ++i;
                rIndex += srcPixelStride;
                gIndex += srcPixelStride;
                bIndex += srcPixelStride;
            }
            ++j;
            rStart += srcLineStride;
            gStart += srcLineStride;
            bStart += srcLineStride;
        }
        if (dstType < 4) {
            ColorSpaceJAI.roundValues(dstPixels);
        }
        ColorSpaceJAI.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    protected ColorSpaceJAI(int type, int numComponents, boolean isRGBPreferredIntermediary) {
        super(type, numComponents);
        this.isRGBPreferredIntermediary = isRGBPreferredIntermediary;
    }

    public boolean isRGBPreferredIntermediary() {
        return this.isRGBPreferredIntermediary;
    }

    public abstract WritableRaster fromCIEXYZ(Raster var1, int[] var2, WritableRaster var3, int[] var4);

    public abstract WritableRaster fromRGB(Raster var1, int[] var2, WritableRaster var3, int[] var4);

    public abstract WritableRaster toCIEXYZ(Raster var1, int[] var2, WritableRaster var3, int[] var4);

    public abstract WritableRaster toRGB(Raster var1, int[] var2, WritableRaster var3, int[] var4);

    static {
        for (int i = 0; i < 256; ++i) {
            double v = (double)i / 255.0;
            ColorSpaceJAI.LUT[i] = v < 0.040449936 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
        }
    }
}

