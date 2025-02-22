/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer.imageio;

import java.awt.image.RenderedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.apache.xmlgraphics.image.writer.Endianness;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.ResolutionUnit;
import org.apache.xmlgraphics.image.writer.imageio.ImageIOImageWriter;
import org.w3c.dom.Node;

public class ImageIOTIFFImageWriter
extends ImageIOImageWriter {
    private static final String SUN_TIFF_NATIVE_FORMAT = "com_sun_media_imageio_plugins_tiff_image_1.0";
    private static final String JAVA_TIFF_NATIVE_FORMAT = "javax_imageio_tiff_image_1.0";
    private static final String SUN_TIFF_NATIVE_STREAM_FORMAT = "com_sun_media_imageio_plugins_tiff_stream_1.0";
    private static final String JAVA_TIFF_NATIVE_STREAM_FORMAT = "javax_imageio_tiff_stream_1.0";
    private static final String DENOMINATOR_CENTIMETER = "/10000";
    private static final String DENOMINATOR_INCH = "/1";

    public ImageIOTIFFImageWriter() {
        super("image/tiff");
    }

    @Override
    protected IIOMetadata updateMetadata(RenderedImage image, IIOMetadata meta, ImageWriterParams params) {
        meta = super.updateMetadata(image, meta, params);
        if (params.getResolution() != null && (SUN_TIFF_NATIVE_FORMAT.equals(meta.getNativeMetadataFormatName()) || JAVA_TIFF_NATIVE_FORMAT.equals(meta.getNativeMetadataFormatName()))) {
            IIOMetadataNode root = new IIOMetadataNode(meta.getNativeMetadataFormatName());
            IIOMetadataNode ifd = ImageIOTIFFImageWriter.getChildNode(root, "TIFFIFD");
            if (ifd == null) {
                ifd = new IIOMetadataNode("TIFFIFD");
                root.appendChild(ifd);
            }
            ifd.appendChild(this.createResolutionUnitField(params));
            ifd.appendChild(this.createResolutionField(282, "XResolution", params.getXResolution(), params.getResolutionUnit()));
            ifd.appendChild(this.createResolutionField(283, "YResolution", params.getYResolution(), params.getResolutionUnit()));
            int rows = params.isSingleStrip() ? image.getHeight() : params.getRowsPerStrip();
            ifd.appendChild(ImageIOTIFFImageWriter.createShortMetadataNode(278, "RowsPerStrip", Integer.toString(rows)));
            try {
                meta.mergeTree(meta.getNativeMetadataFormatName(), root);
            }
            catch (IIOInvalidTreeException e) {
                throw new RuntimeException("Cannot update image metadata: " + e.getMessage(), e);
            }
        }
        return meta;
    }

    private IIOMetadataNode createResolutionField(int number, String name, Integer resolution, ResolutionUnit unit) {
        String value;
        if (unit == ResolutionUnit.INCH) {
            value = resolution + DENOMINATOR_INCH;
        } else {
            float pixSzMM = 25.4f / resolution.floatValue();
            int numPix = (int)((double)(100000.0f / pixSzMM) + 0.5);
            value = numPix + DENOMINATOR_CENTIMETER;
        }
        return ImageIOTIFFImageWriter.createRationalMetadataNode(number, name, value);
    }

    private IIOMetadataNode createResolutionUnitField(ImageWriterParams params) {
        return ImageIOTIFFImageWriter.createShortMetadataNode(296, "ResolutionUnit", Integer.toString(params.getResolutionUnit().getValue()), params.getResolutionUnit().getDescription());
    }

    public static final IIOMetadataNode createShortMetadataNode(int number, String name, String value) {
        return ImageIOTIFFImageWriter.createShortMetadataNode(number, name, value, null);
    }

    public static final IIOMetadataNode createShortMetadataNode(int number, String name, String value, String description) {
        IIOMetadataNode field = ImageIOTIFFImageWriter.createMetadataField(number, name);
        IIOMetadataNode arrayNode = new IIOMetadataNode("TIFFShorts");
        field.appendChild(arrayNode);
        IIOMetadataNode valueNode = new IIOMetadataNode("TIFFShort");
        valueNode.setAttribute("value", value);
        if (description != null) {
            valueNode.setAttribute("description", description);
        }
        arrayNode.appendChild(valueNode);
        return field;
    }

    public static final IIOMetadataNode createRationalMetadataNode(int number, String name, String value) {
        IIOMetadataNode field = ImageIOTIFFImageWriter.createMetadataField(number, name);
        IIOMetadataNode arrayNode = new IIOMetadataNode("TIFFRationals");
        field.appendChild(arrayNode);
        IIOMetadataNode valueNode = new IIOMetadataNode("TIFFRational");
        valueNode.setAttribute("value", value);
        arrayNode.appendChild(valueNode);
        return field;
    }

    public static final IIOMetadataNode createMetadataField(int number, String name) {
        IIOMetadataNode field = new IIOMetadataNode("TIFFField");
        field.setAttribute("number", Integer.toString(number));
        field.setAttribute("name", name);
        return field;
    }

    @Override
    protected IIOMetadata createStreamMetadata(ImageWriter writer, ImageWriteParam writeParam, ImageWriterParams params) {
        Endianness endian;
        Endianness endianness = endian = params != null ? params.getEndianness() : Endianness.DEFAULT;
        if (endian == Endianness.DEFAULT || endian == null) {
            return super.createStreamMetadata(writer, writeParam, params);
        }
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(writeParam);
        if (streamMetadata != null) {
            HashSet<String> names = new HashSet<String>(Arrays.asList(streamMetadata.getMetadataFormatNames()));
            this.setFromTree(names, streamMetadata, endian, SUN_TIFF_NATIVE_STREAM_FORMAT);
            this.setFromTree(names, streamMetadata, endian, JAVA_TIFF_NATIVE_STREAM_FORMAT);
        }
        return streamMetadata;
    }

    private void setFromTree(Set<String> names, IIOMetadata streamMetadata, Endianness endian, String format) {
        if (names.contains(format)) {
            Node root = streamMetadata.getAsTree(format);
            root.getFirstChild().getAttributes().item(0).setNodeValue(endian.toString());
            try {
                streamMetadata.setFromTree(format, root);
            }
            catch (IIOInvalidTreeException e) {
                throw new IllegalStateException("Could not replace TIFF stream metadata: " + e.getMessage(), e);
            }
        }
    }
}

