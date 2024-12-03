/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.bmp.BmpHeaderInfo;
import org.apache.commons.imaging.formats.bmp.PixelParser;

class PixelParserRle
extends PixelParser {
    private static final Logger LOGGER = Logger.getLogger(PixelParserRle.class.getName());

    PixelParserRle(BmpHeaderInfo bhi, byte[] colorTable, byte[] imageData) {
        super(bhi, colorTable, imageData);
    }

    private int getSamplesPerByte() throws ImageReadException {
        if (this.bhi.bitsPerPixel == 8) {
            return 1;
        }
        if (this.bhi.bitsPerPixel == 4) {
            return 2;
        }
        throw new ImageReadException("BMP RLE: bad BitsPerPixel: " + this.bhi.bitsPerPixel);
    }

    private int[] convertDataToSamples(int data) throws ImageReadException {
        int[] rgbs;
        if (this.bhi.bitsPerPixel == 8) {
            rgbs = new int[]{this.getColorTableRGB(data)};
        } else if (this.bhi.bitsPerPixel == 4) {
            rgbs = new int[2];
            int sample1 = data >> 4;
            int sample2 = 0xF & data;
            rgbs[0] = this.getColorTableRGB(sample1);
            rgbs[1] = this.getColorTableRGB(sample2);
        } else {
            throw new ImageReadException("BMP RLE: bad BitsPerPixel: " + this.bhi.bitsPerPixel);
        }
        return rgbs;
    }

    private int processByteOfData(int[] rgbs, int repeat, int x, int y, int width, int height, ImageBuilder imageBuilder) {
        int pixelsWritten = 0;
        for (int i = 0; i < repeat; ++i) {
            if (x >= 0 && x < width && y >= 0 && y < height) {
                int rgb = rgbs[i % rgbs.length];
                imageBuilder.setRGB(x, y, rgb);
            } else {
                LOGGER.fine("skipping bad pixel (" + x + "," + y + ")");
            }
            ++x;
            ++pixelsWritten;
        }
        return pixelsWritten;
    }

    @Override
    public void processImage(ImageBuilder imageBuilder) throws ImageReadException, IOException {
        int width = this.bhi.width;
        int height = this.bhi.height;
        int x = 0;
        int y = height - 1;
        boolean done = false;
        block5: while (!done) {
            int a = 0xFF & BinaryFunctions.readByte("RLE (" + x + "," + y + ") a", this.is, "BMP: Bad RLE");
            int b = 0xFF & BinaryFunctions.readByte("RLE (" + x + "," + y + ") b", this.is, "BMP: Bad RLE");
            if (a == 0) {
                switch (b) {
                    case 0: {
                        --y;
                        x = 0;
                        continue block5;
                    }
                    case 1: {
                        done = true;
                        continue block5;
                    }
                    case 2: {
                        int deltaX = 0xFF & BinaryFunctions.readByte("RLE deltaX", this.is, "BMP: Bad RLE");
                        int deltaY = 0xFF & BinaryFunctions.readByte("RLE deltaY", this.is, "BMP: Bad RLE");
                        x += deltaX;
                        y -= deltaY;
                        continue block5;
                    }
                }
                int samplesPerByte = this.getSamplesPerByte();
                int size = b / samplesPerByte;
                if (b % samplesPerByte > 0) {
                    ++size;
                }
                if (size % 2 != 0) {
                    ++size;
                }
                byte[] bytes = BinaryFunctions.readBytes("bytes", this.is, size, "RLE: Absolute Mode");
                int remaining = b;
                int i = 0;
                while (remaining > 0) {
                    int[] samples = this.convertDataToSamples(0xFF & bytes[i]);
                    int towrite = Math.min(remaining, samplesPerByte);
                    int written = this.processByteOfData(samples, towrite, x, y, width, height, imageBuilder);
                    x += written;
                    remaining -= written;
                    ++i;
                }
                continue;
            }
            int[] rgbs = this.convertDataToSamples(b);
            x += this.processByteOfData(rgbs, a, x, y, width, height, imageBuilder);
        }
    }
}

