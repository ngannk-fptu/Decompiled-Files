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

final class ScaleNearestBinaryOpImage
extends ScaleOpImage {
    long invScaleXInt;
    long invScaleXFrac;
    long invScaleYInt;
    long invScaleYFrac;

    public ScaleNearestBinaryOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp) {
        super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
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
        Raster source = sources[0];
        Rectangle srcRect = source.getBounds();
        int srcRectX = srcRect.x;
        int srcRectY = srcRect.y;
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
        int[] xvalues = new int[dwidth];
        long sxNum = dx;
        long sxDenom = 1L;
        sxNum = sxNum * this.transXRationalDenom - this.transXRationalNum * sxDenom;
        sxNum = 2L * sxNum + (sxDenom *= this.transXRationalDenom);
        sxDenom *= 2L;
        int srcXInt = Rational.floor(sxNum *= this.invScaleXRationalNum, sxDenom *= this.invScaleXRationalDenom);
        long srcXFrac = sxNum % sxDenom;
        if (srcXInt < 0) {
            srcXFrac = sxDenom + srcXFrac;
        }
        long commonXDenom = sxDenom * this.invScaleXRationalDenom;
        srcXFrac *= this.invScaleXRationalDenom;
        long newInvScaleXFrac = this.invScaleXFrac * sxDenom;
        for (int i = 0; i < dwidth; ++i) {
            xvalues[i] = srcXInt;
            srcXInt = (int)((long)srcXInt + this.invScaleXInt);
            if ((srcXFrac += newInvScaleXFrac) < commonXDenom) continue;
            ++srcXInt;
            srcXFrac -= commonXDenom;
        }
        int[] yvalues = new int[dheight];
        long syNum = dy;
        long syDenom = 1L;
        syNum = syNum * this.transYRationalDenom - this.transYRationalNum * syDenom;
        syNum = 2L * syNum + (syDenom *= this.transYRationalDenom);
        syDenom *= 2L;
        int srcYInt = Rational.floor(syNum *= this.invScaleYRationalNum, syDenom *= this.invScaleYRationalDenom);
        long srcYFrac = syNum % syDenom;
        if (srcYInt < 0) {
            srcYFrac = syDenom + srcYFrac;
        }
        long commonYDenom = syDenom * this.invScaleYRationalDenom;
        srcYFrac *= this.invScaleYRationalDenom;
        long newInvScaleYFrac = this.invScaleYFrac * syDenom;
        for (int i = 0; i < dheight; ++i) {
            yvalues[i] = srcYInt;
            srcYInt = (int)((long)srcYInt + this.invScaleYInt);
            if ((srcYFrac += newInvScaleYFrac) < commonYDenom) continue;
            ++srcYInt;
            srcYFrac -= commonYDenom;
        }
        switch (source.getSampleModel().getDataType()) {
            case 0: {
                this.byteLoop(source, dest, destRect, xvalues, yvalues);
                break;
            }
            case 1: 
            case 2: {
                this.shortLoop(source, dest, destRect, xvalues, yvalues);
                break;
            }
            case 3: {
                this.intLoop(source, dest, destRect, xvalues, yvalues);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
            }
        }
    }

    private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect, int[] xvalues, int[] yvalues) {
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
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
        for (int j = 0; j < dheight; ++j) {
            int delement;
            int dshift;
            int dindex;
            int val;
            byte selement;
            int i;
            int y = yvalues[j];
            int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
            int dbitnum = destDataBitOffset + (dx - destTransX);
            for (i = 0; i < dwidth && (dbitnum & 7) != 0; ++i) {
                selement = sourceData[sourceYOffset + sbytenum[i]];
                val = selement >> sshift[i] & 1;
                dindex = destYOffset + (dbitnum >> 3);
                dshift = 7 - (dbitnum & 7);
                delement = destData[dindex];
                destData[dindex] = (byte)(delement |= val << dshift);
                ++dbitnum;
            }
            dindex = destYOffset + (dbitnum >> 3);
            int nbytes = dwidth - i + 1 >> 3;
            if (nbytes > 0 && j > 0 && y == yvalues[j - 1]) {
                System.arraycopy(destData, dindex - destScanlineStride, destData, dindex, nbytes);
                i += nbytes * 8;
                dbitnum += nbytes * 8;
            } else {
                while (i < dwidth - 7) {
                    selement = sourceData[sourceYOffset + sbytenum[i]];
                    val = selement >> sshift[i] & 1;
                    delement = val << 7;
                    selement = sourceData[sourceYOffset + sbytenum[++i]];
                    val = selement >> sshift[i] & 1;
                    delement |= val << 6;
                    selement = sourceData[sourceYOffset + sbytenum[++i]];
                    val = selement >> sshift[i] & 1;
                    delement |= val << 5;
                    selement = sourceData[sourceYOffset + sbytenum[++i]];
                    val = selement >> sshift[i] & 1;
                    delement |= val << 4;
                    selement = sourceData[sourceYOffset + sbytenum[++i]];
                    val = selement >> sshift[i] & 1;
                    delement |= val << 3;
                    selement = sourceData[sourceYOffset + sbytenum[++i]];
                    val = selement >> sshift[i] & 1;
                    delement |= val << 2;
                    selement = sourceData[sourceYOffset + sbytenum[++i]];
                    val = selement >> sshift[i] & 1;
                    delement |= val << 1;
                    selement = sourceData[sourceYOffset + sbytenum[++i]];
                    val = selement >> sshift[i] & 1;
                    ++i;
                    destData[dindex++] = (byte)(delement |= val);
                    dbitnum += 8;
                }
            }
            if (i >= dwidth) continue;
            dindex = destYOffset + (dbitnum >> 3);
            delement = destData[dindex];
            while (i < dwidth) {
                selement = sourceData[sourceYOffset + sbytenum[i]];
                val = selement >> sshift[i] & 1;
                dshift = 7 - (dbitnum & 7);
                delement |= val << dshift;
                ++dbitnum;
                ++i;
            }
            destData[dindex] = (byte)delement;
        }
    }

    private void shortLoop(Raster source, WritableRaster dest, Rectangle destRect, int[] xvalues, int[] yvalues) {
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
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
        for (int j = 0; j < dheight; ++j) {
            int delement;
            int dshift;
            int dindex;
            int val;
            short selement;
            int i;
            int y = yvalues[j];
            int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
            int dbitnum = destDataBitOffset + (dx - destTransX);
            for (i = 0; i < dwidth && (dbitnum & 0xF) != 0; ++i) {
                selement = sourceData[sourceYOffset + sshortnum[i]];
                val = selement >> sshift[i] & 1;
                dindex = destYOffset + (dbitnum >> 4);
                dshift = 15 - (dbitnum & 0xF);
                delement = destData[dindex];
                destData[dindex] = (short)(delement |= val << dshift);
                ++dbitnum;
            }
            dindex = destYOffset + (dbitnum >> 4);
            int nshorts = dwidth - i >> 4;
            if (nshorts > 0 && j > 0 && y == yvalues[j - 1]) {
                int offset = destYOffset + (dbitnum >> 4);
                System.arraycopy(destData, offset - destScanlineStride, destData, offset, nshorts);
                i += nshorts >> 4;
                dbitnum += nshorts >> 4;
            } else {
                while (i < dwidth - 15) {
                    delement = 0;
                    for (int b = 15; b >= 0; --b) {
                        selement = sourceData[sourceYOffset + sshortnum[i]];
                        val = selement >> sshift[i] & 1;
                        delement |= val << b;
                        ++i;
                    }
                    destData[dindex++] = (short)delement;
                    dbitnum += 16;
                }
            }
            if (i >= dwidth) continue;
            dindex = destYOffset + (dbitnum >> 4);
            delement = destData[dindex];
            while (i < dwidth) {
                selement = sourceData[sourceYOffset + sshortnum[i]];
                val = selement >> sshift[i] & 1;
                dshift = 15 - (dbitnum & 0xF);
                delement |= val << dshift;
                ++dbitnum;
                ++i;
            }
            destData[dindex] = (short)delement;
        }
    }

    private void intLoop(Raster source, WritableRaster dest, Rectangle destRect, int[] xvalues, int[] yvalues) {
        int dx = destRect.x;
        int dy = destRect.y;
        int dwidth = destRect.width;
        int dheight = destRect.height;
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
        for (int j = 0; j < dheight; ++j) {
            int delement;
            int dshift;
            int dindex;
            int val;
            int selement;
            int i;
            int y = yvalues[j];
            int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
            int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
            int dbitnum = destDataBitOffset + (dx - destTransX);
            for (i = 0; i < dwidth && (dbitnum & 0x1F) != 0; ++i) {
                selement = sourceData[sourceYOffset + sintnum[i]];
                val = selement >> sshift[i] & 1;
                dindex = destYOffset + (dbitnum >> 5);
                dshift = 31 - (dbitnum & 0x1F);
                delement = destData[dindex];
                destData[dindex] = delement |= val << dshift;
                ++dbitnum;
            }
            dindex = destYOffset + (dbitnum >> 5);
            int nints = dwidth - i >> 5;
            if (nints > 0 && j > 0 && y == yvalues[j - 1]) {
                int offset = destYOffset + (dbitnum >> 5);
                System.arraycopy(destData, offset - destScanlineStride, destData, offset, nints);
                i += nints >> 5;
                dbitnum += nints >> 5;
            } else {
                while (i < dwidth - 31) {
                    delement = 0;
                    for (int b = 31; b >= 0; --b) {
                        selement = sourceData[sourceYOffset + sintnum[i]];
                        val = selement >> sshift[i] & 1;
                        delement |= val << b;
                        ++i;
                    }
                    destData[dindex++] = delement;
                    dbitnum += 32;
                }
            }
            if (i >= dwidth) continue;
            dindex = destYOffset + (dbitnum >> 5);
            delement = destData[dindex];
            while (i < dwidth) {
                selement = sourceData[sourceYOffset + sintnum[i]];
                val = selement >> sshift[i] & 1;
                dshift = 31 - (dbitnum & 0x1F);
                delement |= val << dshift;
                ++dbitnum;
                ++i;
            }
            destData[dindex] = delement;
        }
    }
}

