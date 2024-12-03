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

class PamFileInfo
extends FileInfo {
    private final int depth;
    private final int maxval;
    private final float scale;
    private final int bytesPerSample;
    private final boolean hasAlpha;
    private final TupleReader tupleReader;

    PamFileInfo(int width, int height, int depth, int maxval, String tupleType) throws ImageReadException {
        super(width, height, true);
        this.depth = depth;
        this.maxval = maxval;
        if (maxval <= 0) {
            throw new ImageReadException("PAM maxVal " + maxval + " is out of range [1;65535]");
        }
        if (maxval <= 255) {
            this.scale = 255.0f;
            this.bytesPerSample = 1;
        } else if (maxval <= 65535) {
            this.scale = 65535.0f;
            this.bytesPerSample = 2;
        } else {
            throw new ImageReadException("PAM maxVal " + maxval + " is out of range [1;65535]");
        }
        this.hasAlpha = tupleType.endsWith("_ALPHA");
        if ("BLACKANDWHITE".equals(tupleType) || "BLACKANDWHITE_ALPHA".equals(tupleType)) {
            this.tupleReader = new GrayscaleTupleReader(ImageInfo.ColorType.BW);
        } else if ("GRAYSCALE".equals(tupleType) || "GRAYSCALE_ALPHA".equals(tupleType)) {
            this.tupleReader = new GrayscaleTupleReader(ImageInfo.ColorType.GRAYSCALE);
        } else if ("RGB".equals(tupleType) || "RGB_ALPHA".equals(tupleType)) {
            this.tupleReader = new ColorTupleReader();
        } else {
            throw new ImageReadException("Unknown PAM tupletype '" + tupleType + "'");
        }
    }

    @Override
    public boolean hasAlpha() {
        return this.hasAlpha;
    }

    @Override
    public int getNumComponents() {
        return this.depth;
    }

    @Override
    public int getBitDepth() {
        return this.maxval;
    }

    @Override
    public ImageFormat getImageType() {
        return ImageFormats.PAM;
    }

    @Override
    public String getImageTypeDescription() {
        return "PAM: portable arbitrary map file format";
    }

    @Override
    public String getMIMEType() {
        return "image/x-portable-arbitrary-map";
    }

    @Override
    public ImageInfo.ColorType getColorType() {
        return this.tupleReader.getColorType();
    }

    @Override
    public int getRGB(WhiteSpaceReader wsr) throws IOException {
        throw new UnsupportedOperationException("PAM files are only ever binary");
    }

    @Override
    public int getRGB(InputStream is) throws IOException {
        return this.tupleReader.getRGB(is);
    }

    private class ColorTupleReader
    extends TupleReader {
        private ColorTupleReader() {
        }

        @Override
        public ImageInfo.ColorType getColorType() {
            return ImageInfo.ColorType.RGB;
        }

        @Override
        public int getRGB(InputStream is) throws IOException {
            int red = FileInfo.readSample(is, PamFileInfo.this.bytesPerSample);
            int green = FileInfo.readSample(is, PamFileInfo.this.bytesPerSample);
            int blue = FileInfo.readSample(is, PamFileInfo.this.bytesPerSample);
            red = FileInfo.scaleSample(red, PamFileInfo.this.scale, PamFileInfo.this.maxval);
            green = FileInfo.scaleSample(green, PamFileInfo.this.scale, PamFileInfo.this.maxval);
            blue = FileInfo.scaleSample(blue, PamFileInfo.this.scale, PamFileInfo.this.maxval);
            int alpha = 255;
            if (PamFileInfo.this.hasAlpha) {
                alpha = FileInfo.readSample(is, PamFileInfo.this.bytesPerSample);
                alpha = FileInfo.scaleSample(alpha, PamFileInfo.this.scale, PamFileInfo.this.maxval);
            }
            return (0xFF & alpha) << 24 | (0xFF & red) << 16 | (0xFF & green) << 8 | (0xFF & blue) << 0;
        }
    }

    private class GrayscaleTupleReader
    extends TupleReader {
        private final ImageInfo.ColorType colorType;

        GrayscaleTupleReader(ImageInfo.ColorType colorType) {
            this.colorType = colorType;
        }

        @Override
        public ImageInfo.ColorType getColorType() {
            return this.colorType;
        }

        @Override
        public int getRGB(InputStream is) throws IOException {
            int sample = FileInfo.readSample(is, PamFileInfo.this.bytesPerSample);
            sample = FileInfo.scaleSample(sample, PamFileInfo.this.scale, PamFileInfo.this.maxval);
            int alpha = 255;
            if (PamFileInfo.this.hasAlpha) {
                alpha = FileInfo.readSample(is, PamFileInfo.this.bytesPerSample);
                alpha = FileInfo.scaleSample(alpha, PamFileInfo.this.scale, PamFileInfo.this.maxval);
            }
            return (0xFF & alpha) << 24 | (0xFF & sample) << 16 | (0xFF & sample) << 8 | (0xFF & sample) << 0;
        }
    }

    private abstract class TupleReader {
        private TupleReader() {
        }

        public abstract ImageInfo.ColorType getColorType();

        public abstract int getRGB(InputStream var1) throws IOException;
    }
}

