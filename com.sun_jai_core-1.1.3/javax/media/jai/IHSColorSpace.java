/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;
import javax.media.jai.ColorSpaceJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.UnpackedImageData;

public final class IHSColorSpace
extends ColorSpaceJAI {
    private static final double PI2 = Math.PI * 2;
    private static final double PI23 = 2.0943951023931953;
    private static final double PI43 = 4.1887902047863905;
    private static final double SQRT3 = Math.sqrt(3.0);
    private static final double BYTESCALE = 40.58451048843331;
    private static SoftReference reference = new SoftReference<Object>(null);
    private static byte[] acosTable = null;
    private static double[] sqrtTable = null;
    private static double[] tanTable = null;
    private static SoftReference acosSoftRef;
    private static SoftReference sqrtSoftRef;
    private static SoftReference tanSoftRef;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static IHSColorSpace getInstance() {
        SoftReference softReference = reference;
        synchronized (softReference) {
            IHSColorSpace cs;
            Object referent = reference.get();
            if (referent == null) {
                cs = new IHSColorSpace();
                reference = new SoftReference<IHSColorSpace>(cs);
            } else {
                cs = (IHSColorSpace)referent;
            }
            return cs;
        }
    }

    protected IHSColorSpace() {
        super(7, 3, true);
    }

    private synchronized void generateACosTable() {
        if (acosSoftRef == null || acosSoftRef.get() == null) {
            acosTable = new byte[1001];
            acosSoftRef = new SoftReference<byte[]>(acosTable);
            for (int i = 0; i <= 1000; ++i) {
                IHSColorSpace.acosTable[i] = (byte)(40.58451048843331 * Math.acos((double)(i - 500) * 0.002) + 0.5);
            }
        }
    }

    private synchronized void generateSqrtTable() {
        if (sqrtSoftRef == null || sqrtSoftRef.get() == null) {
            sqrtTable = new double[1001];
            sqrtSoftRef = new SoftReference<double[]>(sqrtTable);
            for (int i = 0; i <= 1000; ++i) {
                IHSColorSpace.sqrtTable[i] = Math.sqrt((double)i / 1000.0);
            }
        }
    }

    private synchronized void generateTanTable() {
        if (tanSoftRef == null || tanSoftRef.get() == null) {
            tanTable = new double[256];
            tanSoftRef = new SoftReference<double[]>(tanTable);
            for (int i = 0; i < 256; ++i) {
                IHSColorSpace.tanTable[i] = Math.tan((double)i * (Math.PI * 2) / 255.0);
            }
        }
    }

    public float[] fromCIEXYZ(float[] colorValue) {
        float[] rgb = new float[3];
        IHSColorSpace.XYZ2RGB(colorValue, rgb);
        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];
        float[] ihs = new float[3];
        ihs[0] = (r + g + b) / 3.0f;
        float drg = r - g;
        float drb = r - b;
        float temp = (float)Math.sqrt((double)drg * (double)drg + (double)drb * (double)(drb - drg));
        if (temp != 0.0f) {
            temp = (float)Math.acos((double)(drg + drb) / (double)temp / 2.0);
            ihs[1] = g < b ? (float)(Math.PI * 2 - (double)temp) : temp;
        } else {
            ihs[1] = (float)Math.PI * 2;
        }
        float min = r < g ? r : g;
        min = min < b ? min : b;
        ihs[2] = ihs[0] == 0.0f ? 0.0f : 1.0f - min / ihs[0];
        return ihs;
    }

    public float[] fromRGB(float[] rgbValue) {
        float r = rgbValue[0];
        float g = rgbValue[1];
        float b = rgbValue[2];
        float f = r < 0.0f ? 0.0f : (r = r > 1.0f ? 1.0f : r);
        float f2 = g < 0.0f ? 0.0f : (g = g > 1.0f ? 1.0f : g);
        b = b < 0.0f ? 0.0f : (b > 1.0f ? 1.0f : b);
        float[] ihs = new float[3];
        ihs[0] = (r + g + b) / 3.0f;
        float drg = r - g;
        float drb = r - b;
        float temp = (float)Math.sqrt((double)drg * (double)drg + (double)drb * (double)(drb - drg));
        if (temp != 0.0f) {
            temp = (float)Math.acos((double)(drg + drb) / (double)temp / 2.0);
            ihs[1] = g < b ? (float)(Math.PI * 2 - (double)temp) : temp;
        } else {
            ihs[1] = (float)Math.PI * 2;
        }
        float min = r < g ? r : g;
        min = min < b ? min : b;
        ihs[2] = ihs[0] == 0.0f ? 0.0f : 1.0f - min / ihs[0];
        return ihs;
    }

    public float[] toCIEXYZ(float[] colorValue) {
        float c2;
        float c1;
        float i = colorValue[0];
        float h = colorValue[1];
        float s = colorValue[2];
        float f = i < 0.0f ? 0.0f : (i = i > 1.0f ? 1.0f : i);
        float f2 = h < 0.0f ? 0.0f : (h = h > (float)Math.PI * 2 ? (float)Math.PI * 2 : h);
        s = s < 0.0f ? 0.0f : (s > 1.0f ? 1.0f : s);
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        if (s == 0.0f) {
            g = b = i;
            r = b;
        } else if ((double)h >= 2.0943951023931953 && (double)h < 4.1887902047863905) {
            r = (1.0f - s) * i;
            c1 = 3.0f * i - r;
            c2 = (float)(SQRT3 * (double)(r - i) * Math.tan(h));
            g = (c1 + c2) / 2.0f;
            b = (c1 - c2) / 2.0f;
        } else if ((double)h > 4.1887902047863905) {
            g = (1.0f - s) * i;
            c1 = 3.0f * i - g;
            c2 = (float)(SQRT3 * (double)(g - i) * Math.tan((double)h - 2.0943951023931953));
            b = (c1 + c2) / 2.0f;
            r = (c1 - c2) / 2.0f;
        } else if ((double)h < 2.0943951023931953) {
            b = (1.0f - s) * i;
            c1 = 3.0f * i - b;
            c2 = (float)(SQRT3 * (double)(b - i) * Math.tan((double)h - 4.1887902047863905));
            r = (c1 + c2) / 2.0f;
            g = (c1 - c2) / 2.0f;
        }
        float[] xyz = new float[3];
        float[] rgb = new float[]{r, g, b};
        IHSColorSpace.RGB2XYZ(rgb, xyz);
        return xyz;
    }

    public float[] toRGB(float[] colorValue) {
        float i = colorValue[0];
        float h = colorValue[1];
        float s = colorValue[2];
        float f = i < 0.0f ? 0.0f : (i = i > 1.0f ? 1.0f : i);
        float f2 = h < 0.0f ? 0.0f : (h = h > (float)Math.PI * 2 ? (float)Math.PI * 2 : h);
        s = s < 0.0f ? 0.0f : (s > 1.0f ? 1.0f : s);
        float[] rgb = new float[3];
        if (s == 0.0f) {
            rgb[1] = rgb[2] = i;
            rgb[0] = rgb[2];
        } else if ((double)h >= 2.0943951023931953 && (double)h <= 4.1887902047863905) {
            float r = (1.0f - s) * i;
            float c1 = 3.0f * i - r;
            float c2 = (float)(SQRT3 * (double)(r - i) * Math.tan(h));
            rgb[0] = r;
            rgb[1] = (c1 + c2) / 2.0f;
            rgb[2] = (c1 - c2) / 2.0f;
        } else if ((double)h > 4.1887902047863905) {
            float g = (1.0f - s) * i;
            float c1 = 3.0f * i - g;
            float c2 = (float)(SQRT3 * (double)(g - i) * Math.tan((double)h - 2.0943951023931953));
            rgb[0] = (c1 - c2) / 2.0f;
            rgb[1] = g;
            rgb[2] = (c1 + c2) / 2.0f;
        } else if ((double)h < 2.0943951023931953) {
            float b = (1.0f - s) * i;
            float c1 = 3.0f * i - b;
            float c2 = (float)(SQRT3 * (double)(b - i) * Math.tan((double)h - 4.1887902047863905));
            rgb[0] = (c1 + c2) / 2.0f;
            rgb[1] = (c1 - c2) / 2.0f;
            rgb[2] = b;
        }
        return rgb;
    }

    public WritableRaster fromCIEXYZ(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        WritableRaster tempRas = IHSColorSpace.CIEXYZToRGB(src, srcComponentSize, null, null);
        return this.fromRGB(tempRas, tempRas.getSampleModel().getSampleSize(), dest, destComponentSize);
    }

    public WritableRaster fromRGB(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        IHSColorSpace.checkParameters(src, srcComponentSize, dest, destComponentSize);
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
                this.fromRGBByte(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 1: 
            case 2: {
                this.fromRGBShort(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 3: {
                this.fromRGBInt(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 4: {
                this.fromRGBFloat(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 5: {
                this.fromRGBDouble(srcUid, srcComponentSize, dest, destComponentSize);
            }
        }
        return dest;
    }

    private void fromRGBByte(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isByte;
        byte[] rBuf = src.getByteData(0);
        byte[] gBuf = src.getByteData(1);
        byte[] bBuf = src.getByteData(2);
        int normr = 8 - srcComponentSize[0];
        int normg = 8 - srcComponentSize[1];
        int normb = 8 - srcComponentSize[2];
        double normi = 0.00392156862745098;
        double normh = 1.0;
        double norms = 1.0;
        int bnormi = 0;
        int bnormh = 0;
        int bnorms = 0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isByte = dstType == 0;
        if (isByte) {
            bnormi = 8 - destComponentSize[0];
            bnormh = 8 - destComponentSize[1];
            bnorms = 8 - destComponentSize[2];
            this.generateACosTable();
            this.generateSqrtTable();
        } else if (dstType < 4) {
            normi = (double)((1L << destComponentSize[0]) - 1L) / 255.0;
            normh = (double)((1L << destComponentSize[1]) - 1L) / (Math.PI * 2);
            norms = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = null;
        int[] dstIntPixels = null;
        if (isByte) {
            dstIntPixels = new int[3 * height * width];
        } else {
            dstPixels = new double[3 * height * width];
        }
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
                double temp;
                float intensity;
                short R = (short)((rBuf[rIndex] & 0xFF) << normr);
                short G = (short)((gBuf[gIndex] & 0xFF) << normg);
                short B = (short)((bBuf[bIndex] & 0xFF) << normb);
                if (isByte) {
                    intensity = (float)(R + G + B) / 3.0f;
                    dstIntPixels[dIndex++] = (short)(intensity + 0.5f) >> bnormi;
                    short drg = (short)(R - G);
                    short drb = (short)(R - B);
                    int tint = drg * drg + drb * (drb - drg);
                    short sum = (short)(drg + drb);
                    temp = tint != 0 ? sqrtTable[(int)(250.0 * (double)sum * (double)sum / (double)tint + 0.5)] : -1.0;
                    byte hue = sum > 0 ? acosTable[(int)(500.0 * temp + 0.5) + 500] : acosTable[(int)(-500.0 * temp - 0.5) + 500];
                    dstIntPixels[dIndex++] = B >= G ? 255 - hue >> bnormh : hue >> bnormh;
                    short min = G > B ? B : G;
                    min = R > min ? min : R;
                    dstIntPixels[dIndex++] = 255 - (int)((float)(255 * min) / intensity + 0.5f) >> bnorms;
                } else {
                    intensity = (float)(R + G + B) / 3.0f;
                    dstPixels[dIndex++] = normi * (double)intensity;
                    double drg = R - G;
                    double drb = R - B;
                    temp = Math.sqrt(drg * drg + drb * (drb - drg));
                    if (temp != 0.0) {
                        temp = Math.acos((drg + drb) / temp / 2.0);
                        if (B >= G) {
                            temp = Math.PI * 2 - temp;
                        }
                    } else {
                        temp = Math.PI * 2;
                    }
                    dstPixels[dIndex++] = normh * temp;
                    double min = G > B ? (double)B : (double)G;
                    min = (double)R > min ? min : (double)R;
                    dstPixels[dIndex++] = norms * (1.0 - min / (double)intensity);
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
        if (isByte) {
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
        } else {
            IHSColorSpace.convertToSigned(dstPixels, dstType);
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
        }
    }

    private void fromRGBShort(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isByte;
        short[] rBuf = src.getShortData(0);
        short[] gBuf = src.getShortData(1);
        short[] bBuf = src.getShortData(2);
        int normr = 16 - srcComponentSize[0];
        int normg = 16 - srcComponentSize[1];
        int normb = 16 - srcComponentSize[2];
        double normi = 1.5259021896696422E-5;
        double normh = 1.0;
        double norms = 1.0;
        int bnormi = 0;
        int bnormh = 0;
        int bnorms = 0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isByte = dstType == 0;
        if (isByte) {
            bnormi = 16 - destComponentSize[0];
            bnormh = 8 - destComponentSize[1];
            bnorms = 8 - destComponentSize[2];
            this.generateACosTable();
            this.generateSqrtTable();
        } else if (dstType < 4) {
            normi = (double)((1L << destComponentSize[0]) - 1L) / 65535.0;
            normh = (double)((1L << destComponentSize[1]) - 1L) / (Math.PI * 2);
            norms = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = null;
        int[] dstIntPixels = null;
        if (isByte) {
            dstIntPixels = new int[3 * height * width];
        } else {
            dstPixels = new double[3 * height * width];
        }
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
                float intensity;
                int R = (rBuf[rIndex] & 0xFFFF) << normr;
                int G = (gBuf[gIndex] & 0xFFFF) << normg;
                int B = (bBuf[bIndex] & 0xFFFF) << normb;
                if (isByte) {
                    intensity = (float)(R + G + B) / 3.0f;
                    dstIntPixels[dIndex++] = (int)(intensity + 0.5f) >> bnormi;
                    int drg = R - G;
                    int drb = R - B;
                    double tint = (double)drg * (double)drg + (double)drb * (double)(drb - drg);
                    double sum = drg + drb;
                    double temp = tint != 0.0 ? sqrtTable[(int)(250.0 * sum * sum / tint + 0.5)] : -1.0;
                    byte hue = sum > 0.0 ? acosTable[(int)(500.0 * temp + 0.5) + 500] : acosTable[(int)(-500.0 * temp - 0.5) + 500];
                    dstIntPixels[dIndex++] = B >= G ? 255 - hue >> bnormh : hue >> bnormh;
                    int min = G > B ? B : G;
                    min = R > min ? min : R;
                    dstIntPixels[dIndex++] = 255 - (int)((float)(255 * min) / intensity + 0.5f) >> bnorms;
                } else {
                    intensity = (float)(R + G + B) / 3.0f;
                    dstPixels[dIndex++] = normi * (double)intensity;
                    double drg = R - G;
                    double drb = R - B;
                    double temp = Math.sqrt(drg * drg + drb * (drb - drg));
                    if (temp != 0.0) {
                        temp = Math.acos((drg + drb) / temp / 2.0);
                        if (B >= G) {
                            temp = Math.PI * 2 - temp;
                        }
                    } else {
                        temp = Math.PI * 2;
                    }
                    dstPixels[dIndex++] = normh * temp;
                    double min = G > B ? (double)B : (double)G;
                    min = (double)R > min ? min : (double)R;
                    dstPixels[dIndex++] = norms * (1.0 - min / (double)intensity);
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
        if (isByte) {
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
        } else {
            IHSColorSpace.convertToSigned(dstPixels, dstType);
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
        }
    }

    private void fromRGBInt(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isByte;
        int[] rBuf = src.getIntData(0);
        int[] gBuf = src.getIntData(1);
        int[] bBuf = src.getIntData(2);
        int normr = 32 - srcComponentSize[0];
        int normg = 32 - srcComponentSize[1];
        int normb = 32 - srcComponentSize[2];
        double range = 4.294967295E9;
        double normi = 1.0 / range;
        double normh = 1.0;
        double norms = 1.0;
        int bnormi = 0;
        int bnormh = 0;
        int bnorms = 0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isByte = dstType == 0;
        if (isByte) {
            bnormi = 32 - destComponentSize[0];
            bnormh = 8 - destComponentSize[1];
            bnorms = 8 - destComponentSize[2];
            this.generateACosTable();
            this.generateSqrtTable();
        } else if (dstType < 4) {
            normi = (double)((1L << destComponentSize[0]) - 1L) / range;
            normh = (double)((1L << destComponentSize[1]) - 1L) / (Math.PI * 2);
            norms = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = null;
        int[] dstIntPixels = null;
        if (isByte) {
            dstIntPixels = new int[3 * height * width];
        } else {
            dstPixels = new double[3 * height * width];
        }
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
                float intensity;
                long R = ((long)rBuf[rIndex] & 0xFFFFFFFFL) << normr;
                long G = ((long)gBuf[gIndex] & 0xFFFFFFFFL) << normg;
                long B = ((long)bBuf[bIndex] & 0xFFFFFFFFL) << normb;
                if (isByte) {
                    intensity = (float)(R + G + B) / 3.0f;
                    dstIntPixels[dIndex++] = (int)((long)(intensity + 0.5f) >> bnormi);
                    long drg = R - G;
                    long drb = R - B;
                    double tint = (double)drg * (double)drg + (double)drb * (double)(drb - drg);
                    double sum = drg + drb;
                    double temp = tint != 0.0 ? sqrtTable[(int)(250.0 * sum * sum / tint + 0.5)] : -1.0;
                    byte hue = sum > 0.0 ? acosTable[(int)(500.0 * temp + 0.5) + 500] : acosTable[(int)(-500.0 * temp - 0.5) + 500];
                    dstIntPixels[dIndex++] = B >= G ? 255 - hue >> bnormh : hue >> bnormh;
                    long min = G > B ? B : G;
                    min = R > min ? min : R;
                    dstIntPixels[dIndex++] = 255 - (int)((float)(255L * min) / intensity + 0.5f) >> bnorms;
                } else {
                    intensity = (float)(R + G + B) / 3.0f;
                    dstPixels[dIndex++] = normi * (double)intensity;
                    double drg = R - G;
                    double drb = R - B;
                    double temp = Math.sqrt(drg * drg + drb * (drb - drg));
                    if (temp != 0.0) {
                        temp = Math.acos((drg + drb) / temp / 2.0);
                        if (B >= G) {
                            temp = Math.PI * 2 - temp;
                        }
                    } else {
                        temp = Math.PI * 2;
                    }
                    dstPixels[dIndex++] = normh * temp;
                    double min = G > B ? (double)B : (double)G;
                    min = (double)R > min ? min : (double)R;
                    dstPixels[dIndex++] = norms * (1.0 - min / (double)intensity);
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
        if (isByte) {
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
        } else {
            IHSColorSpace.convertToSigned(dstPixels, dstType);
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
        }
    }

    private void fromRGBFloat(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isByte;
        float[] rBuf = src.getFloatData(0);
        float[] gBuf = src.getFloatData(1);
        float[] bBuf = src.getFloatData(2);
        double normi = 1.0;
        double normh = 1.0;
        double norms = 1.0;
        int bnormi = 0;
        int bnormh = 0;
        int bnorms = 0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isByte = dstType == 0;
        if (isByte) {
            bnormi = (1 << destComponentSize[0]) - 1;
            bnormh = 8 - destComponentSize[1];
            bnorms = 8 - destComponentSize[2];
            this.generateACosTable();
            this.generateSqrtTable();
        } else if (dstType < 4) {
            normi = (1L << destComponentSize[0]) - 1L;
            normh = (double)((1L << destComponentSize[1]) - 1L) / (Math.PI * 2);
            norms = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = null;
        int[] dstIntPixels = null;
        if (isByte) {
            dstIntPixels = new int[3 * height * width];
        } else {
            dstPixels = new double[3 * height * width];
        }
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
                float intensity;
                float R = rBuf[rIndex];
                float G = gBuf[gIndex];
                float B = bBuf[bIndex];
                if (isByte) {
                    intensity = (R + G + B) / 3.0f;
                    dstIntPixels[dIndex++] = (int)(intensity * (float)bnormi + 0.5f);
                    float drg = R - G;
                    float drb = R - B;
                    double tint = (double)drg * (double)drg + (double)drb * (double)(drb - drg);
                    double sum = drg + drb;
                    double temp = tint != 0.0 ? sqrtTable[(int)(250.0 * sum * sum / tint + 0.5)] : -1.0;
                    byte hue = sum > 0.0 ? acosTable[(int)(500.0 * temp + 0.5) + 500] : acosTable[(int)(-500.0 * temp - 0.5) + 500];
                    dstIntPixels[dIndex++] = B >= G ? 255 - hue >> bnormh : hue >> bnormh;
                    float min = G > B ? B : G;
                    min = R > min ? min : R;
                    dstIntPixels[dIndex++] = 255 - (int)(255.0f * min / intensity + 0.5f) >> bnorms;
                } else {
                    intensity = (R + G + B) / 3.0f;
                    dstPixels[dIndex++] = normi * (double)intensity;
                    double drg = R - G;
                    double drb = R - B;
                    double temp = Math.sqrt(drg * drg + drb * (drb - drg));
                    if (temp != 0.0) {
                        temp = Math.acos((drg + drb) / temp / 2.0);
                        if (B >= G) {
                            temp = Math.PI * 2 - temp;
                        }
                    } else {
                        temp = Math.PI * 2;
                    }
                    dstPixels[dIndex++] = normh * temp;
                    double min = G > B ? (double)B : (double)G;
                    min = (double)R > min ? min : (double)R;
                    dstPixels[dIndex++] = norms * (1.0 - min / (double)intensity);
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
        if (isByte) {
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
        } else {
            IHSColorSpace.convertToSigned(dstPixels, dstType);
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
        }
    }

    private void fromRGBDouble(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isByte;
        double[] rBuf = src.getDoubleData(0);
        double[] gBuf = src.getDoubleData(1);
        double[] bBuf = src.getDoubleData(2);
        double normi = 1.0;
        double normh = 1.0;
        double norms = 1.0;
        int bnormi = 0;
        int bnormh = 0;
        int bnorms = 0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isByte = dstType == 0;
        if (isByte) {
            bnormi = (1 << destComponentSize[0]) - 1;
            bnormh = 8 - destComponentSize[1];
            bnorms = 8 - destComponentSize[2];
            this.generateACosTable();
            this.generateSqrtTable();
        } else if (dstType < 4) {
            normi = (1L << destComponentSize[0]) - 1L;
            normh = (double)((1L << destComponentSize[1]) - 1L) / (Math.PI * 2);
            norms = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = null;
        int[] dstIntPixels = null;
        if (isByte) {
            dstIntPixels = new int[3 * height * width];
        } else {
            dstPixels = new double[3 * height * width];
        }
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
                double drb;
                double drg;
                double intensity;
                double R = rBuf[rIndex];
                double G = gBuf[gIndex];
                double B = bBuf[bIndex];
                if (isByte) {
                    intensity = (R + G + B) / 3.0;
                    dstIntPixels[dIndex++] = (int)(intensity * (double)bnormi + 0.5);
                    drg = R - G;
                    drb = R - B;
                    double tint = drg * drg + drb * (drb - drg);
                    double sum = drg + drb;
                    double temp = tint != 0.0 ? sqrtTable[(int)(250.0 * sum * sum / tint + 0.5)] : -1.0;
                    byte hue = sum > 0.0 ? acosTable[(int)(500.0 * temp + 0.5) + 500] : acosTable[(int)(-500.0 * temp - 0.5) + 500];
                    dstIntPixels[dIndex++] = B >= G ? 255 - hue >> bnormh : hue >> bnormh;
                    double min = G > B ? B : G;
                    min = R > min ? min : R;
                    dstIntPixels[dIndex++] = 255 - (int)(255.0 * min / intensity + 0.5) >> bnorms;
                } else {
                    intensity = (R + G + B) / 3.0;
                    dstPixels[dIndex++] = normi * intensity;
                    drg = R - G;
                    drb = R - B;
                    double temp = Math.sqrt(drg * drg + drb * (drb - drg));
                    if (temp != 0.0) {
                        temp = Math.acos((drg + drb) / temp / 2.0);
                        if (B >= G) {
                            temp = Math.PI * 2 - temp;
                        }
                    } else {
                        temp = Math.PI * 2;
                    }
                    dstPixels[dIndex++] = normh * temp;
                    double min = G > B ? B : G;
                    min = R > min ? min : R;
                    dstPixels[dIndex++] = norms * (1.0 - min / intensity);
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
        if (isByte) {
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
        } else {
            IHSColorSpace.convertToSigned(dstPixels, dstType);
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
        }
    }

    public WritableRaster toCIEXYZ(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        WritableRaster tempRas = this.toRGB(src, srcComponentSize, null, null);
        return IHSColorSpace.RGBToCIEXYZ(tempRas, tempRas.getSampleModel().getSampleSize(), dest, destComponentSize);
    }

    public WritableRaster toRGB(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        IHSColorSpace.checkParameters(src, srcComponentSize, dest, destComponentSize);
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
                this.toRGBByte(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 1: 
            case 2: {
                this.toRGBShort(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 3: {
                this.toRGBInt(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 4: {
                this.toRGBFloat(srcUid, srcComponentSize, dest, destComponentSize);
                break;
            }
            case 5: {
                this.toRGBDouble(srcUid, srcComponentSize, dest, destComponentSize);
            }
        }
        return dest;
    }

    private void toRGBByte(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        boolean isByte;
        byte[] iBuf = src.getByteData(0);
        byte[] hBuf = src.getByteData(1);
        byte[] sBuf = src.getByteData(2);
        double normi = 1.0 / (double)((1 << srcComponentSize[0]) - 1);
        double normh = 1.0 / (double)((1 << srcComponentSize[1]) - 1) * (Math.PI * 2);
        double norms = 1.0 / (double)((1 << srcComponentSize[2]) - 1);
        double normr = 1.0;
        double normg = 1.0;
        double normb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        boolean bl = isByte = dstType == 0;
        if (isByte) {
            this.generateTanTable();
        }
        if (dstType < 4) {
            normr = (1L << destComponentSize[0]) - 1L;
            normg = (1L << destComponentSize[1]) - 1L;
            normb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = null;
        int[] dstIntPixels = null;
        if (isByte) {
            dstIntPixels = new int[3 * height * width];
        } else {
            dstPixels = new double[3 * height * width];
        }
        int iStart = src.bandOffsets[0];
        int hStart = src.bandOffsets[1];
        int sStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int iIndex = iStart;
            int hIndex = hStart;
            int sIndex = sStart;
            while (i < width) {
                double I = (double)(iBuf[iIndex] & 0xFF) * normi;
                int h = hBuf[hIndex] & 0xFF;
                double S = (double)(sBuf[sIndex] & 0xFF) * norms;
                if (isByte) {
                    float b;
                    float g = b = (float)I;
                    float r = b;
                    if (S != 0.0) {
                        float c1;
                        if (h >= 85 && h <= 170) {
                            r = (float)((1.0 - S) * I);
                            c1 = (float)(3.0 * I - (double)r);
                            float c2 = (float)(SQRT3 * ((double)r - I) * tanTable[h]);
                            g = (c1 + c2) / 2.0f;
                            b = (c1 - c2) / 2.0f;
                        } else if (h > 170) {
                            g = (float)((1.0 - S) * I);
                            c1 = (float)(3.0 * I - (double)g);
                            float c2 = (float)(SQRT3 * ((double)g - I) * tanTable[h - 85]);
                            b = (c1 + c2) / 2.0f;
                            r = (c1 - c2) / 2.0f;
                        } else if (h < 85) {
                            b = (float)((1.0 - S) * I);
                            c1 = (float)(3.0 * I - (double)b);
                            float c2 = (float)(SQRT3 * ((double)b - I) * tanTable[h + 85]);
                            r = (c1 + c2) / 2.0f;
                            g = (c1 - c2) / 2.0f;
                        }
                    }
                    dstIntPixels[dIndex++] = (int)((double)(r < 0.0f ? 0.0f : (r > 1.0f ? 1.0f : r)) * normr + 0.5);
                    dstIntPixels[dIndex++] = (int)((double)(g < 0.0f ? 0.0f : (g > 1.0f ? 1.0f : g)) * normg + 0.5);
                    dstIntPixels[dIndex++] = (int)((double)(b < 0.0f ? 0.0f : (b > 1.0f ? 1.0f : b)) * normb + 0.5);
                } else {
                    double B;
                    double G = B = I;
                    double R = B;
                    if (S != 0.0) {
                        double c2;
                        double c1;
                        double H = (double)h * normh;
                        if (H >= 2.0943951023931953 && H <= 4.1887902047863905) {
                            R = (1.0 - S) * I;
                            c1 = 3.0 * I - R;
                            c2 = SQRT3 * (R - I) * Math.tan(H);
                            G = (c1 + c2) / 2.0;
                            B = (c1 - c2) / 2.0;
                        } else if (H > 4.1887902047863905) {
                            G = (1.0 - S) * I;
                            c1 = 3.0 * I - G;
                            c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953);
                            B = (c1 + c2) / 2.0;
                            R = (c1 - c2) / 2.0;
                        } else if (H < 2.0943951023931953) {
                            B = (1.0 - S) * I;
                            c1 = 3.0 * I - B;
                            c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905);
                            R = (c1 + c2) / 2.0;
                            G = (c1 - c2) / 2.0;
                        }
                    }
                    dstPixels[dIndex++] = (R < 0.0 ? 0.0 : (R > 1.0 ? 1.0 : R)) * normr;
                    dstPixels[dIndex++] = (G < 0.0 ? 0.0 : (G > 1.0 ? 1.0 : G)) * normg;
                    dstPixels[dIndex++] = (B < 0.0 ? 0.0 : (B > 1.0 ? 1.0 : B)) * normb;
                }
                ++i;
                iIndex += srcPixelStride;
                hIndex += srcPixelStride;
                sIndex += srcPixelStride;
            }
            ++j;
            iStart += srcLineStride;
            hStart += srcLineStride;
            sStart += srcLineStride;
        }
        if (isByte) {
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
        } else {
            IHSColorSpace.convertToSigned(dstPixels, dstType);
            dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
        }
    }

    private void toRGBShort(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        short[] iBuf = src.getShortData(0);
        short[] hBuf = src.getShortData(1);
        short[] sBuf = src.getShortData(2);
        double normi = 1.0 / (double)((1 << srcComponentSize[0]) - 1);
        double normh = 1.0 / (double)((1 << srcComponentSize[1]) - 1) * (Math.PI * 2);
        double norms = 1.0 / (double)((1 << srcComponentSize[2]) - 1);
        double normr = 1.0;
        double normg = 1.0;
        double normb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            normr = (1L << destComponentSize[0]) - 1L;
            normg = (1L << destComponentSize[1]) - 1L;
            normb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int iStart = src.bandOffsets[0];
        int hStart = src.bandOffsets[1];
        int sStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int iIndex = iStart;
            int hIndex = hStart;
            int sIndex = sStart;
            while (i < width) {
                double B;
                double I = (double)(iBuf[iIndex] & 0xFFFF) * normi;
                double H = (double)(hBuf[hIndex] & 0xFFFF) * normh;
                double S = (double)(sBuf[sIndex] & 0xFFFF) * norms;
                double G = B = I;
                double R = B;
                if (S != 0.0) {
                    double c2;
                    double c1;
                    if (H >= 2.0943951023931953 && H <= 4.1887902047863905) {
                        R = (1.0 - S) * I;
                        c1 = 3.0 * I - R;
                        c2 = SQRT3 * (R - I) * Math.tan(H);
                        G = (c1 + c2) / 2.0;
                        B = (c1 - c2) / 2.0;
                    } else if (H > 4.1887902047863905) {
                        G = (1.0 - S) * I;
                        c1 = 3.0 * I - G;
                        c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953);
                        B = (c1 + c2) / 2.0;
                        R = (c1 - c2) / 2.0;
                    } else if (H < 2.0943951023931953) {
                        B = (1.0 - S) * I;
                        c1 = 3.0 * I - B;
                        c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905);
                        R = (c1 + c2) / 2.0;
                        G = (c1 - c2) / 2.0;
                    }
                }
                dstPixels[dIndex++] = (R < 0.0 ? 0.0 : (R > 1.0 ? 1.0 : R)) * normr;
                dstPixels[dIndex++] = (G < 0.0 ? 0.0 : (G > 1.0 ? 1.0 : G)) * normg;
                dstPixels[dIndex++] = (B < 0.0 ? 0.0 : (B > 1.0 ? 1.0 : B)) * normb;
                ++i;
                iIndex += srcPixelStride;
                hIndex += srcPixelStride;
                sIndex += srcPixelStride;
            }
            ++j;
            iStart += srcLineStride;
            hStart += srcLineStride;
            sStart += srcLineStride;
        }
        IHSColorSpace.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private void toRGBInt(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        int[] iBuf = src.getIntData(0);
        int[] hBuf = src.getIntData(1);
        int[] sBuf = src.getIntData(2);
        double normi = 1.0 / (double)((1L << srcComponentSize[0]) - 1L);
        double normh = 1.0 / (double)((1L << srcComponentSize[1]) - 1L) * (Math.PI * 2);
        double norms = 1.0 / (double)((1L << srcComponentSize[2]) - 1L);
        double normr = 1.0;
        double normg = 1.0;
        double normb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            normr = (1L << destComponentSize[0]) - 1L;
            normg = (1L << destComponentSize[1]) - 1L;
            normb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int iStart = src.bandOffsets[0];
        int hStart = src.bandOffsets[1];
        int sStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int iIndex = iStart;
            int hIndex = hStart;
            int sIndex = sStart;
            while (i < width) {
                double B;
                double I = (double)((long)iBuf[iIndex] & 0xFFFFFFFFL) * normi;
                double H = (double)((long)hBuf[hIndex] & 0xFFFFFFFFL) * normh;
                double S = (double)((long)sBuf[sIndex] & 0xFFFFFFFFL) * norms;
                double G = B = I;
                double R = B;
                if (S != 0.0) {
                    double c2;
                    double c1;
                    if (H >= 2.0943951023931953 && H <= 4.1887902047863905) {
                        R = (1.0 - S) * I;
                        c1 = 3.0 * I - R;
                        c2 = SQRT3 * (R - I) * Math.tan(H);
                        G = (c1 + c2) / 2.0;
                        B = (c1 - c2) / 2.0;
                    } else if (H > 4.1887902047863905) {
                        G = (1.0 - S) * I;
                        c1 = 3.0 * I - G;
                        c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953);
                        B = (c1 + c2) / 2.0;
                        R = (c1 - c2) / 2.0;
                    } else if (H < 2.0943951023931953) {
                        B = (1.0 - S) * I;
                        c1 = 3.0 * I - B;
                        c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905);
                        R = (c1 + c2) / 2.0;
                        G = (c1 - c2) / 2.0;
                    }
                }
                dstPixels[dIndex++] = (R < 0.0 ? 0.0 : (R > 1.0 ? 1.0 : R)) * normr;
                dstPixels[dIndex++] = (G < 0.0 ? 0.0 : (G > 1.0 ? 1.0 : G)) * normg;
                dstPixels[dIndex++] = (B < 0.0 ? 0.0 : (B > 1.0 ? 1.0 : B)) * normb;
                ++i;
                iIndex += srcPixelStride;
                hIndex += srcPixelStride;
                sIndex += srcPixelStride;
            }
            ++j;
            iStart += srcLineStride;
            hStart += srcLineStride;
            sStart += srcLineStride;
        }
        IHSColorSpace.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private void toRGBFloat(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        float[] iBuf = src.getFloatData(0);
        float[] hBuf = src.getFloatData(1);
        float[] sBuf = src.getFloatData(2);
        double normr = 1.0;
        double normg = 1.0;
        double normb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            normr = (1L << destComponentSize[0]) - 1L;
            normg = (1L << destComponentSize[1]) - 1L;
            normb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int iStart = src.bandOffsets[0];
        int hStart = src.bandOffsets[1];
        int sStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int iIndex = iStart;
            int hIndex = hStart;
            int sIndex = sStart;
            while (i < width) {
                double B;
                double I = iBuf[iIndex];
                double H = hBuf[hIndex];
                double S = sBuf[sIndex];
                double G = B = I;
                double R = B;
                if (S != 0.0) {
                    double c2;
                    double c1;
                    if (H >= 2.0943951023931953 && H <= 4.1887902047863905) {
                        R = (1.0 - S) * I;
                        c1 = 3.0 * I - R;
                        c2 = SQRT3 * (R - I) * Math.tan(H);
                        G = (c1 + c2) / 2.0;
                        B = (c1 - c2) / 2.0;
                    } else if (H > 4.1887902047863905) {
                        G = (1.0 - S) * I;
                        c1 = 3.0 * I - G;
                        c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953);
                        B = (c1 + c2) / 2.0;
                        R = (c1 - c2) / 2.0;
                    } else if (H < 2.0943951023931953) {
                        B = (1.0 - S) * I;
                        c1 = 3.0 * I - B;
                        c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905);
                        R = (c1 + c2) / 2.0;
                        G = (c1 - c2) / 2.0;
                    }
                }
                dstPixels[dIndex++] = (R < 0.0 ? 0.0 : (R > 1.0 ? 1.0 : R)) * normr;
                dstPixels[dIndex++] = (G < 0.0 ? 0.0 : (G > 1.0 ? 1.0 : G)) * normg;
                dstPixels[dIndex++] = (B < 0.0 ? 0.0 : (B > 1.0 ? 1.0 : B)) * normb;
                ++i;
                iIndex += srcPixelStride;
                hIndex += srcPixelStride;
                sIndex += srcPixelStride;
            }
            ++j;
            iStart += srcLineStride;
            hStart += srcLineStride;
            sStart += srcLineStride;
        }
        IHSColorSpace.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }

    private void toRGBDouble(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize) {
        double[] iBuf = src.getDoubleData(0);
        double[] hBuf = src.getDoubleData(1);
        double[] sBuf = src.getDoubleData(2);
        double normr = 1.0;
        double normg = 1.0;
        double normb = 1.0;
        int dstType = dest.getSampleModel().getDataType();
        if (dstType < 4) {
            normr = (1L << destComponentSize[0]) - 1L;
            normg = (1L << destComponentSize[1]) - 1L;
            normb = (1L << destComponentSize[2]) - 1L;
        }
        int height = dest.getHeight();
        int width = dest.getWidth();
        double[] dstPixels = new double[3 * height * width];
        int iStart = src.bandOffsets[0];
        int hStart = src.bandOffsets[1];
        int sStart = src.bandOffsets[2];
        int srcPixelStride = src.pixelStride;
        int srcLineStride = src.lineStride;
        int dIndex = 0;
        int j = 0;
        while (j < height) {
            int i = 0;
            int iIndex = iStart;
            int hIndex = hStart;
            int sIndex = sStart;
            while (i < width) {
                double B;
                double I = iBuf[iIndex];
                double H = hBuf[hIndex];
                double S = sBuf[sIndex];
                double G = B = I;
                double R = B;
                if (S != 0.0) {
                    double c2;
                    double c1;
                    if (H >= 2.0943951023931953 && H <= 4.1887902047863905) {
                        R = (1.0 - S) * I;
                        c1 = 3.0 * I - R;
                        c2 = SQRT3 * (R - I) * Math.tan(H);
                        G = (c1 + c2) / 2.0;
                        B = (c1 - c2) / 2.0;
                    } else if (H > 4.1887902047863905) {
                        G = (1.0 - S) * I;
                        c1 = 3.0 * I - G;
                        c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953);
                        B = (c1 + c2) / 2.0;
                        R = (c1 - c2) / 2.0;
                    } else if (H < 2.0943951023931953) {
                        B = (1.0 - S) * I;
                        c1 = 3.0 * I - B;
                        c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905);
                        R = (c1 + c2) / 2.0;
                        G = (c1 - c2) / 2.0;
                    }
                }
                dstPixels[dIndex++] = (R < 0.0 ? 0.0 : (R > 1.0 ? 1.0 : R)) * normr;
                dstPixels[dIndex++] = (G < 0.0 ? 0.0 : (G > 1.0 ? 1.0 : G)) * normg;
                dstPixels[dIndex++] = (B < 0.0 ? 0.0 : (B > 1.0 ? 1.0 : B)) * normb;
                ++i;
                iIndex += srcPixelStride;
                hIndex += srcPixelStride;
                sIndex += srcPixelStride;
            }
            ++j;
            iStart += srcLineStride;
            hStart += srcLineStride;
            sStart += srcLineStride;
        }
        IHSColorSpace.convertToSigned(dstPixels, dstType);
        dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }
}

