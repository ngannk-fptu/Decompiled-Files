/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.formats.png.PhysicalScale;
import org.apache.commons.imaging.formats.png.PngText;

public class PngImageInfo
extends ImageInfo {
    private final List<PngText> textChunks;
    private final PhysicalScale physicalScale;

    PngImageInfo(String formatDetails, int bitsPerPixel, List<String> comments, ImageFormat format, String formatName, int height, String mimeType, int numberOfImages, int physicalHeightDpi, float physicalHeightInch, int physicalWidthDpi, float physicalWidthInch, int width, boolean progressive, boolean transparent, boolean usesPalette, ImageInfo.ColorType colorType, ImageInfo.CompressionAlgorithm compressionAlgorithm, List<PngText> textChunks, PhysicalScale physicalScale) {
        super(formatDetails, bitsPerPixel, comments, format, formatName, height, mimeType, numberOfImages, physicalHeightDpi, physicalHeightInch, physicalWidthDpi, physicalWidthInch, width, progressive, transparent, usesPalette, colorType, compressionAlgorithm);
        this.textChunks = textChunks;
        this.physicalScale = physicalScale;
    }

    public List<PngText> getTextChunks() {
        return new ArrayList<PngText>(this.textChunks);
    }

    public PhysicalScale getPhysicalScale() {
        return this.physicalScale;
    }
}

