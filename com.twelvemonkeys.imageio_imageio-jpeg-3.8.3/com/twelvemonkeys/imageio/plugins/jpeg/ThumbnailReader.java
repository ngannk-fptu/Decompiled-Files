/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.lang.Validate;
import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

abstract class ThumbnailReader {
    ThumbnailReader() {
    }

    public abstract BufferedImage read() throws IOException;

    public abstract int getWidth() throws IOException;

    public abstract int getHeight() throws IOException;

    public IIOMetadata readMetadata() throws IOException {
        return null;
    }

    static class JPEGThumbnailReader
    extends ThumbnailReader {
        private final ImageReader reader;
        private final ImageInputStream input;
        private final long offset;
        private Dimension dimension;

        public JPEGThumbnailReader(ImageReader imageReader, ImageInputStream imageInputStream, long l) {
            this.reader = (ImageReader)Validate.notNull((Object)imageReader, (String)"reader");
            this.input = (ImageInputStream)Validate.notNull((Object)imageInputStream, (String)"input");
            this.offset = (Long)Validate.isTrue((l >= 0L ? 1 : 0) != 0, (Object)l, (String)"offset");
        }

        private void initReader() throws IOException {
            if (this.reader.getInput() != this.input) {
                this.input.seek(this.offset);
                this.reader.setInput(this.input);
            }
        }

        @Override
        public BufferedImage read() throws IOException {
            this.initReader();
            return this.reader.read(0, null);
        }

        private Dimension readDimensions() throws IOException {
            if (this.dimension == null) {
                this.initReader();
                this.dimension = new Dimension(this.reader.getWidth(0), this.reader.getHeight(0));
            }
            return this.dimension;
        }

        @Override
        public int getWidth() throws IOException {
            return this.readDimensions().width;
        }

        @Override
        public int getHeight() throws IOException {
            return this.readDimensions().height;
        }

        @Override
        public IIOMetadata readMetadata() throws IOException {
            this.initReader();
            return this.reader.getImageMetadata(0);
        }
    }

    static class IndexedThumbnailReader
    extends ThumbnailReader {
        private final int width;
        private final int height;
        private final byte[] palette;
        private final int paletteOff;
        private final byte[] data;
        private final int dataOff;

        public IndexedThumbnailReader(int n, int n2, byte[] byArray, int n3, byte[] byArray2, int n4) {
            this.width = (Integer)Validate.isTrue((n > 0 ? 1 : 0) != 0, (Object)n, (String)"width");
            this.height = (Integer)Validate.isTrue((n2 > 0 ? 1 : 0) != 0, (Object)n2, (String)"height");
            this.palette = (byte[])Validate.notNull((Object)byArray, (String)"palette");
            this.paletteOff = (Integer)Validate.isTrue((n3 >= 0 && n3 < byArray.length ? 1 : 0) != 0, (Object)n3, (String)"paletteOff");
            this.data = (byte[])Validate.notNull((Object)byArray2, (String)"data");
            this.dataOff = (Integer)Validate.isTrue((n4 >= 0 && n4 < byArray2.length ? 1 : 0) != 0, (Object)n4, (String)"dataOff");
        }

        @Override
        public BufferedImage read() throws IOException {
            int[] nArray = new int[256];
            for (int i = 0; i < nArray.length; ++i) {
                nArray[i] = (this.palette[this.paletteOff + 3 * i] & 0xFF) << 16 | (this.palette[this.paletteOff + 3 * i + 1] & 0xFF) << 8 | this.palette[this.paletteOff + 3 * i + 2] & 0xFF;
            }
            IndexColorModel indexColorModel = new IndexColorModel(8, nArray.length, nArray, 0, false, -1, 0);
            DataBufferByte dataBufferByte = new DataBufferByte(this.data, this.data.length - this.dataOff, this.dataOff);
            WritableRaster writableRaster = Raster.createPackedRaster(dataBufferByte, this.width, this.height, 8, null);
            return new BufferedImage(indexColorModel, writableRaster, indexColorModel.isAlphaPremultiplied(), null);
        }

        @Override
        public int getWidth() throws IOException {
            return this.width;
        }

        @Override
        public int getHeight() throws IOException {
            return this.height;
        }
    }

    static class UncompressedThumbnailReader
    extends ThumbnailReader {
        private final int width;
        private final int height;
        private final byte[] data;
        private final int offset;

        public UncompressedThumbnailReader(int n, int n2, byte[] byArray) {
            this(n, n2, byArray, 0);
        }

        public UncompressedThumbnailReader(int n, int n2, byte[] byArray, int n3) {
            this.width = (Integer)Validate.isTrue((n > 0 ? 1 : 0) != 0, (Object)n, (String)"width");
            this.height = (Integer)Validate.isTrue((n2 > 0 ? 1 : 0) != 0, (Object)n2, (String)"height");
            this.data = (byte[])Validate.notNull((Object)byArray, (String)"data");
            this.offset = (Integer)Validate.isTrue((n3 >= 0 && n3 < byArray.length ? 1 : 0) != 0, (Object)n3, (String)"offset");
        }

        @Override
        public BufferedImage read() throws IOException {
            ComponentColorModel componentColorModel;
            WritableRaster writableRaster;
            DataBufferByte dataBufferByte = new DataBufferByte(this.data, this.data.length, this.offset);
            if (this.data.length == this.width * this.height) {
                writableRaster = Raster.createInterleavedRaster(dataBufferByte, this.width, this.height, this.width, 1, new int[]{0}, null);
                componentColorModel = new ComponentColorModel(ColorSpace.getInstance(1003), false, false, 1, 0);
            } else {
                writableRaster = Raster.createInterleavedRaster(dataBufferByte, this.width, this.height, this.width * 3, 3, new int[]{0, 1, 2}, null);
                componentColorModel = new ComponentColorModel(ColorSpace.getInstance(1000), false, false, 1, 0);
            }
            return new BufferedImage(componentColorModel, writableRaster, componentColorModel.isAlphaPremultiplied(), null);
        }

        @Override
        public int getWidth() throws IOException {
            return this.width;
        }

        @Override
        public int getHeight() throws IOException {
            return this.height;
        }
    }
}

