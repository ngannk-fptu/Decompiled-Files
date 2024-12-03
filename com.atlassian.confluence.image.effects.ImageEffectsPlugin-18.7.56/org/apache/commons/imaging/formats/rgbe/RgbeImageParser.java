/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.rgbe;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.Raster;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.rgbe.RgbeInfo;

public class RgbeImageParser
extends ImageParser {
    public RgbeImageParser() {
        this.setByteOrder(ByteOrder.BIG_ENDIAN);
    }

    @Override
    public String getName() {
        return "Radiance HDR";
    }

    @Override
    public String getDefaultExtension() {
        return ".hdr";
    }

    @Override
    protected String[] getAcceptedExtensions() {
        return new String[]{".hdr", ".pic"};
    }

    @Override
    protected ImageFormat[] getAcceptedTypes() {
        return new ImageFormat[]{ImageFormats.RGBE};
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        try (RgbeInfo info = new RgbeInfo(byteSource);){
            ImageMetadata ret;
            ImageMetadata imageMetadata = ret = info.getMetadata();
            return imageMetadata;
        }
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        try (RgbeInfo info = new RgbeInfo(byteSource);){
            ImageInfo imageInfo = new ImageInfo(this.getName(), 32, new ArrayList<String>(), ImageFormats.RGBE, this.getName(), info.getHeight(), "image/vnd.radiance", 1, -1, -1.0f, -1, -1.0f, info.getWidth(), false, false, false, ImageInfo.ColorType.RGB, ImageInfo.CompressionAlgorithm.ADAPTIVE_RLE);
            return imageInfo;
        }
    }

    @Override
    public BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        try (RgbeInfo info = new RgbeInfo(byteSource);){
            BufferedImage ret;
            DataBufferFloat buffer = new DataBufferFloat(info.getPixelData(), info.getWidth() * info.getHeight());
            BufferedImage bufferedImage = ret = new BufferedImage(new ComponentColorModel(ColorSpace.getInstance(1000), false, false, 1, buffer.getDataType()), Raster.createWritableRaster(new BandedSampleModel(buffer.getDataType(), info.getWidth(), info.getHeight(), 3), buffer, new Point()), false, null);
            return bufferedImage;
        }
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        try (RgbeInfo info = new RgbeInfo(byteSource);){
            Dimension ret;
            Dimension dimension = ret = new Dimension(info.getWidth(), info.getHeight());
            return dimension;
        }
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }
}

