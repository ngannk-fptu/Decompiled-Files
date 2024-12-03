/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.io.IOException;
import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.datareaders.DataReaderStrips;
import org.apache.commons.imaging.formats.tiff.datareaders.DataReaderTiled;
import org.apache.commons.imaging.formats.tiff.datareaders.ImageDataReader;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public abstract class TiffImageData {
    public abstract TiffElement.DataElement[] getImageData();

    public abstract boolean stripsNotTiles();

    public abstract ImageDataReader getDataReader(TiffDirectory var1, PhotometricInterpreter var2, int var3, int[] var4, int var5, int var6, int var7, int var8, int var9, ByteOrder var10) throws IOException, ImageReadException;

    private static int extractSampleFormat(TiffDirectory directory) throws ImageReadException {
        short[] sSampleFmt = directory.getFieldValue(TiffTagConstants.TIFF_TAG_SAMPLE_FORMAT, false);
        if (sSampleFmt != null && sSampleFmt.length > 0) {
            return sSampleFmt[0];
        }
        return 0;
    }

    public static class ByteSourceData
    extends Data {
        ByteSourceFile byteSourceFile;

        public ByteSourceData(long offset, int length, ByteSourceFile byteSource) {
            super(offset, length, new byte[0]);
            this.byteSourceFile = byteSource;
        }

        @Override
        public String getElementDescription() {
            return "Tiff image data: " + this.getDataLength() + " bytes";
        }

        @Override
        public byte[] getData() {
            try {
                return this.byteSourceFile.getBlock(this.offset, this.length);
            }
            catch (IOException ioex) {
                return new byte[0];
            }
        }
    }

    public static class Data
    extends TiffElement.DataElement {
        public Data(long offset, int length, byte[] data) {
            super(offset, length, data);
        }

        @Override
        public String getElementDescription() {
            return "Tiff image data: " + this.getDataLength() + " bytes";
        }
    }

    public static class Strips
    extends TiffImageData {
        private final TiffElement.DataElement[] strips;
        public final int rowsPerStrip;

        public Strips(TiffElement.DataElement[] strips, int rowsPerStrip) {
            this.strips = strips;
            this.rowsPerStrip = rowsPerStrip;
        }

        @Override
        public TiffElement.DataElement[] getImageData() {
            return this.strips;
        }

        public TiffElement.DataElement getImageData(int offset) {
            return this.strips[offset];
        }

        public int getImageDataLength() {
            return this.strips.length;
        }

        @Override
        public boolean stripsNotTiles() {
            return true;
        }

        @Override
        public ImageDataReader getDataReader(TiffDirectory directory, PhotometricInterpreter photometricInterpreter, int bitsPerPixel, int[] bitsPerSample, int predictor, int samplesPerPixel, int width, int height, int compression, ByteOrder byteorder) throws IOException, ImageReadException {
            int sampleFormat = TiffImageData.extractSampleFormat(directory);
            return new DataReaderStrips(directory, photometricInterpreter, bitsPerPixel, bitsPerSample, predictor, samplesPerPixel, sampleFormat, width, height, compression, byteorder, this.rowsPerStrip, this);
        }
    }

    public static class Tiles
    extends TiffImageData {
        public final TiffElement.DataElement[] tiles;
        private final int tileWidth;
        private final int tileLength;

        public Tiles(TiffElement.DataElement[] tiles, int tileWidth, int tileLength) {
            this.tiles = tiles;
            this.tileWidth = tileWidth;
            this.tileLength = tileLength;
        }

        @Override
        public TiffElement.DataElement[] getImageData() {
            return this.tiles;
        }

        @Override
        public boolean stripsNotTiles() {
            return false;
        }

        @Override
        public ImageDataReader getDataReader(TiffDirectory directory, PhotometricInterpreter photometricInterpreter, int bitsPerPixel, int[] bitsPerSample, int predictor, int samplesPerPixel, int width, int height, int compression, ByteOrder byteOrder) throws IOException, ImageReadException {
            int sampleFormat = TiffImageData.extractSampleFormat(directory);
            return new DataReaderTiled(directory, photometricInterpreter, this.tileWidth, this.tileLength, bitsPerPixel, bitsPerSample, predictor, samplesPerPixel, sampleFormat, width, height, compression, byteOrder, this);
        }

        public int getTileWidth() {
            return this.tileWidth;
        }

        public int getTileHeight() {
            return this.tileLength;
        }
    }
}

