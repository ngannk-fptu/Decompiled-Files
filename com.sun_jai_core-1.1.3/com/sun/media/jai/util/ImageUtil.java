/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.JaiI18N;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.DeferredData;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.UnpackedImageData;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public final class ImageUtil {
    private static final float FLOAT_MIN = -3.4028235E38f;
    private static long counter;
    public static final int BYTE_MASK = 255;
    public static final int USHORT_MASK = 65535;
    static /* synthetic */ Class class$java$awt$image$ColorModel;
    static /* synthetic */ Class class$java$awt$image$SampleModel;
    static /* synthetic */ Class class$com$sun$media$jai$util$ImageUtil;

    public static final byte clampByte(int in) {
        return in > 255 ? (byte)-1 : (in >= 0 ? (byte)in : (byte)0);
    }

    public static final short clampUShort(int in) {
        return in > 65535 ? (short)-1 : (in >= 0 ? (short)in : (short)0);
    }

    public static final short clampShort(int in) {
        return (short)(in > Short.MAX_VALUE ? Short.MAX_VALUE : (in >= Short.MIN_VALUE ? (int)in : Short.MIN_VALUE));
    }

    public static final int clampInt(long in) {
        return in > Integer.MAX_VALUE ? Integer.MAX_VALUE : (in >= Integer.MIN_VALUE ? (int)in : Integer.MIN_VALUE);
    }

    public static final float clampFloat(double in) {
        return in > 3.4028234663852886E38 ? Float.MAX_VALUE : (in >= -3.4028234663852886E38 ? (float)in : -3.4028235E38f);
    }

    public static final byte clampRoundByte(float in) {
        return in > 255.0f ? (byte)-1 : (in >= 0.0f ? (byte)(in + 0.5f) : (byte)0);
    }

    public static final byte clampRoundByte(double in) {
        return in > 255.0 ? (byte)-1 : (in >= 0.0 ? (byte)(in + 0.5) : (byte)0);
    }

    public static final short clampRoundUShort(float in) {
        return in > 65535.0f ? (short)-1 : (in >= 0.0f ? (short)(in + 0.5f) : (short)0);
    }

    public static final short clampRoundUShort(double in) {
        return in > 65535.0 ? (short)-1 : (in >= 0.0 ? (short)(in + 0.5) : (short)0);
    }

    public static final short clampRoundShort(float in) {
        return (short)(in > 32767.0f ? Short.MAX_VALUE : (in >= -32768.0f ? (int)((int)Math.floor(in + 0.5f)) : Short.MIN_VALUE));
    }

    public static final short clampRoundShort(double in) {
        return (short)(in > 32767.0 ? Short.MAX_VALUE : (in >= -32768.0 ? (int)((int)Math.floor(in + 0.5)) : Short.MIN_VALUE));
    }

    public static final int clampRoundInt(float in) {
        return in > 2.14748365E9f ? Integer.MAX_VALUE : (in >= -2.14748365E9f ? (int)Math.floor(in + 0.5f) : Integer.MIN_VALUE);
    }

    public static final int clampRoundInt(double in) {
        return in > 2.147483647E9 ? Integer.MAX_VALUE : (in >= -2.147483648E9 ? (int)Math.floor(in + 0.5) : Integer.MIN_VALUE);
    }

    public static final byte clampBytePositive(int in) {
        return (byte)(in > 255 ? -1 : (byte)in);
    }

    public static final byte clampByteNegative(int in) {
        return in < 0 ? (byte)0 : (byte)in;
    }

    public static final short clampUShortPositive(int in) {
        return (short)(in > 65535 ? -1 : (short)in);
    }

    public static final short clampUShortNegative(int in) {
        return in < 0 ? (short)0 : (short)in;
    }

    public static final void copyRaster(RasterAccessor src, RasterAccessor dst) {
        int srcPixelStride = src.getPixelStride();
        int srcLineStride = src.getScanlineStride();
        int[] srcBandOffsets = src.getBandOffsets();
        int dstPixelStride = dst.getPixelStride();
        int dstLineStride = dst.getScanlineStride();
        int[] dstBandOffsets = dst.getBandOffsets();
        int width = dst.getWidth() * dstPixelStride;
        int height = dst.getHeight() * dstLineStride;
        int bands = dst.getNumBands();
        switch (dst.getDataType()) {
            case 0: {
                byte[][] bSrcData = src.getByteDataArrays();
                byte[][] bDstData = dst.getByteDataArrays();
                for (int b = 0; b < bands; ++b) {
                    byte[] s = bSrcData[b];
                    byte[] d = bDstData[b];
                    int heightEnd = dstBandOffsets[b] + height;
                    int dstLineOffset = dstBandOffsets[b];
                    int srcLineOffset = srcBandOffsets[b];
                    while (dstLineOffset < heightEnd) {
                        int widthEnd = dstLineOffset + width;
                        int dstPixelOffset = dstLineOffset;
                        int srcPixelOffset = srcLineOffset;
                        while (dstPixelOffset < widthEnd) {
                            d[dstPixelOffset] = s[srcPixelOffset];
                            dstPixelOffset += dstPixelStride;
                            srcPixelOffset += srcPixelStride;
                        }
                        dstLineOffset += dstLineStride;
                        srcLineOffset += srcLineStride;
                    }
                }
                break;
            }
            case 1: 
            case 2: {
                short[][] sSrcData = src.getShortDataArrays();
                short[][] sDstData = dst.getShortDataArrays();
                for (int b = 0; b < bands; ++b) {
                    short[] s = sSrcData[b];
                    short[] d = sDstData[b];
                    int heightEnd = dstBandOffsets[b] + height;
                    int dstLineOffset = dstBandOffsets[b];
                    int srcLineOffset = srcBandOffsets[b];
                    while (dstLineOffset < heightEnd) {
                        int widthEnd = dstLineOffset + width;
                        int dstPixelOffset = dstLineOffset;
                        int srcPixelOffset = srcLineOffset;
                        while (dstPixelOffset < widthEnd) {
                            d[dstPixelOffset] = s[srcPixelOffset];
                            dstPixelOffset += dstPixelStride;
                            srcPixelOffset += srcPixelStride;
                        }
                        dstLineOffset += dstLineStride;
                        srcLineOffset += srcLineStride;
                    }
                }
                break;
            }
            case 3: {
                int[][] iSrcData = src.getIntDataArrays();
                int[][] iDstData = dst.getIntDataArrays();
                for (int b = 0; b < bands; ++b) {
                    int[] s = iSrcData[b];
                    int[] d = iDstData[b];
                    int heightEnd = dstBandOffsets[b] + height;
                    int dstLineOffset = dstBandOffsets[b];
                    int srcLineOffset = srcBandOffsets[b];
                    while (dstLineOffset < heightEnd) {
                        int widthEnd = dstLineOffset + width;
                        int dstPixelOffset = dstLineOffset;
                        int srcPixelOffset = srcLineOffset;
                        while (dstPixelOffset < widthEnd) {
                            d[dstPixelOffset] = s[srcPixelOffset];
                            dstPixelOffset += dstPixelStride;
                            srcPixelOffset += srcPixelStride;
                        }
                        dstLineOffset += dstLineStride;
                        srcLineOffset += srcLineStride;
                    }
                }
                break;
            }
            case 4: {
                float[][] fSrcData = src.getFloatDataArrays();
                float[][] fDstData = dst.getFloatDataArrays();
                for (int b = 0; b < bands; ++b) {
                    float[] s = fSrcData[b];
                    float[] d = fDstData[b];
                    int heightEnd = dstBandOffsets[b] + height;
                    int dstLineOffset = dstBandOffsets[b];
                    int srcLineOffset = srcBandOffsets[b];
                    while (dstLineOffset < heightEnd) {
                        int widthEnd = dstLineOffset + width;
                        int dstPixelOffset = dstLineOffset;
                        int srcPixelOffset = srcLineOffset;
                        while (dstPixelOffset < widthEnd) {
                            d[dstPixelOffset] = s[srcPixelOffset];
                            dstPixelOffset += dstPixelStride;
                            srcPixelOffset += srcPixelStride;
                        }
                        dstLineOffset += dstLineStride;
                        srcLineOffset += srcLineStride;
                    }
                }
                break;
            }
            case 5: {
                double[][] dSrcData = src.getDoubleDataArrays();
                double[][] dDstData = dst.getDoubleDataArrays();
                for (int b = 0; b < bands; ++b) {
                    double[] s = dSrcData[b];
                    double[] d = dDstData[b];
                    int heightEnd = dstBandOffsets[b] + height;
                    int dstLineOffset = dstBandOffsets[b];
                    int srcLineOffset = srcBandOffsets[b];
                    while (dstLineOffset < heightEnd) {
                        int widthEnd = dstLineOffset + width;
                        int dstPixelOffset = dstLineOffset;
                        int srcPixelOffset = srcLineOffset;
                        while (dstPixelOffset < widthEnd) {
                            d[dstPixelOffset] = s[srcPixelOffset];
                            dstPixelOffset += dstPixelStride;
                            srcPixelOffset += srcPixelStride;
                        }
                        dstLineOffset += dstLineStride;
                        srcLineOffset += srcLineStride;
                    }
                }
                break;
            }
        }
        if (dst.isDataCopy()) {
            dst.clampDataArrays();
            dst.copyDataToRaster();
        }
    }

    public boolean areEqualSampleModels(SampleModel sm1, SampleModel sm2) {
        if (sm1 == sm2) {
            return true;
        }
        if (sm1.getClass() == sm2.getClass() && sm1.getDataType() == sm2.getDataType() && sm1.getTransferType() == sm2.getTransferType() && sm1.getWidth() == sm2.getWidth() && sm1.getHeight() == sm2.getHeight()) {
            if (sm1 instanceof ComponentSampleModel) {
                ComponentSampleModel csm1 = (ComponentSampleModel)sm1;
                ComponentSampleModel csm2 = (ComponentSampleModel)sm2;
                return csm1.getPixelStride() == csm2.getPixelStride() && csm1.getScanlineStride() == csm2.getScanlineStride() && Arrays.equals(csm1.getBankIndices(), csm2.getBankIndices()) && Arrays.equals(csm1.getBandOffsets(), csm2.getBandOffsets());
            }
            if (sm1 instanceof MultiPixelPackedSampleModel) {
                MultiPixelPackedSampleModel mpp1 = (MultiPixelPackedSampleModel)sm1;
                MultiPixelPackedSampleModel mpp2 = (MultiPixelPackedSampleModel)sm2;
                return mpp1.getPixelBitStride() == mpp2.getPixelBitStride() && mpp1.getScanlineStride() == mpp2.getScanlineStride() && mpp1.getDataBitOffset() == mpp2.getDataBitOffset();
            }
            if (sm1 instanceof SinglePixelPackedSampleModel) {
                SinglePixelPackedSampleModel spp1 = (SinglePixelPackedSampleModel)sm1;
                SinglePixelPackedSampleModel spp2 = (SinglePixelPackedSampleModel)sm2;
                return spp1.getScanlineStride() == spp2.getScanlineStride() && Arrays.equals(spp1.getBitMasks(), spp2.getBitMasks());
            }
        }
        return false;
    }

    public static boolean isBinary(SampleModel sm) {
        return sm instanceof MultiPixelPackedSampleModel && ((MultiPixelPackedSampleModel)sm).getPixelBitStride() == 1 && sm.getNumBands() == 1;
    }

    public static byte[] getPackedBinaryData(Raster raster, Rectangle rect) {
        byte[] binaryDataArray;
        block29: {
            int numBytesPerRow;
            int bitOffset;
            int eltOffset;
            int lineStride;
            DataBuffer dataBuffer;
            int rectHeight;
            int rectWidth;
            block27: {
                block30: {
                    block28: {
                        SampleModel sm = raster.getSampleModel();
                        if (!ImageUtil.isBinary(sm)) {
                            throw new IllegalArgumentException(JaiI18N.getString("ImageUtil0"));
                        }
                        int rectX = rect.x;
                        int rectY = rect.y;
                        rectWidth = rect.width;
                        rectHeight = rect.height;
                        dataBuffer = raster.getDataBuffer();
                        int dx = rectX - raster.getSampleModelTranslateX();
                        int dy = rectY - raster.getSampleModelTranslateY();
                        MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
                        lineStride = mpp.getScanlineStride();
                        eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
                        bitOffset = mpp.getBitOffset(dx);
                        numBytesPerRow = (rectWidth + 7) / 8;
                        if (dataBuffer instanceof DataBufferByte && eltOffset == 0 && bitOffset == 0 && numBytesPerRow == lineStride && ((DataBufferByte)dataBuffer).getData().length == numBytesPerRow * rectHeight) {
                            return ((DataBufferByte)dataBuffer).getData();
                        }
                        binaryDataArray = new byte[numBytesPerRow * rectHeight];
                        int b = 0;
                        if (bitOffset != 0) break block27;
                        if (!(dataBuffer instanceof DataBufferByte)) break block28;
                        byte[] data = ((DataBufferByte)dataBuffer).getData();
                        int stride = numBytesPerRow;
                        int offset = 0;
                        for (int y = 0; y < rectHeight; ++y) {
                            System.arraycopy(data, eltOffset, binaryDataArray, offset, stride);
                            offset += stride;
                            eltOffset += lineStride;
                        }
                        break block29;
                    }
                    if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) break block30;
                    short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                    for (int y = 0; y < rectHeight; ++y) {
                        int xRemaining;
                        int i = eltOffset;
                        for (xRemaining = rectWidth; xRemaining > 8; xRemaining -= 16) {
                            short datum = data[i++];
                            binaryDataArray[b++] = (byte)(datum >>> 8 & 0xFF);
                            binaryDataArray[b++] = (byte)(datum & 0xFF);
                        }
                        if (xRemaining > 0) {
                            binaryDataArray[b++] = (byte)(data[i] >>> 8 & 0xFF);
                        }
                        eltOffset += lineStride;
                    }
                    break block29;
                }
                if (!(dataBuffer instanceof DataBufferInt)) break block29;
                int[] data = ((DataBufferInt)dataBuffer).getData();
                for (int y = 0; y < rectHeight; ++y) {
                    int xRemaining;
                    int i = eltOffset;
                    for (xRemaining = rectWidth; xRemaining > 24; xRemaining -= 32) {
                        int datum = data[i++];
                        binaryDataArray[b++] = (byte)(datum >>> 24 & 0xFF);
                        binaryDataArray[b++] = (byte)(datum >>> 16 & 0xFF);
                        binaryDataArray[b++] = (byte)(datum >>> 8 & 0xFF);
                        binaryDataArray[b++] = (byte)(datum & 0xFF);
                    }
                    int shift = 24;
                    while (xRemaining > 0) {
                        binaryDataArray[b++] = (byte)(data[i] >>> shift & 0xFF);
                        shift -= 8;
                        xRemaining -= 8;
                    }
                    eltOffset += lineStride;
                }
                break block29;
            }
            if (dataBuffer instanceof DataBufferByte) {
                byte[] data = ((DataBufferByte)dataBuffer).getData();
                if ((bitOffset & 7) == 0) {
                    int stride = numBytesPerRow;
                    int offset = 0;
                    for (int y = 0; y < rectHeight; ++y) {
                        System.arraycopy(data, eltOffset, binaryDataArray, offset, stride);
                        offset += stride;
                        eltOffset += lineStride;
                    }
                } else {
                    int leftShift = bitOffset & 7;
                    int rightShift = 8 - leftShift;
                    for (int y = 0; y < rectHeight; ++y) {
                        int i = eltOffset;
                        for (int xRemaining = rectWidth; xRemaining > 0; xRemaining -= 8) {
                            binaryDataArray[b++] = xRemaining > rightShift ? (byte)((data[i++] & 0xFF) << leftShift | (data[i] & 0xFF) >>> rightShift) : (byte)((data[i] & 0xFF) << leftShift);
                        }
                        eltOffset += lineStride;
                    }
                }
            } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
                short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                for (int y = 0; y < rectHeight; ++y) {
                    int bOffset = bitOffset;
                    int x = 0;
                    while (x < rectWidth) {
                        int i = eltOffset + bOffset / 16;
                        int mod = bOffset % 16;
                        int left = data[i] & 0xFFFF;
                        if (mod <= 8) {
                            binaryDataArray[b++] = (byte)(left >>> 8 - mod);
                        } else {
                            int delta = mod - 8;
                            int right = data[i + 1] & 0xFFFF;
                            binaryDataArray[b++] = (byte)(left << delta | right >>> 16 - delta);
                        }
                        x += 8;
                        bOffset += 8;
                    }
                    eltOffset += lineStride;
                }
            } else if (dataBuffer instanceof DataBufferInt) {
                int[] data = ((DataBufferInt)dataBuffer).getData();
                for (int y = 0; y < rectHeight; ++y) {
                    int bOffset = bitOffset;
                    int x = 0;
                    while (x < rectWidth) {
                        int i = eltOffset + bOffset / 32;
                        int mod = bOffset % 32;
                        int left = data[i];
                        if (mod <= 24) {
                            binaryDataArray[b++] = (byte)(left >>> 24 - mod);
                        } else {
                            int delta = mod - 24;
                            int right = data[i + 1];
                            binaryDataArray[b++] = (byte)(left << delta | right >>> 32 - delta);
                        }
                        x += 8;
                        bOffset += 8;
                    }
                    eltOffset += lineStride;
                }
            }
        }
        return binaryDataArray;
    }

    public static byte[] getUnpackedBinaryData(Raster raster, Rectangle rect) {
        byte[] bdata;
        block8: {
            int maxX;
            int maxY;
            int bitOffset;
            int eltOffset;
            int lineStride;
            DataBuffer dataBuffer;
            block9: {
                block7: {
                    SampleModel sm = raster.getSampleModel();
                    if (!ImageUtil.isBinary(sm)) {
                        throw new IllegalArgumentException(JaiI18N.getString("ImageUtil0"));
                    }
                    int rectX = rect.x;
                    int rectY = rect.y;
                    int rectWidth = rect.width;
                    int rectHeight = rect.height;
                    dataBuffer = raster.getDataBuffer();
                    int dx = rectX - raster.getSampleModelTranslateX();
                    int dy = rectY - raster.getSampleModelTranslateY();
                    MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
                    lineStride = mpp.getScanlineStride();
                    eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
                    bitOffset = mpp.getBitOffset(dx);
                    bdata = new byte[rectWidth * rectHeight];
                    maxY = rectY + rectHeight;
                    maxX = rectX + rectWidth;
                    int k = 0;
                    if (!(dataBuffer instanceof DataBufferByte)) break block7;
                    byte[] data = ((DataBufferByte)dataBuffer).getData();
                    for (int y = rectY; y < maxY; ++y) {
                        int bOffset = eltOffset * 8 + bitOffset;
                        for (int x = rectX; x < maxX; ++x) {
                            byte b = data[bOffset / 8];
                            bdata[k++] = (byte)(b >>> (7 - bOffset & 7) & 1);
                            ++bOffset;
                        }
                        eltOffset += lineStride;
                    }
                    break block8;
                }
                if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) break block9;
                short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                for (int y = rectY; y < maxY; ++y) {
                    int bOffset = eltOffset * 16 + bitOffset;
                    for (int x = rectX; x < maxX; ++x) {
                        short s = data[bOffset / 16];
                        bdata[k++] = (byte)(s >>> 15 - bOffset % 16 & 1);
                        ++bOffset;
                    }
                    eltOffset += lineStride;
                }
                break block8;
            }
            if (!(dataBuffer instanceof DataBufferInt)) break block8;
            int[] data = ((DataBufferInt)dataBuffer).getData();
            for (int y = rectY; y < maxY; ++y) {
                int bOffset = eltOffset * 32 + bitOffset;
                for (int x = rectX; x < maxX; ++x) {
                    int i = data[bOffset / 32];
                    bdata[k++] = (byte)(i >>> 31 - bOffset % 32 & 1);
                    ++bOffset;
                }
                eltOffset += lineStride;
            }
        }
        return bdata;
    }

    public static void setPackedBinaryData(byte[] binaryDataArray, WritableRaster raster, Rectangle rect) {
        block41: {
            int b;
            int bitOffset;
            int eltOffset;
            int lineStride;
            DataBuffer dataBuffer;
            int rectHeight;
            int rectWidth;
            block39: {
                block42: {
                    block40: {
                        SampleModel sm = raster.getSampleModel();
                        if (!ImageUtil.isBinary(sm)) {
                            throw new IllegalArgumentException(JaiI18N.getString("ImageUtil0"));
                        }
                        int rectX = rect.x;
                        int rectY = rect.y;
                        rectWidth = rect.width;
                        rectHeight = rect.height;
                        dataBuffer = raster.getDataBuffer();
                        int dx = rectX - raster.getSampleModelTranslateX();
                        int dy = rectY - raster.getSampleModelTranslateY();
                        MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
                        lineStride = mpp.getScanlineStride();
                        eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
                        bitOffset = mpp.getBitOffset(dx);
                        b = 0;
                        if (bitOffset != 0) break block39;
                        if (!(dataBuffer instanceof DataBufferByte)) break block40;
                        byte[] data = ((DataBufferByte)dataBuffer).getData();
                        if (data == binaryDataArray) {
                            return;
                        }
                        int stride = (rectWidth + 7) / 8;
                        int offset = 0;
                        for (int y = 0; y < rectHeight; ++y) {
                            System.arraycopy(binaryDataArray, offset, data, eltOffset, stride);
                            offset += stride;
                            eltOffset += lineStride;
                        }
                        break block41;
                    }
                    if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) break block42;
                    short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                    for (int y = 0; y < rectHeight; ++y) {
                        int xRemaining;
                        int i = eltOffset;
                        for (xRemaining = rectWidth; xRemaining > 8; xRemaining -= 16) {
                            data[i++] = (short)((binaryDataArray[b++] & 0xFF) << 8 | binaryDataArray[b++] & 0xFF);
                        }
                        if (xRemaining > 0) {
                            data[i++] = (short)((binaryDataArray[b++] & 0xFF) << 8);
                        }
                        eltOffset += lineStride;
                    }
                    break block41;
                }
                if (!(dataBuffer instanceof DataBufferInt)) break block41;
                int[] data = ((DataBufferInt)dataBuffer).getData();
                for (int y = 0; y < rectHeight; ++y) {
                    int xRemaining;
                    int i = eltOffset;
                    for (xRemaining = rectWidth; xRemaining > 24; xRemaining -= 32) {
                        data[i++] = (binaryDataArray[b++] & 0xFF) << 24 | (binaryDataArray[b++] & 0xFF) << 16 | (binaryDataArray[b++] & 0xFF) << 8 | binaryDataArray[b++] & 0xFF;
                    }
                    int shift = 24;
                    while (xRemaining > 0) {
                        int n = i;
                        data[n] = data[n] | (binaryDataArray[b++] & 0xFF) << shift;
                        shift -= 8;
                        xRemaining -= 8;
                    }
                    eltOffset += lineStride;
                }
                break block41;
            }
            int stride = (rectWidth + 7) / 8;
            int offset = 0;
            if (dataBuffer instanceof DataBufferByte) {
                byte[] data = ((DataBufferByte)dataBuffer).getData();
                if ((bitOffset & 7) == 0) {
                    for (int y = 0; y < rectHeight; ++y) {
                        System.arraycopy(binaryDataArray, offset, data, eltOffset, stride);
                        offset += stride;
                        eltOffset += lineStride;
                    }
                } else {
                    int rightShift = bitOffset & 7;
                    int leftShift = 8 - rightShift;
                    int leftShift8 = 8 + leftShift;
                    byte mask = (byte)(255 << leftShift);
                    byte mask1 = ~mask;
                    for (int y = 0; y < rectHeight; ++y) {
                        int i = eltOffset;
                        for (int xRemaining = rectWidth; xRemaining > 0; xRemaining -= 8) {
                            byte datum = binaryDataArray[b++];
                            if (xRemaining > leftShift8) {
                                data[i] = (byte)(data[i] & mask | (datum & 0xFF) >>> rightShift);
                                data[++i] = (byte)((datum & 0xFF) << leftShift);
                                continue;
                            }
                            if (xRemaining > leftShift) {
                                data[i] = (byte)(data[i] & mask | (datum & 0xFF) >>> rightShift);
                                data[++i] = (byte)(data[i] & mask1 | (datum & 0xFF) << leftShift);
                                continue;
                            }
                            int remainMask = (1 << leftShift - xRemaining) - 1;
                            data[i] = (byte)(data[i] & (mask | remainMask) | (datum & 0xFF) >>> rightShift & ~remainMask);
                        }
                        eltOffset += lineStride;
                    }
                }
            } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
                short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                int rightShift = bitOffset & 7;
                int leftShift = 8 - rightShift;
                int leftShift16 = 16 + leftShift;
                short mask = (short)(~(255 << leftShift));
                short mask1 = (short)(65535 << leftShift);
                short mask2 = ~mask1;
                for (int y = 0; y < rectHeight; ++y) {
                    int bOffset = bitOffset;
                    int xRemaining = rectWidth;
                    int x = 0;
                    while (x < rectWidth) {
                        int i = eltOffset + (bOffset >> 4);
                        int mod = bOffset & 0xF;
                        int datum = binaryDataArray[b++] & 0xFF;
                        if (mod <= 8) {
                            if (xRemaining < 8) {
                                datum &= 255 << 8 - xRemaining;
                            }
                            data[i] = (short)(data[i] & mask | datum << leftShift);
                        } else if (xRemaining > leftShift16) {
                            data[i] = (short)(data[i] & mask1 | datum >>> rightShift & 0xFFFF);
                            data[++i] = (short)(datum << leftShift & 0xFFFF);
                        } else if (xRemaining > leftShift) {
                            data[i] = (short)(data[i] & mask1 | datum >>> rightShift & 0xFFFF);
                            data[++i] = (short)(data[i] & mask2 | datum << leftShift & 0xFFFF);
                        } else {
                            int remainMask = (1 << leftShift - xRemaining) - 1;
                            data[i] = (short)(data[i] & (mask1 | remainMask) | datum >>> rightShift & 0xFFFF & ~remainMask);
                        }
                        x += 8;
                        bOffset += 8;
                        xRemaining -= 8;
                    }
                    eltOffset += lineStride;
                }
            } else if (dataBuffer instanceof DataBufferInt) {
                int[] data = ((DataBufferInt)dataBuffer).getData();
                int rightShift = bitOffset & 7;
                int leftShift = 8 - rightShift;
                int leftShift32 = 32 + leftShift;
                int mask = -1 << leftShift;
                int mask1 = ~mask;
                for (int y = 0; y < rectHeight; ++y) {
                    int bOffset = bitOffset;
                    int xRemaining = rectWidth;
                    int x = 0;
                    while (x < rectWidth) {
                        int i = eltOffset + (bOffset >> 5);
                        int mod = bOffset & 0x1F;
                        int datum = binaryDataArray[b++] & 0xFF;
                        if (mod <= 24) {
                            int shift = 24 - mod;
                            if (xRemaining < 8) {
                                datum &= 255 << 8 - xRemaining;
                            }
                            data[i] = data[i] & ~(255 << shift) | datum << shift;
                        } else if (xRemaining > leftShift32) {
                            data[i] = data[i] & mask | datum >>> rightShift;
                            data[++i] = datum << leftShift;
                        } else if (xRemaining > leftShift) {
                            data[i] = data[i] & mask | datum >>> rightShift;
                            data[++i] = data[i] & mask1 | datum << leftShift;
                        } else {
                            int remainMask = (1 << leftShift - xRemaining) - 1;
                            data[i] = data[i] & (mask | remainMask) | datum >>> rightShift & ~remainMask;
                        }
                        x += 8;
                        bOffset += 8;
                        xRemaining -= 8;
                    }
                    eltOffset += lineStride;
                }
            }
        }
    }

    public static void setUnpackedBinaryData(byte[] bdata, WritableRaster raster, Rectangle rect) {
        block11: {
            int k;
            int bitOffset;
            int eltOffset;
            int lineStride;
            DataBuffer dataBuffer;
            int rectHeight;
            int rectWidth;
            block12: {
                block10: {
                    SampleModel sm = raster.getSampleModel();
                    if (!ImageUtil.isBinary(sm)) {
                        throw new IllegalArgumentException(JaiI18N.getString("ImageUtil0"));
                    }
                    int rectX = rect.x;
                    int rectY = rect.y;
                    rectWidth = rect.width;
                    rectHeight = rect.height;
                    dataBuffer = raster.getDataBuffer();
                    int dx = rectX - raster.getSampleModelTranslateX();
                    int dy = rectY - raster.getSampleModelTranslateY();
                    MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
                    lineStride = mpp.getScanlineStride();
                    eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
                    bitOffset = mpp.getBitOffset(dx);
                    k = 0;
                    if (!(dataBuffer instanceof DataBufferByte)) break block10;
                    byte[] data = ((DataBufferByte)dataBuffer).getData();
                    for (int y = 0; y < rectHeight; ++y) {
                        int bOffset = eltOffset * 8 + bitOffset;
                        for (int x = 0; x < rectWidth; ++x) {
                            if (bdata[k++] != 0) {
                                int n = bOffset / 8;
                                data[n] = (byte)(data[n] | (byte)(1 << (7 - bOffset & 7)));
                            }
                            ++bOffset;
                        }
                        eltOffset += lineStride;
                    }
                    break block11;
                }
                if (!(dataBuffer instanceof DataBufferShort) && !(dataBuffer instanceof DataBufferUShort)) break block12;
                short[] data = dataBuffer instanceof DataBufferShort ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                for (int y = 0; y < rectHeight; ++y) {
                    int bOffset = eltOffset * 16 + bitOffset;
                    for (int x = 0; x < rectWidth; ++x) {
                        if (bdata[k++] != 0) {
                            int n = bOffset / 16;
                            data[n] = (short)(data[n] | (short)(1 << 15 - bOffset % 16));
                        }
                        ++bOffset;
                    }
                    eltOffset += lineStride;
                }
                break block11;
            }
            if (!(dataBuffer instanceof DataBufferInt)) break block11;
            int[] data = ((DataBufferInt)dataBuffer).getData();
            for (int y = 0; y < rectHeight; ++y) {
                int bOffset = eltOffset * 32 + bitOffset;
                for (int x = 0; x < rectWidth; ++x) {
                    if (bdata[k++] != 0) {
                        int n = bOffset / 32;
                        data[n] = data[n] | 1 << 31 - bOffset % 32;
                    }
                    ++bOffset;
                }
                eltOffset += lineStride;
            }
        }
    }

    public static void fillBackground(WritableRaster raster, Rectangle rect, double[] backgroundValues) {
        block44: {
            PixelAccessor accessor;
            block43: {
                rect = rect.intersection(raster.getBounds());
                int numBands = raster.getSampleModel().getNumBands();
                SampleModel sm = raster.getSampleModel();
                accessor = new PixelAccessor(sm, null);
                if (!ImageUtil.isBinary(sm)) break block43;
                byte value = (byte)((int)backgroundValues[0] & 1);
                if (value == 0) {
                    return;
                }
                int rectX = rect.x;
                int rectY = rect.y;
                int rectWidth = rect.width;
                int rectHeight = rect.height;
                int dx = rectX - raster.getSampleModelTranslateX();
                int dy = rectY - raster.getSampleModelTranslateY();
                DataBuffer dataBuffer = raster.getDataBuffer();
                MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)sm;
                int lineStride = mpp.getScanlineStride();
                int eltOffset = dataBuffer.getOffset() + mpp.getOffset(dx, dy);
                int bitOffset = mpp.getBitOffset(dx);
                switch (sm.getDataType()) {
                    case 0: {
                        byte[] data = ((DataBufferByte)dataBuffer).getData();
                        int bits = bitOffset & 7;
                        int otherBits = bits == 0 ? 0 : 8 - bits;
                        byte mask = (byte)(255 >> bits);
                        int lineLength = (rectWidth - otherBits) / 8;
                        int bits1 = rectWidth - otherBits & 7;
                        byte mask1 = (byte)(255 << 8 - bits1);
                        if (lineLength == 0) {
                            mask = (byte)(mask & mask1);
                            bits1 = 0;
                        }
                        for (int y = 0; y < rectHeight; ++y) {
                            int start = eltOffset;
                            int end = start + lineLength;
                            if (bits != 0) {
                                int n = start++;
                                data[n] = (byte)(data[n] | mask);
                            }
                            while (start < end) {
                                data[start++] = -1;
                            }
                            if (bits1 != 0) {
                                int n = start;
                                data[n] = (byte)(data[n] | mask1);
                            }
                            eltOffset += lineStride;
                        }
                        break block44;
                    }
                    case 1: {
                        short[] data = ((DataBufferUShort)dataBuffer).getData();
                        int bits = bitOffset & 0xF;
                        int otherBits = bits == 0 ? 0 : 16 - bits;
                        short mask = (short)(65535 >> bits);
                        int lineLength = (rectWidth - otherBits) / 16;
                        int bits1 = rectWidth - otherBits & 0xF;
                        short mask1 = (short)(65535 << 16 - bits1);
                        if (lineLength == 0) {
                            mask = (short)(mask & mask1);
                            bits1 = 0;
                        }
                        for (int y = 0; y < rectHeight; ++y) {
                            int start = eltOffset;
                            int end = start + lineLength;
                            if (bits != 0) {
                                int n = start++;
                                data[n] = (short)(data[n] | mask);
                            }
                            while (start < end) {
                                data[start++] = -1;
                            }
                            if (bits1 != 0) {
                                int n = start++;
                                data[n] = (short)(data[n] | mask1);
                            }
                            eltOffset += lineStride;
                        }
                        break block44;
                    }
                    case 3: {
                        int[] data = ((DataBufferInt)dataBuffer).getData();
                        int bits = bitOffset & 0x1F;
                        int otherBits = bits == 0 ? 0 : 32 - bits;
                        int mask = -1 >> bits;
                        int lineLength = (rectWidth - otherBits) / 32;
                        int bits1 = rectWidth - otherBits & 0x1F;
                        int mask1 = -1 << 32 - bits1;
                        if (lineLength == 0) {
                            mask &= mask1;
                            bits1 = 0;
                        }
                        for (int y = 0; y < rectHeight; ++y) {
                            int start = eltOffset;
                            int end = start + lineLength;
                            if (bits != 0) {
                                int n = start++;
                                data[n] = data[n] | mask;
                            }
                            while (start < end) {
                                data[start++] = -1;
                            }
                            if (bits1 != 0) {
                                int n = start++;
                                data[n] = data[n] | mask1;
                            }
                            eltOffset += lineStride;
                        }
                        break block44;
                    }
                }
                break block44;
            }
            int srcSampleType = accessor.sampleType == -1 ? 0 : accessor.sampleType;
            UnpackedImageData uid = accessor.getPixels(raster, rect, srcSampleType, false);
            rect = uid.rect;
            int lineStride = uid.lineStride;
            int pixelStride = uid.pixelStride;
            switch (uid.type) {
                case 0: {
                    byte[][] bdata = uid.getByteData();
                    for (int b = 0; b < accessor.numBands; ++b) {
                        byte value = (byte)backgroundValues[b];
                        byte[] bd = bdata[b];
                        int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                        for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineStride) {
                            int lastPixel = lo + rect.width * pixelStride;
                            for (int po = lo; po < lastPixel; po += pixelStride) {
                                bd[po] = value;
                            }
                        }
                    }
                    break;
                }
                case 1: 
                case 2: {
                    short[][] sdata = uid.getShortData();
                    for (int b = 0; b < accessor.numBands; ++b) {
                        short value = (short)backgroundValues[b];
                        short[] sd = sdata[b];
                        int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                        for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineStride) {
                            int lastPixel = lo + rect.width * pixelStride;
                            for (int po = lo; po < lastPixel; po += pixelStride) {
                                sd[po] = value;
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    int[][] idata = uid.getIntData();
                    for (int b = 0; b < accessor.numBands; ++b) {
                        int value = (int)backgroundValues[b];
                        int[] id = idata[b];
                        int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                        for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineStride) {
                            int lastPixel = lo + rect.width * pixelStride;
                            for (int po = lo; po < lastPixel; po += pixelStride) {
                                id[po] = value;
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    float[][] fdata = uid.getFloatData();
                    for (int b = 0; b < accessor.numBands; ++b) {
                        float value = (float)backgroundValues[b];
                        float[] fd = fdata[b];
                        int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                        for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineStride) {
                            int lastPixel = lo + rect.width * pixelStride;
                            for (int po = lo; po < lastPixel; po += pixelStride) {
                                fd[po] = value;
                            }
                        }
                    }
                    break;
                }
                case 5: {
                    double[][] ddata = uid.getDoubleData();
                    for (int b = 0; b < accessor.numBands; ++b) {
                        double value = backgroundValues[b];
                        double[] dd = ddata[b];
                        int lastLine = uid.bandOffsets[b] + rect.height * lineStride;
                        for (int lo = uid.bandOffsets[b]; lo < lastLine; lo += lineStride) {
                            int lastPixel = lo + rect.width * pixelStride;
                            for (int po = lo; po < lastPixel; po += pixelStride) {
                                dd[po] = value;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public static void fillBordersWithBackgroundValues(Rectangle outerRect, Rectangle innerRect, WritableRaster raster, double[] backgroundValues) {
        Rectangle rect;
        int outerMaxX = outerRect.x + outerRect.width;
        int outerMaxY = outerRect.y + outerRect.height;
        int innerMaxX = innerRect.x + innerRect.width;
        int innerMaxY = innerRect.y + innerRect.height;
        if (outerRect.x < innerRect.x) {
            rect = new Rectangle(outerRect.x, innerRect.y, innerRect.x - outerRect.x, outerMaxY - innerRect.y);
            ImageUtil.fillBackground(raster, rect, backgroundValues);
        }
        if (outerRect.y < innerRect.y) {
            rect = new Rectangle(outerRect.x, outerRect.y, innerMaxX - outerRect.x, innerRect.y - outerRect.y);
            ImageUtil.fillBackground(raster, rect, backgroundValues);
        }
        if (outerMaxX > innerMaxX) {
            rect = new Rectangle(innerMaxX, outerRect.y, outerMaxX - innerMaxX, innerMaxY - outerRect.y);
            ImageUtil.fillBackground(raster, rect, backgroundValues);
        }
        if (outerMaxY > innerMaxY) {
            rect = new Rectangle(innerRect.x, innerMaxY, outerMaxX - innerRect.x, outerMaxY - innerMaxY);
            ImageUtil.fillBackground(raster, rect, backgroundValues);
        }
    }

    public static KernelJAI getUnsharpMaskEquivalentKernel(KernelJAI kernel, float gain) {
        int k;
        int width = kernel.getWidth();
        int height = kernel.getHeight();
        int xOrigin = kernel.getXOrigin();
        int yOrigin = kernel.getYOrigin();
        float[] oldData = kernel.getKernelData();
        float[] newData = new float[oldData.length];
        for (k = 0; k < width * height; ++k) {
            newData[k] = -gain * oldData[k];
        }
        k = yOrigin * width + xOrigin;
        newData[k] = 1.0f + gain * (1.0f - oldData[k]);
        return new KernelJAI(width, height, xOrigin, yOrigin, newData);
    }

    public static final Point[] getTileIndices(int txmin, int txmax, int tymin, int tymax) {
        if (txmin > txmax || tymin > tymax) {
            return null;
        }
        Point[] tileIndices = new Point[(txmax - txmin + 1) * (tymax - tymin + 1)];
        int k = 0;
        for (int tj = tymin; tj <= tymax; ++tj) {
            for (int ti = txmin; ti <= txmax; ++ti) {
                tileIndices[k++] = new Point(ti, tj);
            }
        }
        return tileIndices;
    }

    public static Vector evaluateParameters(Vector parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException();
        }
        Vector paramEval = parameters;
        int size = parameters.size();
        for (int i = 0; i < size; ++i) {
            Object element = parameters.get(i);
            if (!(element instanceof DeferredData)) continue;
            if (paramEval == parameters) {
                paramEval = (Vector)parameters.clone();
            }
            paramEval.set(i, ((DeferredData)element).getData());
        }
        return paramEval;
    }

    public static ParameterBlock evaluateParameters(ParameterBlock pb) {
        if (pb == null) {
            throw new IllegalArgumentException();
        }
        Vector<Object> parameters = pb.getParameters();
        Vector paramEval = ImageUtil.evaluateParameters(parameters);
        return paramEval == parameters ? pb : new ParameterBlock(pb.getSources(), paramEval);
    }

    public static ColorModel getCompatibleColorModel(SampleModel sm, Map config) {
        ColorModel cm = null;
        if (config == null || !Boolean.FALSE.equals(config.get(JAI.KEY_DEFAULT_COLOR_MODEL_ENABLED))) {
            if (config != null && config.containsKey(JAI.KEY_DEFAULT_COLOR_MODEL_METHOD)) {
                Method cmMethod = (Method)config.get(JAI.KEY_DEFAULT_COLOR_MODEL_METHOD);
                Class<?>[] paramTypes = cmMethod.getParameterTypes();
                if ((cmMethod.getModifiers() & 8) != 8) {
                    throw new RuntimeException(JaiI18N.getString("ImageUtil1"));
                }
                if (cmMethod.getReturnType() != (class$java$awt$image$ColorModel == null ? (class$java$awt$image$ColorModel = ImageUtil.class$("java.awt.image.ColorModel")) : class$java$awt$image$ColorModel)) {
                    throw new RuntimeException(JaiI18N.getString("ImageUtil2"));
                }
                if (paramTypes.length != 1 || !paramTypes[0].equals(class$java$awt$image$SampleModel == null ? (class$java$awt$image$SampleModel = ImageUtil.class$("java.awt.image.SampleModel")) : class$java$awt$image$SampleModel)) {
                    throw new RuntimeException(JaiI18N.getString("ImageUtil3"));
                }
                try {
                    Object[] args = new Object[]{sm};
                    cm = (ColorModel)cmMethod.invoke(null, args);
                }
                catch (Exception e) {
                    String message = JaiI18N.getString("ImageUtil4") + cmMethod.getName();
                    ImageUtil.sendExceptionToListener(message, new ImagingException(message, e));
                }
            } else {
                cm = PlanarImage.createColorModel(sm);
            }
        }
        return cm;
    }

    public static String getStackTraceString(Exception e) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteStream);
        e.printStackTrace(printStream);
        printStream.flush();
        String stackTraceString = byteStream.toString();
        printStream.close();
        return stackTraceString;
    }

    public static ImagingListener getImagingListener(RenderingHints hints) {
        ImagingListener listener = null;
        if (hints != null) {
            listener = (ImagingListener)hints.get(JAI.KEY_IMAGING_LISTENER);
        }
        if (listener == null) {
            listener = JAI.getDefaultInstance().getImagingListener();
        }
        return listener;
    }

    public static ImagingListener getImagingListener(RenderContext context) {
        return ImageUtil.getImagingListener(context.getRenderingHints());
    }

    public static synchronized Object generateID(Object owner) {
        Class<?> c = owner.getClass();
        ++counter;
        byte[] uid = new byte[32];
        int k = 0;
        int i = 7;
        int j = 0;
        while (i >= 0) {
            uid[k++] = (byte)(counter >> j);
            --i;
            j += 8;
        }
        int hash = c.hashCode();
        int i2 = 3;
        int j2 = 0;
        while (i2 >= 0) {
            uid[k++] = (byte)(hash >> j2);
            --i2;
            j2 += 8;
        }
        hash = owner.hashCode();
        i2 = 3;
        j2 = 0;
        while (i2 >= 0) {
            uid[k++] = (byte)(hash >> j2);
            --i2;
            j2 += 8;
        }
        long time = System.currentTimeMillis();
        int i3 = 7;
        int j3 = 0;
        while (i3 >= 0) {
            uid[k++] = (byte)(time >> j3);
            --i3;
            j3 += 8;
        }
        long rand = Double.doubleToLongBits(new Double(Math.random()));
        int i4 = 7;
        int j4 = 0;
        while (i4 >= 0) {
            uid[k++] = (byte)(rand >> j4);
            --i4;
            j4 += 8;
        }
        return new BigInteger(uid);
    }

    static void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
        listener.errorOccurred(message, e, class$com$sun$media$jai$util$ImageUtil == null ? (class$com$sun$media$jai$util$ImageUtil = ImageUtil.class$("com.sun.media.jai.util.ImageUtil")) : class$com$sun$media$jai$util$ImageUtil, false);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

