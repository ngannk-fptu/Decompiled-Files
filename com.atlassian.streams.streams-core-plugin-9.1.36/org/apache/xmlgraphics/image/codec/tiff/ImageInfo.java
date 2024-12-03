/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.tiff;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import org.apache.xmlgraphics.image.codec.tiff.ExtraSamplesType;
import org.apache.xmlgraphics.image.codec.tiff.ImageType;
import org.apache.xmlgraphics.image.codec.tiff.TIFFEncodeParam;

final class ImageInfo {
    private static final int DEFAULT_ROWS_PER_STRIP = 8;
    private final int numExtraSamples;
    private final ExtraSamplesType extraSampleType;
    private final ImageType imageType;
    private final int colormapSize;
    private final char[] colormap;
    private final int tileWidth;
    private final int tileHeight;
    private final int numTiles;
    private final long bytesPerRow;
    private final long bytesPerTile;

    private ImageInfo(ImageInfoBuilder builder) {
        this.numExtraSamples = builder.numExtraSamples;
        this.extraSampleType = builder.extraSampleType;
        this.imageType = builder.imageType;
        this.colormapSize = builder.colormapSize;
        this.colormap = ImageInfo.copyColormap(builder.colormap);
        this.tileWidth = builder.tileWidth;
        this.tileHeight = builder.tileHeight;
        this.numTiles = builder.numTiles;
        this.bytesPerRow = builder.bytesPerRow;
        this.bytesPerTile = builder.bytesPerTile;
    }

    private static char[] copyColormap(char[] colorMap) {
        if (colorMap == null) {
            return null;
        }
        char[] copy = new char[colorMap.length];
        System.arraycopy(colorMap, 0, copy, 0, colorMap.length);
        return copy;
    }

    private static int getNumberOfExtraSamplesForColorSpace(ColorSpace colorSpace, ImageType imageType, int numBands) {
        if (imageType == ImageType.GENERIC) {
            return numBands - 1;
        }
        if (numBands > 1) {
            return numBands - colorSpace.getNumComponents();
        }
        return 0;
    }

    private static char[] createColormap(int sizeOfColormap, byte[] r, byte[] g, byte[] b) {
        int redIndex = 0;
        int greenIndex = sizeOfColormap;
        int blueIndex = 2 * sizeOfColormap;
        char[] colormap = new char[sizeOfColormap * 3];
        for (int i = 0; i < sizeOfColormap; ++i) {
            colormap[redIndex++] = ImageInfo.convertColorToColormapChar(0xFF & r[i]);
            colormap[greenIndex++] = ImageInfo.convertColorToColormapChar(0xFF & g[i]);
            colormap[blueIndex++] = ImageInfo.convertColorToColormapChar(0xFF & b[i]);
        }
        return colormap;
    }

    private static char convertColorToColormapChar(int color) {
        return (char)(color << 8 | color);
    }

    int getNumberOfExtraSamples() {
        return this.numExtraSamples;
    }

    ExtraSamplesType getExtraSamplesType() {
        return this.extraSampleType;
    }

    ImageType getType() {
        return this.imageType;
    }

    int getColormapSize() {
        return this.colormapSize;
    }

    char[] getColormap() {
        return ImageInfo.copyColormap(this.colormap);
    }

    int getTileWidth() {
        return this.tileWidth;
    }

    int getTileHeight() {
        return this.tileHeight;
    }

    int getNumTiles() {
        return this.numTiles;
    }

    long getBytesPerRow() {
        return this.bytesPerRow;
    }

    long getBytesPerTile() {
        return this.bytesPerTile;
    }

    static ImageInfo newInstance(RenderedImage im, int dataTypeSize, int numBands, ColorModel colorModel, TIFFEncodeParam params) {
        ImageInfoBuilder builder = new ImageInfoBuilder();
        if (colorModel instanceof IndexColorModel) {
            IndexColorModel indexColorModel = (IndexColorModel)colorModel;
            int colormapSize = indexColorModel.getMapSize();
            byte[] r = new byte[colormapSize];
            indexColorModel.getReds(r);
            byte[] g = new byte[colormapSize];
            indexColorModel.getGreens(g);
            byte[] b = new byte[colormapSize];
            indexColorModel.getBlues(b);
            builder.imageType = ImageType.getTypeFromRGB(colormapSize, r, g, b, dataTypeSize, numBands);
            if (builder.imageType == ImageType.PALETTE) {
                ImageInfoBuilder.access$402(builder, ImageInfo.createColormap(colormapSize, r, g, b));
                builder.colormapSize = colormapSize * 3;
            }
        } else if (colorModel == null) {
            if (dataTypeSize == 1 && numBands == 1) {
                builder.imageType = ImageType.BILEVEL_BLACK_IS_ZERO;
            } else {
                builder.imageType = ImageType.GENERIC;
                builder.numExtraSamples = numBands > 1 ? numBands - 1 : 0;
            }
        } else {
            ColorSpace colorSpace = colorModel.getColorSpace();
            builder.imageType = ImageType.getTypeFromColorSpace(colorSpace, params);
            builder.numExtraSamples = ImageInfo.getNumberOfExtraSamplesForColorSpace(colorSpace, builder.imageType, numBands);
            builder.extraSampleType = ExtraSamplesType.getValue(colorModel, builder.numExtraSamples);
        }
        int width = im.getWidth();
        int height = im.getHeight();
        if (params.getWriteTiled()) {
            builder.tileWidth = params.getTileWidth() > 0 ? params.getTileWidth() : width;
            builder.tileHeight = params.getTileHeight() > 0 ? params.getTileHeight() : height;
            builder.numTiles = (width + builder.tileWidth - 1) / builder.tileWidth * ((height + builder.tileHeight - 1) / builder.tileHeight);
        } else {
            builder.tileWidth = width;
            builder.tileHeight = params.getTileHeight() > 0 ? params.getTileHeight() : 8;
            builder.numTiles = (int)Math.ceil((double)height / (double)builder.tileHeight);
        }
        builder.setBytesPerRow(dataTypeSize, numBands).setBytesPerTile();
        return builder.build();
    }

    private static final class ImageInfoBuilder {
        private ImageType imageType = ImageType.UNSUPPORTED;
        private int numExtraSamples;
        private char[] colormap;
        private int colormapSize;
        private ExtraSamplesType extraSampleType = ExtraSamplesType.UNSPECIFIED;
        private int tileWidth;
        private int tileHeight;
        private int numTiles;
        private long bytesPerRow;
        private long bytesPerTile;

        private ImageInfoBuilder() {
        }

        private ImageInfoBuilder setBytesPerRow(int dataTypeSize, int numBands) {
            this.bytesPerRow = (long)Math.ceil((double)dataTypeSize / 8.0 * (double)this.tileWidth * (double)numBands);
            return this;
        }

        private ImageInfoBuilder setBytesPerTile() {
            this.bytesPerTile = this.bytesPerRow * (long)this.tileHeight;
            return this;
        }

        private ImageInfo build() {
            return new ImageInfo(this);
        }

        static /* synthetic */ char[] access$402(ImageInfoBuilder x0, char[] x1) {
            x0.colormap = x1;
            return x1;
        }
    }
}

