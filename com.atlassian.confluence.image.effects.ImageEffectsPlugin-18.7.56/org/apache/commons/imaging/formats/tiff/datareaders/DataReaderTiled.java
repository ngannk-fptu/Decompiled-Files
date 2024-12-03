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
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.TiffRasterData;
import org.apache.commons.imaging.formats.tiff.datareaders.BitInputStream;
import org.apache.commons.imaging.formats.tiff.datareaders.ImageDataReader;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterRgb;

public final class DataReaderTiled
extends ImageDataReader {
    private final int tileWidth;
    private final int tileLength;
    private final int bitsPerPixel;
    private final int compression;
    private final ByteOrder byteOrder;
    private final TiffImageData.Tiles imageData;

    public DataReaderTiled(TiffDirectory directory, PhotometricInterpreter photometricInterpreter, int tileWidth, int tileLength, int bitsPerPixel, int[] bitsPerSample, int predictor, int samplesPerPixel, int sampleFormat, int width, int height, int compression, ByteOrder byteOrder, TiffImageData.Tiles imageData) {
        super(directory, photometricInterpreter, bitsPerSample, predictor, samplesPerPixel, sampleFormat, width, height);
        this.tileWidth = tileWidth;
        this.tileLength = tileLength;
        this.bitsPerPixel = bitsPerPixel;
        this.compression = compression;
        this.imageData = imageData;
        this.byteOrder = byteOrder;
    }

    private void interpretTile(ImageBuilder imageBuilder, byte[] bytes, int startX, int startY, int xLimit, int yLimit) throws ImageReadException, IOException {
        if (this.sampleFormat == 3) {
            int i0 = startY;
            int i1 = startY + this.tileLength;
            if (i1 > yLimit) {
                i1 = yLimit;
            }
            int j0 = startX;
            int j1 = startX + this.tileWidth;
            if (j1 > xLimit) {
                j1 = xLimit;
            }
            int[] samples = new int[4];
            int[] b = this.unpackFloatingPointSamples(j1 - j0, i1 - i0, this.tileWidth, bytes, this.predictor, this.bitsPerPixel, this.byteOrder);
            for (int i = i0; i < i1; ++i) {
                int row = i - startY;
                int rowOffset = row * this.tileWidth;
                for (int j = j0; j < j1; ++j) {
                    int column = j - startX;
                    samples[0] = b[rowOffset + column];
                    this.photometricInterpreter.interpretPixel(imageBuilder, samples, j, i);
                }
            }
            return;
        }
        boolean allSamplesAreOneByte = this.isHomogenous(8);
        if (this.bitsPerPixel == 24 && allSamplesAreOneByte && this.photometricInterpreter instanceof PhotometricInterpreterRgb) {
            int k;
            int i;
            int i0 = startY;
            int i1 = startY + this.tileLength;
            if (i1 > yLimit) {
                i1 = yLimit;
            }
            int j0 = startX;
            int j1 = startX + this.tileWidth;
            if (j1 > xLimit) {
                j1 = xLimit;
            }
            if (this.predictor == 2) {
                for (i = i0; i < i1; ++i) {
                    k = (i - i0) * this.tileWidth * 3;
                    int p0 = bytes[k++] & 0xFF;
                    int p1 = bytes[k++] & 0xFF;
                    int p2 = bytes[k++] & 0xFF;
                    for (int j = 1; j < this.tileWidth; ++j) {
                        p0 = bytes[k] + p0 & 0xFF;
                        bytes[k++] = (byte)p0;
                        p1 = bytes[k] + p1 & 0xFF;
                        bytes[k++] = (byte)p1;
                        p2 = bytes[k] + p2 & 0xFF;
                        bytes[k++] = (byte)p2;
                    }
                }
            }
            for (i = i0; i < i1; ++i) {
                k = (i - i0) * this.tileWidth * 3;
                int j = j0;
                while (j < j1) {
                    int rgb = 0xFF000000 | (bytes[k] << 8 | bytes[k + 1] & 0xFF) << 8 | bytes[k + 2] & 0xFF;
                    imageBuilder.setRGB(j, i, rgb);
                    ++j;
                    k += 3;
                }
            }
            return;
        }
        try (BitInputStream bis = new BitInputStream(new ByteArrayInputStream(bytes), this.byteOrder);){
            int pixelsPerTile = this.tileWidth * this.tileLength;
            int tileX = 0;
            int tileY = 0;
            int[] samples = new int[this.bitsPerSampleLength];
            this.resetPredictor();
            for (int i = 0; i < pixelsPerTile; ++i) {
                int x = tileX + startX;
                int y = tileY + startY;
                this.getSamplesAsBytes(bis, samples);
                if (x < xLimit && y < yLimit) {
                    samples = this.applyPredictor(samples);
                    this.photometricInterpreter.interpretPixel(imageBuilder, samples, x, y);
                }
                if (++tileX < this.tileWidth) continue;
                tileX = 0;
                this.resetPredictor();
                bis.flushCache();
                if (++tileY < this.tileLength) continue;
                break;
            }
        }
    }

    @Override
    public void readImageData(ImageBuilder imageBuilder) throws ImageReadException, IOException {
        int bitsPerRow = this.tileWidth * this.bitsPerPixel;
        int bytesPerRow = (bitsPerRow + 7) / 8;
        int bytesPerTile = bytesPerRow * this.tileLength;
        int x = 0;
        int y = 0;
        for (TiffElement.DataElement tile2 : this.imageData.tiles) {
            byte[] compressed = tile2.getData();
            byte[] decompressed = this.decompress(compressed, this.compression, bytesPerTile, this.tileWidth, this.tileLength);
            this.interpretTile(imageBuilder, decompressed, x, y, this.width, this.height);
            if ((x += this.tileWidth) < this.width) continue;
            x = 0;
            if ((y += this.tileLength) >= this.height) break;
        }
    }

    @Override
    public BufferedImage readImageData(Rectangle subImage) throws ImageReadException, IOException {
        int bitsPerRow = this.tileWidth * this.bitsPerPixel;
        int bytesPerRow = (bitsPerRow + 7) / 8;
        int bytesPerTile = bytesPerRow * this.tileLength;
        int col0 = subImage.x / this.tileWidth;
        int col1 = (subImage.x + subImage.width - 1) / this.tileWidth;
        int row0 = subImage.y / this.tileLength;
        int row1 = (subImage.y + subImage.height - 1) / this.tileLength;
        int nCol = col1 - col0 + 1;
        int nRow = row1 - row0 + 1;
        int workingWidth = nCol * this.tileWidth;
        int workingHeight = nRow * this.tileLength;
        int nColumnsOfTiles = (this.width + this.tileWidth - 1) / this.tileWidth;
        int x0 = col0 * this.tileWidth;
        int y0 = row0 * this.tileLength;
        ImageBuilder workingBuilder = new ImageBuilder(workingWidth, workingHeight, false);
        for (int iRow = row0; iRow <= row1; ++iRow) {
            for (int iCol = col0; iCol <= col1; ++iCol) {
                int tile = iRow * nColumnsOfTiles + iCol;
                byte[] compressed = this.imageData.tiles[tile].getData();
                byte[] decompressed = this.decompress(compressed, this.compression, bytesPerTile, this.tileWidth, this.tileLength);
                int x = iCol * this.tileWidth - x0;
                int y = iRow * this.tileLength - y0;
                this.interpretTile(workingBuilder, decompressed, x, y, workingWidth, workingHeight);
            }
        }
        if (subImage.x == x0 && subImage.y == y0 && subImage.width == workingWidth && subImage.height == workingHeight) {
            return workingBuilder.getBufferedImage();
        }
        return workingBuilder.getSubimage(subImage.x - x0, subImage.y - y0, subImage.width, subImage.height);
    }

    @Override
    public TiffRasterData readRasterData(Rectangle subImage) throws ImageReadException, IOException {
        int rasterHeight;
        int rasterWidth;
        int yRaster;
        int xRaster;
        int bitsPerRow = this.tileWidth * this.bitsPerPixel;
        int bytesPerRow = (bitsPerRow + 7) / 8;
        int bytesPerTile = bytesPerRow * this.tileLength;
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
        int col0 = xRaster / this.tileWidth;
        int col1 = (xRaster + rasterWidth - 1) / this.tileWidth;
        int row0 = yRaster / this.tileLength;
        int row1 = (yRaster + rasterHeight - 1) / this.tileLength;
        int nColumnsOfTiles = (this.width + this.tileWidth - 1) / this.tileWidth;
        int x0 = col0 * this.tileWidth;
        int y0 = row0 * this.tileLength;
        for (int iRow = row0; iRow <= row1; ++iRow) {
            for (int iCol = col0; iCol <= col1; ++iCol) {
                int tile = iRow * nColumnsOfTiles + iCol;
                byte[] compressed = this.imageData.tiles[tile].getData();
                byte[] decompressed = this.decompress(compressed, this.compression, bytesPerTile, this.tileWidth, this.tileLength);
                int x = iCol * this.tileWidth - x0;
                int y = iRow * this.tileLength - y0;
                int[] blockData = this.unpackFloatingPointSamples(this.tileWidth, this.tileLength, this.tileWidth, decompressed, this.predictor, this.bitsPerPixel, this.byteOrder);
                this.transferBlockToRaster(x, y, this.tileWidth, this.tileLength, blockData, xRaster, yRaster, rasterWidth, rasterHeight, rasterData);
            }
        }
        return new TiffRasterData(rasterWidth, rasterHeight, rasterData);
    }
}

