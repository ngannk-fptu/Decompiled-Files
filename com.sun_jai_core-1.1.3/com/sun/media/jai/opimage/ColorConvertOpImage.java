/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.media.jai.ColorSpaceJAI;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterFactory;

final class ColorConvertOpImage
extends PointOpImage {
    private static final ColorSpace rgbColorSpace = ColorSpace.getInstance(1000);
    private static SoftReference softRef = null;
    private ImageParameters srcParam = null;
    private ImageParameters dstParam = null;
    private ImageParameters tempParam = null;
    private ColorConvertOp colorConvertOp = null;
    private int caseNumber;

    private static synchronized ColorConvertOp getColorConvertOp(ColorSpace src, ColorSpace dst) {
        HashMap colorConvertOpBuf = null;
        if (softRef == null || (colorConvertOpBuf = (HashMap)softRef.get()) == null) {
            colorConvertOpBuf = new HashMap();
            softRef = new SoftReference<HashMap>(colorConvertOpBuf);
        }
        ArrayList<ColorSpace> hashcode = new ArrayList<ColorSpace>(2);
        hashcode.add(0, src);
        hashcode.add(1, dst);
        ColorConvertOp op = (ColorConvertOp)colorConvertOpBuf.get(hashcode);
        if (op == null) {
            op = new ColorConvertOp(src, dst, null);
            colorConvertOpBuf.put(hashcode, op);
        }
        return op;
    }

    private static float getMinValue(int dataType) {
        float minValue = 0.0f;
        switch (dataType) {
            case 0: {
                minValue = 0.0f;
                break;
            }
            case 2: {
                minValue = -32768.0f;
                break;
            }
            case 1: {
                minValue = 0.0f;
                break;
            }
            case 3: {
                minValue = -2.14748365E9f;
                break;
            }
            default: {
                minValue = 0.0f;
            }
        }
        return minValue;
    }

    private static float getRange(int dataType) {
        float range = 1.0f;
        switch (dataType) {
            case 0: {
                range = 255.0f;
                break;
            }
            case 2: {
                range = 65535.0f;
                break;
            }
            case 1: {
                range = 65535.0f;
                break;
            }
            case 3: {
                range = 4.2949673E9f;
                break;
            }
            default: {
                range = 1.0f;
            }
        }
        return range;
    }

    public ColorConvertOpImage(RenderedImage source, Map config, ImageLayout layout, ColorModel colorModel) {
        super(source, layout, config, true);
        this.colorModel = colorModel;
        this.srcParam = new ImageParameters(source.getColorModel(), source.getSampleModel());
        this.dstParam = new ImageParameters(colorModel, this.sampleModel);
        ColorSpace srcColorSpace = this.srcParam.getColorModel().getColorSpace();
        ColorSpace dstColorSpace = this.dstParam.getColorModel().getColorSpace();
        if (srcColorSpace instanceof ColorSpaceJAI && dstColorSpace instanceof ColorSpaceJAI) {
            this.caseNumber = 1;
            this.tempParam = this.createTempParam();
        } else if (srcColorSpace instanceof ColorSpaceJAI) {
            if (dstColorSpace != rgbColorSpace) {
                this.caseNumber = 2;
                this.tempParam = this.createTempParam();
                this.colorConvertOp = ColorConvertOpImage.getColorConvertOp(rgbColorSpace, dstColorSpace);
            } else {
                this.caseNumber = 3;
            }
        } else if (dstColorSpace instanceof ColorSpaceJAI) {
            if (srcColorSpace != rgbColorSpace) {
                this.caseNumber = 4;
                this.tempParam = this.createTempParam();
                this.colorConvertOp = ColorConvertOpImage.getColorConvertOp(srcColorSpace, rgbColorSpace);
            } else {
                this.caseNumber = 5;
            }
        } else {
            this.caseNumber = 6;
            this.colorConvertOp = ColorConvertOpImage.getColorConvertOp(srcColorSpace, dstColorSpace);
        }
        this.permitInPlaceOperation();
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        WritableRaster tempRas = null;
        Raster source = sources[0];
        if (!destRect.equals(source.getBounds())) {
            source = source.createChild(destRect.x, destRect.y, destRect.width, destRect.height, destRect.x, destRect.y, null);
        }
        switch (this.caseNumber) {
            case 1: {
                tempRas = this.computeRectColorSpaceJAIToRGB(source, this.srcParam, null, this.tempParam);
                this.computeRectColorSpaceJAIFromRGB(tempRas, this.tempParam, dest, this.dstParam);
                break;
            }
            case 2: {
                tempRas = this.computeRectColorSpaceJAIToRGB(source, this.srcParam, null, this.tempParam);
                this.computeRectNonColorSpaceJAI(tempRas, this.tempParam, dest, this.dstParam, destRect);
                break;
            }
            case 3: {
                this.computeRectColorSpaceJAIToRGB(source, this.srcParam, dest, this.dstParam);
                break;
            }
            case 4: {
                tempRas = this.createTempWritableRaster(source);
                this.computeRectNonColorSpaceJAI(source, this.srcParam, tempRas, this.tempParam, destRect);
                this.computeRectColorSpaceJAIFromRGB(tempRas, this.tempParam, dest, this.dstParam);
                break;
            }
            case 5: {
                this.computeRectColorSpaceJAIFromRGB(source, this.srcParam, dest, this.dstParam);
                break;
            }
            case 6: {
                this.computeRectNonColorSpaceJAI(source, this.srcParam, dest, this.dstParam, destRect);
            }
        }
    }

    private WritableRaster computeRectColorSpaceJAIToRGB(Raster src, ImageParameters srcParam, WritableRaster dest, ImageParameters dstParam) {
        src = this.convertRasterToUnsigned(src);
        ColorSpaceJAI colorSpaceJAI = (ColorSpaceJAI)srcParam.getColorModel().getColorSpace();
        dest = colorSpaceJAI.toRGB(src, srcParam.getComponentSize(), dest, dstParam.getComponentSize());
        dest = this.convertRasterToSigned(dest);
        return dest;
    }

    private WritableRaster computeRectColorSpaceJAIFromRGB(Raster src, ImageParameters srcParam, WritableRaster dest, ImageParameters dstParam) {
        src = this.convertRasterToUnsigned(src);
        ColorSpaceJAI colorSpaceJAI = (ColorSpaceJAI)dstParam.getColorModel().getColorSpace();
        dest = colorSpaceJAI.fromRGB(src, srcParam.getComponentSize(), dest, dstParam.getComponentSize());
        dest = this.convertRasterToSigned(dest);
        return dest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void computeRectNonColorSpaceJAI(Raster src, ImageParameters srcParam, WritableRaster dest, ImageParameters dstParam, Rectangle destRect) {
        if (!srcParam.isFloat() && !dstParam.isFloat()) {
            WritableRaster d;
            Raster s = src;
            if (s.getMinX() != destRect.x || s.getMinY() != destRect.y || s.getWidth() != destRect.width || s.getHeight() != destRect.height) {
                s = s.createChild(destRect.x, destRect.y, destRect.width, destRect.height, destRect.x, destRect.y, null);
            }
            if ((d = dest).getMinX() != destRect.x || d.getMinY() != destRect.y || d.getWidth() != destRect.width || d.getHeight() != destRect.height) {
                d = d.createWritableChild(destRect.x, destRect.y, destRect.width, destRect.height, destRect.x, destRect.y, null);
            }
            Class<?> clazz = this.colorConvertOp.getClass();
            synchronized (clazz) {
                this.colorConvertOp.filter(s, d);
            }
        } else {
            ColorSpace srcColorSpace = srcParam.getColorModel().getColorSpace();
            ColorSpace dstColorSpace = dstParam.getColorModel().getColorSpace();
            boolean srcFloat = srcParam.isFloat();
            float srcMinValue = srcParam.getMinValue();
            float srcRange = srcParam.getRange();
            boolean dstFloat = dstParam.isFloat();
            float dstMinValue = dstParam.getMinValue();
            float dstRange = dstParam.getRange();
            int rectYMax = destRect.y + destRect.height;
            int rectXMax = destRect.x + destRect.width;
            int numComponents = srcColorSpace.getNumComponents();
            float[] srcPixel = new float[numComponents];
            for (int y = destRect.y; y < rectYMax; ++y) {
                for (int x = destRect.x; x < rectXMax; ++x) {
                    int i;
                    srcPixel = src.getPixel(x, y, srcPixel);
                    if (!srcFloat) {
                        for (i = 0; i < numComponents; ++i) {
                            srcPixel[i] = (srcPixel[i] - srcMinValue) / srcRange;
                        }
                    }
                    float[] xyzPixel = srcColorSpace.toCIEXYZ(srcPixel);
                    float[] dstPixel = dstColorSpace.fromCIEXYZ(xyzPixel);
                    if (!dstFloat) {
                        for (i = 0; i < numComponents; ++i) {
                            dstPixel[i] = dstPixel[i] * dstRange + dstMinValue;
                        }
                    }
                    dest.setPixel(x, y, dstPixel);
                }
            }
        }
    }

    private ImageParameters createTempParam() {
        ColorModel cm = null;
        SampleModel sm = null;
        if (this.srcParam.getDataType() > this.dstParam.getDataType()) {
            cm = this.srcParam.getColorModel();
            sm = this.srcParam.getSampleModel();
        } else {
            cm = this.dstParam.getColorModel();
            sm = this.dstParam.getSampleModel();
        }
        cm = new ComponentColorModel(rgbColorSpace, cm.getComponentSize(), cm.hasAlpha(), cm.isAlphaPremultiplied(), cm.getTransparency(), sm.getDataType());
        return new ImageParameters(cm, sm);
    }

    private WritableRaster createTempWritableRaster(Raster src) {
        Point origin = new Point(src.getMinX(), src.getMinY());
        return RasterFactory.createWritableRaster(src.getSampleModel(), origin);
    }

    private Raster convertRasterToUnsigned(Raster ras) {
        int type = ras.getSampleModel().getDataType();
        WritableRaster tempRas = null;
        if (type == 3 || type == 2) {
            int minX = ras.getMinX();
            int minY = ras.getMinY();
            int w = ras.getWidth();
            int h = ras.getHeight();
            int[] buf = ras.getPixels(minX, minY, w, h, (int[])null);
            this.convertBufferToUnsigned(buf, type);
            tempRas = this.createTempWritableRaster(ras);
            tempRas.setPixels(minX, minY, w, h, buf);
            return tempRas;
        }
        return ras;
    }

    private WritableRaster convertRasterToSigned(WritableRaster ras) {
        int type = ras.getSampleModel().getDataType();
        WritableRaster tempRas = null;
        if (type == 3 || type == 2) {
            int minX = ras.getMinX();
            int minY = ras.getMinY();
            int w = ras.getWidth();
            int h = ras.getHeight();
            int[] buf = ras.getPixels(minX, minY, w, h, (int[])null);
            this.convertBufferToSigned(buf, type);
            tempRas = ras instanceof WritableRaster ? ras : this.createTempWritableRaster(ras);
            tempRas.setPixels(minX, minY, w, h, buf);
            return tempRas;
        }
        return ras;
    }

    private void convertBufferToSigned(int[] buf, int type) {
        block4: {
            block3: {
                if (buf == null) {
                    return;
                }
                if (type != 2) break block3;
                int i = 0;
                while (i < buf.length) {
                    int n = i++;
                    buf[n] = buf[n] + Short.MIN_VALUE;
                }
                break block4;
            }
            if (type != 3) break block4;
            for (int i = 0; i < buf.length; ++i) {
                buf[i] = (int)(((long)buf[i] & 0xFFFFFFFFL) + Integer.MIN_VALUE);
            }
        }
    }

    private void convertBufferToUnsigned(int[] buf, int type) {
        block4: {
            block3: {
                if (buf == null) {
                    return;
                }
                if (type != 2) break block3;
                int i = 0;
                while (i < buf.length) {
                    int n = i++;
                    buf[n] = buf[n] - Short.MIN_VALUE;
                }
                break block4;
            }
            if (type != 3) break block4;
            for (int i = 0; i < buf.length; ++i) {
                buf[i] = (int)(((long)buf[i] & 0xFFFFFFFFL) - Integer.MIN_VALUE);
            }
        }
    }

    private final class ImageParameters {
        private boolean isFloat;
        private ColorModel colorModel;
        private SampleModel sampleModel;
        private float minValue;
        private float range;
        private int[] componentSize;
        private int dataType;

        ImageParameters(ColorModel cm, SampleModel sm) {
            this.colorModel = cm;
            this.sampleModel = sm;
            this.dataType = sm.getDataType();
            this.isFloat = this.dataType == 4 || this.dataType == 5;
            this.minValue = ColorConvertOpImage.getMinValue(this.dataType);
            this.range = ColorConvertOpImage.getRange(this.dataType);
            this.componentSize = cm.getComponentSize();
        }

        public boolean isFloat() {
            return this.isFloat;
        }

        public ColorModel getColorModel() {
            return this.colorModel;
        }

        public SampleModel getSampleModel() {
            return this.sampleModel;
        }

        public float getMinValue() {
            return this.minValue;
        }

        public float getRange() {
            return this.range;
        }

        public int[] getComponentSize() {
            return this.componentSize;
        }

        public int getDataType() {
            return this.dataType;
        }
    }
}

