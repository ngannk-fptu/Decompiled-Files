/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.datareaders;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.common.PackBits;
import org.apache.commons.imaging.common.ZlibDeflate;
import org.apache.commons.imaging.common.itu_t4.T4AndT6Compression;
import org.apache.commons.imaging.common.mylzw.MyLzwDecompressor;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffRasterData;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.datareaders.BitInputStream;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public abstract class ImageDataReader {
    protected final TiffDirectory directory;
    protected final PhotometricInterpreter photometricInterpreter;
    private final int[] bitsPerSample;
    protected final int bitsPerSampleLength;
    private final int[] last;
    protected final int predictor;
    protected final int samplesPerPixel;
    protected final int width;
    protected final int height;
    protected final int sampleFormat;

    public ImageDataReader(TiffDirectory directory, PhotometricInterpreter photometricInterpreter, int[] bitsPerSample, int predictor, int samplesPerPixel, int sampleFormat, int width, int height) {
        this.directory = directory;
        this.photometricInterpreter = photometricInterpreter;
        this.bitsPerSample = bitsPerSample;
        this.bitsPerSampleLength = bitsPerSample.length;
        this.samplesPerPixel = samplesPerPixel;
        this.sampleFormat = sampleFormat;
        this.predictor = predictor;
        this.width = width;
        this.height = height;
        this.last = new int[samplesPerPixel];
    }

    public abstract void readImageData(ImageBuilder var1) throws ImageReadException, IOException;

    public abstract BufferedImage readImageData(Rectangle var1) throws ImageReadException, IOException;

    protected boolean isHomogenous(int size) {
        for (int element : this.bitsPerSample) {
            if (element == size) continue;
            return false;
        }
        return true;
    }

    void getSamplesAsBytes(BitInputStream bis, int[] result) throws IOException {
        for (int i = 0; i < this.bitsPerSample.length; ++i) {
            int bits = this.bitsPerSample[i];
            int sample = bis.readBits(bits);
            if (bits < 8) {
                int sign = sample & 1;
                sample <<= 8 - bits;
                if (sign > 0) {
                    sample |= (1 << 8 - bits) - 1;
                }
            } else if (bits > 8) {
                sample >>= bits - 8;
            }
            result[i] = sample;
        }
    }

    protected void resetPredictor() {
        Arrays.fill(this.last, 0);
    }

    protected int[] applyPredictor(int[] samples) {
        if (this.predictor == 2) {
            for (int i = 0; i < samples.length; ++i) {
                samples[i] = 0xFF & samples[i] + this.last[i];
                this.last[i] = samples[i];
            }
        }
        return samples;
    }

    protected byte[] decompress(byte[] compressedInput, int compression, int expectedSize, int tileWidth, int tileHeight) throws ImageReadException, IOException {
        byte[] compressedOrdered;
        TiffField fillOrderField = this.directory.findField(TiffTagConstants.TIFF_TAG_FILL_ORDER);
        int fillOrder = 1;
        if (fillOrderField != null) {
            fillOrder = fillOrderField.getIntValue();
        }
        if (fillOrder == 1) {
            compressedOrdered = compressedInput;
        } else if (fillOrder == 2) {
            compressedOrdered = new byte[compressedInput.length];
            for (int i = 0; i < compressedInput.length; ++i) {
                compressedOrdered[i] = (byte)(Integer.reverse(0xFF & compressedInput[i]) >>> 24);
            }
        } else {
            throw new ImageReadException("TIFF FillOrder=" + fillOrder + " is invalid");
        }
        switch (compression) {
            case 1: {
                return compressedOrdered;
            }
            case 2: {
                return T4AndT6Compression.decompressModifiedHuffman(compressedOrdered, tileWidth, tileHeight);
            }
            case 3: {
                boolean hasFillBitsBeforeEOL;
                boolean usesUncompressedMode;
                int t4Options = 0;
                TiffField field = this.directory.findField(TiffTagConstants.TIFF_TAG_T4_OPTIONS);
                if (field != null) {
                    t4Options = field.getIntValue();
                }
                boolean is2D = (t4Options & 1) != 0;
                boolean bl = usesUncompressedMode = (t4Options & 2) != 0;
                if (usesUncompressedMode) {
                    throw new ImageReadException("T.4 compression with the uncompressed mode extension is not yet supported");
                }
                boolean bl2 = hasFillBitsBeforeEOL = (t4Options & 4) != 0;
                if (is2D) {
                    return T4AndT6Compression.decompressT4_2D(compressedOrdered, tileWidth, tileHeight, hasFillBitsBeforeEOL);
                }
                return T4AndT6Compression.decompressT4_1D(compressedOrdered, tileWidth, tileHeight, hasFillBitsBeforeEOL);
            }
            case 4: {
                boolean usesUncompressedMode;
                int t6Options = 0;
                TiffField field = this.directory.findField(TiffTagConstants.TIFF_TAG_T6_OPTIONS);
                if (field != null) {
                    t6Options = field.getIntValue();
                }
                boolean bl = usesUncompressedMode = (t6Options & 2) != 0;
                if (usesUncompressedMode) {
                    throw new ImageReadException("T.6 compression with the uncompressed mode extension is not yet supported");
                }
                return T4AndT6Compression.decompressT6(compressedOrdered, tileWidth, tileHeight);
            }
            case 5: {
                ByteArrayInputStream is = new ByteArrayInputStream(compressedOrdered);
                int lzwMinimumCodeSize = 8;
                MyLzwDecompressor myLzwDecompressor = new MyLzwDecompressor(8, ByteOrder.BIG_ENDIAN);
                myLzwDecompressor.setTiffLZWMode();
                return myLzwDecompressor.decompress(is, expectedSize);
            }
            case 32773: {
                return new PackBits().decompress(compressedOrdered, expectedSize);
            }
            case 8: 
            case 32946: {
                return ZlibDeflate.decompress(compressedInput, expectedSize);
            }
        }
        throw new ImageReadException("Tiff: unknown/unsupported compression: " + compression);
    }

    protected int[] unpackFloatingPointSamples(int width, int height, int scansize, byte[] bytes, int predictor, int bitsPerSample, ByteOrder byteOrder) throws ImageReadException {
        int bytesPerSample = bitsPerSample / 8;
        int nBytes = bytesPerSample * scansize * height;
        int length = bytes.length < nBytes ? nBytes / scansize : height;
        int[] samples = new int[scansize * height];
        if (predictor == 3) {
            if (bitsPerSample != 32) {
                throw new ImageReadException("Imaging does not yet support floating-point data with predictor type 3 for " + bitsPerSample + " bits per sample");
            }
            int bytesInRow = scansize * 4;
            for (int i = 0; i < length; ++i) {
                int aOffset = i * bytesInRow;
                int bOffset = aOffset + scansize;
                int cOffset = bOffset + scansize;
                int dOffset = cOffset + scansize;
                for (int j = 1; j < bytesInRow; ++j) {
                    int n = aOffset + j;
                    bytes[n] = (byte)(bytes[n] + bytes[aOffset + j - 1]);
                }
                int index = i * scansize;
                for (int j = 0; j < width; ++j) {
                    byte a = bytes[aOffset + j];
                    byte b = bytes[bOffset + j];
                    byte c = bytes[cOffset + j];
                    byte d = bytes[dOffset + j];
                    samples[index++] = (a & 0xFF) << 24 | (b & 0xFF) << 16 | (c & 0xFF) << 8 | d & 0xFF;
                }
            }
            return samples;
        }
        if (bitsPerSample == 64) {
            int k = 0;
            int index = 0;
            for (int i = 0; i < length; ++i) {
                for (int j = 0; j < scansize; ++j) {
                    long b0 = (long)bytes[k++] & 0xFFL;
                    long b1 = (long)bytes[k++] & 0xFFL;
                    long b2 = (long)bytes[k++] & 0xFFL;
                    long b3 = (long)bytes[k++] & 0xFFL;
                    long b4 = (long)bytes[k++] & 0xFFL;
                    long b5 = (long)bytes[k++] & 0xFFL;
                    long b6 = (long)bytes[k++] & 0xFFL;
                    long b7 = (long)bytes[k++] & 0xFFL;
                    long sbits = byteOrder == ByteOrder.LITTLE_ENDIAN ? b7 << 56 | b6 << 48 | b5 << 40 | b4 << 32 | b3 << 24 | b2 << 16 | b1 << 8 | b0 : b0 << 56 | b1 << 48 | b2 << 40 | b3 << 32 | b4 << 24 | b5 << 16 | b6 << 8 | b7;
                    float f = (float)Double.longBitsToDouble(sbits);
                    samples[index++] = Float.floatToRawIntBits(f);
                }
            }
        } else if (bitsPerSample == 32) {
            int k = 0;
            int index = 0;
            for (int i = 0; i < length; ++i) {
                for (int j = 0; j < scansize; ++j) {
                    int b0 = bytes[k++] & 0xFF;
                    int b1 = bytes[k++] & 0xFF;
                    int b2 = bytes[k++] & 0xFF;
                    int b3 = bytes[k++] & 0xFF;
                    int sbits = byteOrder == ByteOrder.LITTLE_ENDIAN ? b3 << 24 | b2 << 16 | b1 << 8 | b0 : b0 << 24 | b1 << 16 | b2 << 8 | b3;
                    samples[index++] = sbits;
                }
            }
        } else {
            throw new ImageReadException("Imaging does not support floating-point samples with " + bitsPerSample + " bits per sample");
        }
        return samples;
    }

    void transferBlockToRaster(int xBlock, int yBlock, int blockWidth, int blockHeight, int[] blockData, int xRaster, int yRaster, int rasterWidth, int rasterHeight, float[] rasterData) {
        int xR0 = xBlock - xRaster;
        int yR0 = yBlock - yRaster;
        int xR1 = xR0 + blockWidth;
        int yR1 = yR0 + blockHeight;
        if (xR0 < 0) {
            xR0 = 0;
        }
        if (yR0 < 0) {
            yR0 = 0;
        }
        if (xR1 > rasterWidth) {
            xR1 = rasterWidth;
        }
        if (yR1 > rasterHeight) {
            yR1 = rasterHeight;
        }
        int xB0 = xR0 + xRaster - xBlock;
        int yB0 = yR0 + yRaster - yBlock;
        if (xB0 < 0) {
            xR0 -= xB0;
            xB0 = 0;
        }
        if (yB0 < 0) {
            yR0 -= yB0;
            yB0 = 0;
        }
        int w = xR1 - xR0;
        int h = yR1 - yR0;
        if (w <= 0 || h <= 0) {
            return;
        }
        if (w > blockWidth) {
            w = blockWidth;
        }
        if (h > blockHeight) {
            h = blockHeight;
        }
        for (int i = 0; i < h; ++i) {
            int yR = yR0 + i;
            int yB = yB0 + i;
            int rOffset = yR * rasterWidth + xR0;
            int bOffset = yB * blockWidth + xB0;
            for (int j = 0; j < w; ++j) {
                rasterData[rOffset + j] = Float.intBitsToFloat(blockData[bOffset + j]);
            }
        }
    }

    public abstract TiffRasterData readRasterData(Rectangle var1) throws ImageReadException, IOException;
}

