/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pnm;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.pnm.WhiteSpaceReader;

abstract class FileInfo {
    final int width;
    final int height;
    final boolean rawbits;

    FileInfo(int width, int height, boolean rawbits) {
        this.width = width;
        this.height = height;
        this.rawbits = rawbits;
    }

    abstract boolean hasAlpha();

    abstract int getNumComponents();

    abstract int getBitDepth();

    abstract ImageFormat getImageType();

    abstract String getImageTypeDescription();

    abstract String getMIMEType();

    abstract ImageInfo.ColorType getColorType();

    abstract int getRGB(WhiteSpaceReader var1) throws IOException;

    abstract int getRGB(InputStream var1) throws IOException;

    void newline() {
    }

    static int readSample(InputStream is, int bytesPerSample) throws IOException {
        int sample = 0;
        for (int i = 0; i < bytesPerSample; ++i) {
            int nextByte = is.read();
            if (nextByte < 0) {
                throw new IOException("PNM: Unexpected EOF");
            }
            sample <<= 8;
            sample |= nextByte;
        }
        return sample;
    }

    static int scaleSample(int sample, float scale, int max) throws IOException {
        if (sample < 0) {
            throw new IOException("Negative pixel values are invalid in PNM files");
        }
        if (sample > max) {
            sample = 0;
        }
        return (int)((float)sample * scale / (float)max + 0.5f);
    }

    void readImage(ImageBuilder imageBuilder, InputStream is) throws IOException {
        if (!this.rawbits) {
            WhiteSpaceReader wsr = new WhiteSpaceReader(is);
            for (int y = 0; y < this.height; ++y) {
                for (int x = 0; x < this.width; ++x) {
                    int rgb = this.getRGB(wsr);
                    imageBuilder.setRGB(x, y, rgb);
                }
                this.newline();
            }
        } else {
            for (int y = 0; y < this.height; ++y) {
                for (int x = 0; x < this.width; ++x) {
                    int rgb = this.getRGB(is);
                    imageBuilder.setRGB(x, y, rgb);
                }
                this.newline();
            }
        }
    }
}

