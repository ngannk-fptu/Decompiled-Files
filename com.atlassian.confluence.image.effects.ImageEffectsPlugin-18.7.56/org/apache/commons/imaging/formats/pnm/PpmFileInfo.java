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

class PpmFileInfo
extends FileInfo {
    private final int max;
    private final float scale;
    private final int bytesPerSample;

    PpmFileInfo(int width, int height, boolean rawbits, int max) throws ImageReadException {
        super(width, height, rawbits);
        if (max <= 0) {
            throw new ImageReadException("PPM maxVal " + max + " is out of range [1;65535]");
        }
        if (max <= 255) {
            this.scale = 255.0f;
            this.bytesPerSample = 1;
        } else if (max <= 65535) {
            this.scale = 65535.0f;
            this.bytesPerSample = 2;
        } else {
            throw new ImageReadException("PPM maxVal " + max + " is out of range [1;65535]");
        }
        this.max = max;
    }

    @Override
    public boolean hasAlpha() {
        return false;
    }

    @Override
    public int getNumComponents() {
        return 3;
    }

    @Override
    public int getBitDepth() {
        return this.max;
    }

    @Override
    public ImageFormat getImageType() {
        return ImageFormats.PPM;
    }

    @Override
    public String getImageTypeDescription() {
        return "PPM: portable pixmap file format";
    }

    @Override
    public String getMIMEType() {
        return "image/x-portable-pixmap";
    }

    @Override
    public ImageInfo.ColorType getColorType() {
        return ImageInfo.ColorType.RGB;
    }

    @Override
    public int getRGB(InputStream is) throws IOException {
        int red = PpmFileInfo.readSample(is, this.bytesPerSample);
        int green = PpmFileInfo.readSample(is, this.bytesPerSample);
        int blue = PpmFileInfo.readSample(is, this.bytesPerSample);
        red = PpmFileInfo.scaleSample(red, this.scale, this.max);
        green = PpmFileInfo.scaleSample(green, this.scale, this.max);
        blue = PpmFileInfo.scaleSample(blue, this.scale, this.max);
        int alpha = 255;
        return 0xFF000000 | (0xFF & red) << 16 | (0xFF & green) << 8 | (0xFF & blue) << 0;
    }

    @Override
    public int getRGB(WhiteSpaceReader wsr) throws IOException {
        int red = Integer.parseInt(wsr.readtoWhiteSpace());
        int green = Integer.parseInt(wsr.readtoWhiteSpace());
        int blue = Integer.parseInt(wsr.readtoWhiteSpace());
        red = PpmFileInfo.scaleSample(red, this.scale, this.max);
        green = PpmFileInfo.scaleSample(green, this.scale, this.max);
        blue = PpmFileInfo.scaleSample(blue, this.scale, this.max);
        int alpha = 255;
        return 0xFF000000 | (0xFF & red) << 16 | (0xFF & green) << 8 | (0xFF & blue) << 0;
    }
}

