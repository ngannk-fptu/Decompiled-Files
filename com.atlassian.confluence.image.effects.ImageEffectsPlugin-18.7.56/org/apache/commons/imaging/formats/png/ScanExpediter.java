/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.BitParser;
import org.apache.commons.imaging.formats.png.FilterType;
import org.apache.commons.imaging.formats.png.GammaCorrection;
import org.apache.commons.imaging.formats.png.PngColorType;
import org.apache.commons.imaging.formats.png.chunks.PngChunkPlte;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilter;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilterAverage;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilterNone;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilterPaeth;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilterSub;
import org.apache.commons.imaging.formats.png.scanlinefilters.ScanlineFilterUp;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilter;

abstract class ScanExpediter {
    final int width;
    final int height;
    final InputStream is;
    final BufferedImage bi;
    final PngColorType pngColorType;
    final int bitDepth;
    final int bytesPerPixel;
    final int bitsPerPixel;
    final PngChunkPlte pngChunkPLTE;
    final GammaCorrection gammaCorrection;
    final TransparencyFilter transparencyFilter;

    ScanExpediter(int width, int height, InputStream is, BufferedImage bi, PngColorType pngColorType, int bitDepth, int bitsPerPixel, PngChunkPlte pngChunkPLTE, GammaCorrection gammaCorrection, TransparencyFilter transparencyFilter) {
        this.width = width;
        this.height = height;
        this.is = is;
        this.bi = bi;
        this.pngColorType = pngColorType;
        this.bitDepth = bitDepth;
        this.bytesPerPixel = this.getBitsToBytesRoundingUp(bitsPerPixel);
        this.bitsPerPixel = bitsPerPixel;
        this.pngChunkPLTE = pngChunkPLTE;
        this.gammaCorrection = gammaCorrection;
        this.transparencyFilter = transparencyFilter;
    }

    final int getBitsToBytesRoundingUp(int bits) {
        return (bits + 7) / 8;
    }

    final int getPixelARGB(int alpha, int red, int green, int blue) {
        return (0xFF & alpha) << 24 | (0xFF & red) << 16 | (0xFF & green) << 8 | (0xFF & blue) << 0;
    }

    final int getPixelRGB(int red, int green, int blue) {
        return this.getPixelARGB(255, red, green, blue);
    }

    public abstract void drive() throws ImageReadException, IOException;

    int getRGB(BitParser bitParser, int pixelIndexInScanline) throws ImageReadException, IOException {
        switch (this.pngColorType) {
            case GREYSCALE: {
                int sample = bitParser.getSampleAsByte(pixelIndexInScanline, 0);
                if (this.gammaCorrection != null) {
                    sample = this.gammaCorrection.correctSample(sample);
                }
                int rgb = this.getPixelRGB(sample, sample, sample);
                if (this.transparencyFilter != null) {
                    rgb = this.transparencyFilter.filter(rgb, sample);
                }
                return rgb;
            }
            case TRUE_COLOR: {
                int red = bitParser.getSampleAsByte(pixelIndexInScanline, 0);
                int green = bitParser.getSampleAsByte(pixelIndexInScanline, 1);
                int blue = bitParser.getSampleAsByte(pixelIndexInScanline, 2);
                int rgb = this.getPixelRGB(red, green, blue);
                if (this.transparencyFilter != null) {
                    rgb = this.transparencyFilter.filter(rgb, -1);
                }
                if (this.gammaCorrection != null) {
                    int alpha = (0xFF000000 & rgb) >> 24;
                    red = this.gammaCorrection.correctSample(red);
                    green = this.gammaCorrection.correctSample(green);
                    blue = this.gammaCorrection.correctSample(blue);
                    rgb = this.getPixelARGB(alpha, red, green, blue);
                }
                return rgb;
            }
            case INDEXED_COLOR: {
                int index = bitParser.getSample(pixelIndexInScanline, 0);
                int rgb = this.pngChunkPLTE.getRGB(index);
                if (this.transparencyFilter != null) {
                    rgb = this.transparencyFilter.filter(rgb, index);
                }
                return rgb;
            }
            case GREYSCALE_WITH_ALPHA: {
                int sample = bitParser.getSampleAsByte(pixelIndexInScanline, 0);
                int alpha = bitParser.getSampleAsByte(pixelIndexInScanline, 1);
                if (this.gammaCorrection != null) {
                    sample = this.gammaCorrection.correctSample(sample);
                }
                return this.getPixelARGB(alpha, sample, sample, sample);
            }
            case TRUE_COLOR_WITH_ALPHA: {
                int red = bitParser.getSampleAsByte(pixelIndexInScanline, 0);
                int green = bitParser.getSampleAsByte(pixelIndexInScanline, 1);
                int blue = bitParser.getSampleAsByte(pixelIndexInScanline, 2);
                int alpha = bitParser.getSampleAsByte(pixelIndexInScanline, 3);
                if (this.gammaCorrection != null) {
                    red = this.gammaCorrection.correctSample(red);
                    green = this.gammaCorrection.correctSample(green);
                    blue = this.gammaCorrection.correctSample(blue);
                }
                return this.getPixelARGB(alpha, red, green, blue);
            }
        }
        throw new ImageReadException("PNG: unknown color type: " + (Object)((Object)this.pngColorType));
    }

    ScanlineFilter getScanlineFilter(FilterType filterType, int bytesPerPixel) throws ImageReadException {
        switch (filterType) {
            case NONE: {
                return new ScanlineFilterNone();
            }
            case SUB: {
                return new ScanlineFilterSub(bytesPerPixel);
            }
            case UP: {
                return new ScanlineFilterUp();
            }
            case AVERAGE: {
                return new ScanlineFilterAverage(bytesPerPixel);
            }
            case PAETH: {
                return new ScanlineFilterPaeth(bytesPerPixel);
            }
        }
        return null;
    }

    byte[] unfilterScanline(FilterType filterType, byte[] src, byte[] prev, int bytesPerPixel) throws ImageReadException, IOException {
        ScanlineFilter filter = this.getScanlineFilter(filterType, bytesPerPixel);
        byte[] dst = new byte[src.length];
        filter.unfilter(src, dst, prev);
        return dst;
    }

    byte[] getNextScanline(InputStream is, int length, byte[] prev, int bytesPerPixel) throws ImageReadException, IOException {
        int filterType = is.read();
        if (filterType < 0) {
            throw new ImageReadException("PNG: missing filter type");
        }
        if (filterType >= FilterType.values().length) {
            throw new ImageReadException("PNG: unknown filterType: " + filterType);
        }
        byte[] scanline = BinaryFunctions.readBytes("scanline", is, length, "PNG: missing image data");
        return this.unfilterScanline(FilterType.values()[filterType], scanline, prev, bytesPerPixel);
    }
}

