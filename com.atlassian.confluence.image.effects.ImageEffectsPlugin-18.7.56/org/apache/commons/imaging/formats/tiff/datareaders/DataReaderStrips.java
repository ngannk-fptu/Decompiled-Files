/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.datareaders;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.TiffRasterData;
import org.apache.commons.imaging.formats.tiff.datareaders.BitInputStream;
import org.apache.commons.imaging.formats.tiff.datareaders.ImageDataReader;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterRgb;

public final class DataReaderStrips
extends ImageDataReader {
    private final int bitsPerPixel;
    private final int compression;
    private final int rowsPerStrip;
    private final ByteOrder byteOrder;
    private int x;
    private int y;
    private final TiffImageData.Strips imageData;

    public DataReaderStrips(TiffDirectory directory, PhotometricInterpreter photometricInterpreter, int bitsPerPixel, int[] bitsPerSample, int predictor, int samplesPerPixel, int sampleFormat, int width, int height, int compression, ByteOrder byteOrder, int rowsPerStrip, TiffImageData.Strips imageData) {
        super(directory, photometricInterpreter, bitsPerSample, predictor, samplesPerPixel, sampleFormat, width, height);
        this.bitsPerPixel = bitsPerPixel;
        this.compression = compression;
        this.rowsPerStrip = rowsPerStrip;
        this.imageData = imageData;
        this.byteOrder = byteOrder;
    }

    private void interpretStrip(ImageBuilder imageBuilder, byte[] bytes, int pixelsPerStrip, int yLimit) throws ImageReadException, IOException {
        if (this.y >= yLimit) {
            return;
        }
        if (this.sampleFormat == 3) {
            int k = 0;
            int nRows = pixelsPerStrip / this.width;
            if (this.y + nRows > yLimit) {
                nRows = yLimit - this.y;
            }
            int i0 = this.y;
            int i1 = this.y + nRows;
            this.x = 0;
            this.y += nRows;
            int[] samples = new int[1];
            int[] b = this.unpackFloatingPointSamples(this.width, i1 - i0, this.width, bytes, this.predictor, this.bitsPerPixel, this.byteOrder);
            for (int i = i0; i < i1; ++i) {
                for (int j = 0; j < this.width; ++j) {
                    samples[0] = b[k++];
                    this.photometricInterpreter.interpretPixel(imageBuilder, samples, j, i);
                }
            }
            return;
        }
        boolean allSamplesAreOneByte = this.isHomogenous(8);
        if (this.predictor != 2 && this.bitsPerPixel == 8 && allSamplesAreOneByte) {
            int k = 0;
            int nRows = pixelsPerStrip / this.width;
            if (this.y + nRows > yLimit) {
                nRows = yLimit - this.y;
            }
            int i0 = this.y;
            int i1 = this.y + nRows;
            this.x = 0;
            this.y += nRows;
            int[] samples = new int[1];
            for (int i = i0; i < i1; ++i) {
                for (int j = 0; j < this.width; ++j) {
                    samples[0] = bytes[k++] & 0xFF;
                    this.photometricInterpreter.interpretPixel(imageBuilder, samples, j, i);
                }
            }
            return;
        }
        if (this.bitsPerPixel == 24 && allSamplesAreOneByte && this.photometricInterpreter instanceof PhotometricInterpreterRgb) {
            int i;
            int k = 0;
            int nRows = pixelsPerStrip / this.width;
            if (this.y + nRows > yLimit) {
                nRows = yLimit - this.y;
            }
            int i0 = this.y;
            int i1 = this.y + nRows;
            this.x = 0;
            this.y += nRows;
            if (this.predictor == 2) {
                for (i = i0; i < i1; ++i) {
                    int p0 = bytes[k++] & 0xFF;
                    int p1 = bytes[k++] & 0xFF;
                    int p2 = bytes[k++] & 0xFF;
                    for (int j = 1; j < this.width; ++j) {
                        p0 = bytes[k] + p0 & 0xFF;
                        bytes[k++] = (byte)p0;
                        p1 = bytes[k] + p1 & 0xFF;
                        bytes[k++] = (byte)p1;
                        p2 = bytes[k] + p2 & 0xFF;
                        bytes[k++] = (byte)p2;
                    }
                }
            }
            k = 0;
            for (i = i0; i < i1; ++i) {
                int j = 0;
                while (j < this.width) {
                    int rgb = 0xFF000000 | (bytes[k] << 8 | bytes[k + 1] & 0xFF) << 8 | bytes[k + 2] & 0xFF;
                    imageBuilder.setRGB(j, i, rgb);
                    ++j;
                    k += 3;
                }
            }
            return;
        }
        try (BitInputStream bis = new BitInputStream(new ByteArrayInputStream(bytes), this.byteOrder);){
            int[] samples = new int[this.bitsPerSampleLength];
            this.resetPredictor();
            for (int i = 0; i < pixelsPerStrip; ++i) {
                this.getSamplesAsBytes(bis, samples);
                if (this.x < this.width) {
                    samples = this.applyPredictor(samples);
                    this.photometricInterpreter.interpretPixel(imageBuilder, samples, this.x, this.y);
                }
                ++this.x;
                if (this.x < this.width) continue;
                this.x = 0;
                this.resetPredictor();
                ++this.y;
                bis.flushCache();
                if (this.y < yLimit) continue;
                break;
            }
        }
    }

    @Override
    public void readImageData(ImageBuilder imageBuilder) throws ImageReadException, IOException {
        for (int strip = 0; strip < this.imageData.getImageDataLength(); ++strip) {
            long rowsPerStripLong = 0xFFFFFFFFL & (long)this.rowsPerStrip;
            long rowsRemaining = (long)this.height - (long)strip * rowsPerStripLong;
            long rowsInThisStrip = Math.min(rowsRemaining, rowsPerStripLong);
            long bytesPerRow = (this.bitsPerPixel * this.width + 7) / 8;
            long bytesPerStrip = rowsInThisStrip * bytesPerRow;
            long pixelsPerStrip = rowsInThisStrip * (long)this.width;
            byte[] compressed = this.imageData.getImageData(strip).getData();
            byte[] decompressed = this.decompress(compressed, this.compression, (int)bytesPerStrip, this.width, (int)rowsInThisStrip);
            this.interpretStrip(imageBuilder, decompressed, (int)pixelsPerStrip, this.height);
        }
    }

    @Override
    public BufferedImage readImageData(Rectangle subImage) throws ImageReadException, IOException {
        int strip0 = subImage.y / this.rowsPerStrip;
        int strip1 = (subImage.y + subImage.height - 1) / this.rowsPerStrip;
        int workingHeight = (strip1 - strip0 + 1) * this.rowsPerStrip;
        int y0 = strip0 * this.rowsPerStrip;
        int yLimit = subImage.y - y0 + subImage.height;
        ImageBuilder workingBuilder = new ImageBuilder(this.width, workingHeight, false);
        for (int strip = strip0; strip <= strip1; ++strip) {
            long rowsPerStripLong = 0xFFFFFFFFL & (long)this.rowsPerStrip;
            long rowsRemaining = (long)this.height - (long)strip * rowsPerStripLong;
            long rowsInThisStrip = Math.min(rowsRemaining, rowsPerStripLong);
            long bytesPerRow = (this.bitsPerPixel * this.width + 7) / 8;
            long bytesPerStrip = rowsInThisStrip * bytesPerRow;
            long pixelsPerStrip = rowsInThisStrip * (long)this.width;
            byte[] compressed = this.imageData.getImageData(strip).getData();
            byte[] decompressed = this.decompress(compressed, this.compression, (int)bytesPerStrip, this.width, (int)rowsInThisStrip);
            this.interpretStrip(workingBuilder, decompressed, (int)pixelsPerStrip, yLimit);
        }
        if (subImage.x == 0 && subImage.y == y0 && subImage.width == this.width && subImage.height == workingHeight) {
            return workingBuilder.getBufferedImage();
        }
        return workingBuilder.getSubimage(subImage.x, subImage.y - y0, subImage.width, subImage.height);
    }

    @Override
    public TiffRasterData readRasterData(Rectangle subImage) throws ImageReadException, IOException {
        int rasterHeight;
        int rasterWidth;
        int yRaster;
        int xRaster;
        if (subImage != null) {
            xRaster = subImage.x;
            yRaster = subImage.y;
            rasterWidth = subImage.width;
            rasterHeight = subImage.height;
        } else {
            xRaster = 0;
            yRaster = 0;
            rasterWidth = this.width;
            rasterHeight = this.height;
        }
        float[] rasterData = new float[rasterWidth * rasterHeight];
        int strip0 = yRaster / this.rowsPerStrip;
        int strip1 = (yRaster + rasterHeight - 1) / this.rowsPerStrip;
        for (int strip = strip0; strip <= strip1; ++strip) {
            int yStrip = strip * this.rowsPerStrip;
            int rowsRemaining = this.height - yStrip;
            int rowsInThisStrip = Math.min(rowsRemaining, this.rowsPerStrip);
            int bytesPerRow = (this.bitsPerPixel * this.width + 7) / 8;
            int bytesPerStrip = rowsInThisStrip * bytesPerRow;
            byte[] compressed = this.imageData.getImageData(strip).getData();
            byte[] decompressed = this.decompress(compressed, this.compression, bytesPerStrip, this.width, rowsInThisStrip);
            int[] blockData = this.unpackFloatingPointSamples(this.width, rowsInThisStrip, this.width, decompressed, this.predictor, this.bitsPerPixel, this.byteOrder);
            this.transferBlockToRaster(0, yStrip, this.width, rowsInThisStrip, blockData, xRaster, yRaster, rasterWidth, rasterHeight, rasterData);
        }
        return new TiffRasterData(rasterWidth, rasterHeight, rasterData);
    }
}

