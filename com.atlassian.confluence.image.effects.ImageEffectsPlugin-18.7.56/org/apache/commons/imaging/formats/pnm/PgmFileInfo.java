/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pnm;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.pnm.FileInfo;
import org.apache.commons.imaging.formats.pnm.WhiteSpaceReader;

class PgmFileInfo
extends FileInfo {
    private final int max;
    private final float scale;
    private final int bytesPerSample;

    PgmFileInfo(int width, int height, boolean rawbits, int max) throws ImageReadException {
        super(width, height, rawbits);
        if (max <= 0) {
            throw new ImageReadException("PGM maxVal " + max + " is out of range [1;65535]");
        }
        if (max <= 255) {
            this.scale = 255.0f;
            this.bytesPerSample = 1;
        } else if (max <= 65535) {
            this.scale = 65535.0f;
            this.bytesPerSample = 2;
        } else {
            throw new ImageReadException("PGM maxVal " + max + " is out of range [1;65535]");
        }
        this.max = max;
    }

    @Override
    public boolean hasAlpha() {
        return false;
    }

    @Override
    public int getNumComponents() {
        return 1;
    }

    @Override
    public int getBitDepth() {
        return this.max;
    }

    @Override
    public ImageFormat getImageType() {
        return ImageFormats.PGM;
    }

    @Override
    public String getImageTypeDescription() {
        return "PGM: portable graymap file format";
    }

    @Override
    public String getMIMEType() {
        return "image/x-portable-graymap";
    }

    @Override
    public ImageInfo.ColorType getColorType() {
        return ImageInfo.ColorType.GRAYSCALE;
    }

    @Override
    public int getRGB(InputStream is) throws IOException {
        int sample = PgmFileInfo.readSample(is, this.bytesPerSample);
        sample = PgmFileInfo.scaleSample(sample, this.scale, this.max);
        int alpha = 255;
        return 0xFF000000 | (0xFF & sample) << 16 | (0xFF & sample) << 8 | (0xFF & sample) << 0;
    }

    @Override
    public int getRGB(WhiteSpaceReader wsr) throws IOException {
        int sample = Integer.parseInt(wsr.readtoWhiteSpace());
        sample = PgmFileInfo.scaleSample(sample, this.scale, this.max);
        int alpha = 255;
        return 0xFF000000 | (0xFF & sample) << 16 | (0xFF & sample) << 8 | (0xFF & sample) << 0;
    }
}

