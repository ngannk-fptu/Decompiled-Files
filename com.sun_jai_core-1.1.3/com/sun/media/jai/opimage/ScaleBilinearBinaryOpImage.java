/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.Rational;
import java.awt.Rectangle;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.ScaleOpImage;

public final class ScaleBilinearBinaryOpImage
extends ScaleOpImage {
    private int subsampleBits;
    int one;
    int shift2;
    int round2;
    long invScaleXInt;
    long invScaleXFrac;
    long invScaleYInt;
    long invScaleYFrac;

    public ScaleBilinearBinaryOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp) {
        super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
        this.subsampleBits = interp.getSubsampleBitsH();
        this.one = 1 << this.subsampleBits;
        this.shift2 = 2 * this.subsampleBits;
        this.round2 = 1 << this.shift2 - 1;
        this.colorModel = layout != null ? layout.getColorModel(source) : source.getColorModel();
        this.sampleModel = source.getSampleModel().createCompatibleSampleModel(this.tileWidth, this.tileHeight);
        if (this.invScaleXRational.num > this.invScaleXRational.denom) {
            this.invScaleXInt = this.invScaleXRational.num / this.invScaleXRational.denom;
            this.invScaleXFrac = this.invScaleXRational.num % this.invScaleXRational.denom;
        } else {
            this.invScaleXInt = 0L;
            this.invScaleXFrac = this.invScaleXRational.num;
        }
        if (this.invScaleYRational.num > this.invScaleYRational.denom) {
            this.invScaleYInt = this.invScaleYRational.num / this.invScaleYRational.denom;
            this.invScaleYFrac = this.invScaleYRational.num % this.invScaleYRational.denom;
        } else {
            this.invScaleYInt = 0L;
            this.invScaleYFrac = this.invScaleYRational.num;
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        int i;
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int srcRectX = srcRect.x;
        int srcRectY = srcRect.y;
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int[] xvalues = new int[dwidth];
        int[] yvalues = new int[dheight];
        int[] xfracvalues = new int[dwidth];
        int[] yfracvalues = new int[dheight];
        long sxNum = dx;
        long sxDenom = 1L;
        long syNum = dy;
        long syDenom = 1L;
        sxNum = sxNum * this.transXRationalDenom - this.transXRationalNum * sxDenom;
        syNum = syNum * this.transYRationalDenom - this.transYRationalNum * syDenom;
        sxNum = 2L * sxNum + (sxDenom *= this.transXRationalDenom);
        sxDenom *= 2L;
        syNum = 2L * syNum + (syDenom *= this.transYRationalDenom);
        syDenom *= 2L;
        sxNum *= this.invScaleXRationalNum;
        syNum *= this.invScaleYRationalNum;
        sxNum = 2L * sxNum - (sxDenom *= this.invScaleXRationalDenom);
        syNum = 2L * syNum - (syDenom *= this.invScaleYRationalDenom);
        syDenom *= 2L;
        int srcXInt = Rational.floor(sxNum, sxDenom *= 2L);
        long srcXFrac = sxNum % sxDenom;
        if (srcXInt < 0) {
            srcXFrac = sxDenom + srcXFrac;
        }
        int srcYInt = Rational.floor(syNum, syDenom);
        long srcYFrac = syNum % syDenom;
        if (srcYInt < 0) {
            srcYFrac = syDenom + srcYFrac;
        }
        long commonXDenom = sxDenom * this.invScaleXRationalDenom;
        srcXFrac *= this.invScaleXRationalDenom;
        long newInvScaleXFrac = this.invScaleXFrac * sxDenom;
        long commonYDenom = syDenom * this.invScaleYRationalDenom;
        srcYFrac *= this.invScaleYRationalDenom;
        long newInvScaleYFrac = this.invScaleYFrac * syDenom;
        for (i = 0; i < dwidth; ++i) {
            xvalues[i] = srcXInt;
            xfracvalues[i] = (int)((float)srcXFrac / (float)commonXDenom * (float)this.one);
            srcXInt = (int)((long)srcXInt + this.invScaleXInt);
            if ((srcXFrac += newInvScaleXFrac) < commonXDenom) continue;
            ++srcXInt;
            srcXFrac -= commonXDenom;
        }
        for (i = 0; i < dheight; ++i) {
            yvalues[i] = srcYInt;
            yfracvalues[i] = (int)((float)srcYFrac / (float)commonYDenom * (float)this.one);
            srcYInt = (int)((long)srcYInt + this.invScaleYInt);
            if ((srcYFrac += newInvScaleYFrac) < commonYDenom) continue;
            ++srcYInt;
            srcYFrac -= commonYDenom;
        }
        switch (source.getSampleModel().getDataType()) {
            case 0: {
                this.byteLoop(source, dest, dx, dy, dwidth, dheight, xvalues, yvalues, xfracvalues, yfracvalues);
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(source, dest, dx, dy, dwidth, dheight, xvalues, yvalues, xfracvalues, yfracvalues);
                break;
            }
            case 3: {
                this.intLoop(source, dest, dx, dy, dwidth, dheight, xvalues, yvalues, xfracvalues, yfracvalues);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
            }
        }
    }

    private void byteLoop(Raster source, WritableRaster dest, int dx, int dy, int dwidth, int dheight, int[] xvalues, int[] yvalues, int[] xfracvalues, int[] yfracvalues) {
        MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
        DataBufferByte sourceDB = (DataBufferByte)source.getDataBuffer();
        int sourceTransX = source.getSampleModelTranslateX();
        int sourceTransY = source.getSampleModelTranslateY();
        int sourceDataBitOffset = sourceSM.getDataBitOffset();
        int sourceScanlineStride = sourceSM.getScanlineStride();
        MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
        DataBufferByte destDB = (DataBufferByte)dest.getDataBuffer();
        int destMinX = dest.getMinX();
        int destMinY = dest.getMinY();
        int destTransX = dest.getSampleModelTranslateX();
        int destTransY = dest.getSampleModelTranslateY();
        int destDataBitOffset = destSM.getDataBitOffset();
        int destScanlineStride = destSM.getScanlineStride();
        byte[] sourceData = sourceDB.getData();
        int sourceDBOffset = sourceDB.getOffset();
        byte[] destData = destDB.getData();
        int destDBOffset = destDB.getOffset();
        int[] sbytenum = new int[dwidth];
        int[] sshift = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            int x = xvalues[i];
            int sbitnum = sourceDataBitOffset + (x - sourceTransX);
            sbytenum[i] = sbitnum >> 3;
            sshift[i] = 7 - (sbitnum & 7);
        }
        int x = 0;
        int y = 0;
        int destYOffset = (dy - destTransY) * destScanlineStride + destDBOffset;
        int dbitnum = destDataBitOffset + (dx - destTransX);
        int i = 0;
        int j = 0;
        for (j = 0; j < dheight; ++j) {
            y = yvalues[j];
            int yfrac = yfracvalues[j];
            int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            dbitnum = destDataBitOffset + (dx - destTransX);
            for (i = 0; i < dwidth; ++i) {
                int xfrac = xfracvalues[i];
                x = xvalues[i];
                int xNextBitNo = sourceDataBitOffset + (x + 1 - sourceTransX);
                int xNextByteNo = xNextBitNo >> 3;
                int xNextShiftNo = 7 - (xNextBitNo & 7);
                int s00 = sourceData[sourceYOffset + sbytenum[i]] >> sshift[i] & 1;
                int s01 = sourceData[sourceYOffset + xNextByteNo] >> xNextShiftNo & 1;
                int s10 = sourceData[sourceYOffset + sourceScanlineStride + sbytenum[i]] >> sshift[i] & 1;
                int s11 = sourceData[sourceYOffset + sourceScanlineStride + xNextByteNo] >> xNextShiftNo & 1;
                int s0 = (s01 - s00) * xfrac + (s00 << this.subsampleBits);
                int s1 = (s11 - s10) * xfrac + (s10 << this.subsampleBits);
                int s = (s1 - s0) * yfrac + ((s0 << this.subsampleBits) + this.round2) >> this.shift2;
                int destByteNum = dbitnum >> 3;
                int destBitShift = 7 - (dbitnum & 7);
                if (s == 1) {
                    int n = destYOffset + destByteNum;
                    destData[n] = (byte)(destData[n] | 1 << destBitShift);
                } else {
                    int n = destYOffset + destByteNum;
                    destData[n] = (byte)(destData[n] & 255 - (1 << destBitShift));
                }
                ++dbitnum;
            }
            destYOffset += destScanlineStride;
        }
    }

    private void shortLoop(Raster source, WritableRaster dest, int dx, int dy, int dwidth, int dheight, int[] xvalues, int[] yvalues, int[] xfracvalues, int[] yfracvalues) {
        MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
        int sourceTransX = source.getSampleModelTranslateX();
        int sourceTransY = source.getSampleModelTranslateY();
        int sourceDataBitOffset = sourceSM.getDataBitOffset();
        int sourceScanlineStride = sourceSM.getScanlineStride();
        MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
        int destMinX = dest.getMinX();
        int destMinY = dest.getMinY();
        int destTransX = dest.getSampleModelTranslateX();
        int destTransY = dest.getSampleModelTranslateY();
        int destDataBitOffset = destSM.getDataBitOffset();
        int destScanlineStride = destSM.getScanlineStride();
        DataBufferUShort sourceDB = (DataBufferUShort)source.getDataBuffer();
        short[] sourceData = sourceDB.getData();
        int sourceDBOffset = sourceDB.getOffset();
        DataBufferUShort destDB = (DataBufferUShort)dest.getDataBuffer();
        short[] destData = destDB.getData();
        int destDBOffset = destDB.getOffset();
        int[] sshortnum = new int[dwidth];
        int[] sshift = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            int x = xvalues[i];
            int sbitnum = sourceDataBitOffset + (x - sourceTransX);
            sshortnum[i] = sbitnum >> 4;
            sshift[i] = 15 - (sbitnum & 0xF);
        }
        int destYOffset = (dy - destTransY) * destScanlineStride + destDBOffset;
        int dbitnum = destDataBitOffset + (dx - destTransX);
        for (int j = 0; j < dheight; ++j) {
            int y = yvalues[j];
            int yfrac = yfracvalues[j];
            int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            dbitnum = destDataBitOffset + (dx - destTransX);
            for (int i = 0; i < dwidth; ++i) {
                int xfrac = xfracvalues[i];
                int x = xvalues[i];
                int xNextBitNo = sourceDataBitOffset + (x + 1 - sourceTransX);
                int xNextShortNo = xNextBitNo >> 4;
                int xNextShiftNo = 15 - (xNextBitNo & 0xF);
                int s00 = sourceData[sourceYOffset + sshortnum[i]] >> sshift[i] & 1;
                int s01 = sourceData[sourceYOffset + xNextShortNo] >> xNextShiftNo & 1;
                int s10 = sourceData[sourceYOffset + sourceScanlineStride + sshortnum[i]] >> sshift[i] & 1;
                int s11 = sourceData[sourceYOffset + sourceScanlineStride + xNextShortNo] >> xNextShiftNo & 1;
                int s0 = (s01 - s00) * xfrac + (s00 << this.subsampleBits);
                int s1 = (s11 - s10) * xfrac + (s10 << this.subsampleBits);
                int s = (s1 - s0) * yfrac + (s0 << this.subsampleBits) + this.round2 >> this.shift2;
                int destShortNum = dbitnum >> 4;
                int destBitShift = 15 - (dbitnum & 0xF);
                if (s == 1) {
                    int n = destYOffset + destShortNum;
                    destData[n] = (short)(destData[n] | 1 << destBitShift);
                } else {
                    int n = destYOffset + destShortNum;
                    destData[n] = (short)(destData[n] & 65535 - (1 << destBitShift));
                }
                ++dbitnum;
            }
            destYOffset += destScanlineStride;
        }
    }

    private void intLoop(Raster source, WritableRaster dest, int dx, int dy, int dwidth, int dheight, int[] xvalues, int[] yvalues, int[] xfracvalues, int[] yfracvalues) {
        MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
        DataBufferInt sourceDB = (DataBufferInt)source.getDataBuffer();
        int sourceTransX = source.getSampleModelTranslateX();
        int sourceTransY = source.getSampleModelTranslateY();
        int sourceDataBitOffset = sourceSM.getDataBitOffset();
        int sourceScanlineStride = sourceSM.getScanlineStride();
        MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
        DataBufferInt destDB = (DataBufferInt)dest.getDataBuffer();
        int destMinX = dest.getMinX();
        int destMinY = dest.getMinY();
        int destTransX = dest.getSampleModelTranslateX();
        int destTransY = dest.getSampleModelTranslateY();
        int destDataBitOffset = destSM.getDataBitOffset();
        int destScanlineStride = destSM.getScanlineStride();
        int[] sourceData = sourceDB.getData();
        int sourceDBOffset = sourceDB.getOffset();
        int[] destData = destDB.getData();
        int destDBOffset = destDB.getOffset();
        int[] sintnum = new int[dwidth];
        int[] sshift = new int[dwidth];
        for (int i = 0; i < dwidth; ++i) {
            int x = xvalues[i];
            int sbitnum = sourceDataBitOffset + (x - sourceTransX);
            sintnum[i] = sbitnum >> 5;
            sshift[i] = 31 - (sbitnum & 0x1F);
        }
        int destYOffset = (dy - destTransY) * destScanlineStride + destDBOffset;
        int dbitnum = destDataBitOffset + (dx - destTransX);
        for (int j = 0; j < dheight; ++j) {
            int y = yvalues[j];
            int yfrac = yfracvalues[j];
            int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            dbitnum = destDataBitOffset + (dx - destTransX);
            for (int i = 0; i < dwidth; ++i) {
                int xfrac = xfracvalues[i];
                int x = xvalues[i];
                int xNextBitNo = sourceDataBitOffset + (x + 1 - sourceTransX);
                int xNextIntNo = xNextBitNo >> 5;
                int xNextShiftNo = 31 - (xNextBitNo & 0x1F);
                int s00 = sourceData[sourceYOffset + sintnum[i]] >> sshift[i] & 1;
                int s01 = sourceData[sourceYOffset + xNextIntNo] >> xNextShiftNo & 1;
                int s10 = sourceData[sourceYOffset + sourceScanlineStride + sintnum[i]] >> sshift[i] & 1;
                int s11 = sourceData[sourceYOffset + sourceScanlineStride + xNextIntNo] >> xNextShiftNo & 1;
                int s0 = (s01 - s00) * xfrac + (s00 << this.subsampleBits);
                int s1 = (s11 - s10) * xfrac + (s10 << this.subsampleBits);
                int s = (s1 - s0) * yfrac + (s0 << this.subsampleBits) + this.round2 >> this.shift2;
                int destIntNum = dbitnum >> 5;
                int destBitShift = 31 - (dbitnum & 0x1F);
                if (s == 1) {
                    int n = destYOffset + destIntNum;
                    destData[n] = destData[n] | 1 << destBitShift;
                } else {
                    int n = destYOffset + destIntNum;
                    destData[n] = destData[n] & 255 - (1 << destBitShift);
                }
                ++dbitnum;
            }
            destYOffset += destScanlineStride;
        }
    }
}

