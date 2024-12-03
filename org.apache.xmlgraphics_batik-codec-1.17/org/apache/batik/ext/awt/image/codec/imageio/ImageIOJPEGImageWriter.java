/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.ImageWriterParams
 */
package org.apache.batik.ext.awt.image.codec.imageio;

import java.awt.image.RenderedImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import org.apache.batik.ext.awt.image.codec.imageio.ImageIOImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;

public class ImageIOJPEGImageWriter
extends ImageIOImageWriter {
    private static final String JPEG_NATIVE_FORMAT = "javax_imageio_jpeg_image_1.0";

    public ImageIOJPEGImageWriter() {
        super("image/jpeg");
    }

    @Override
    protected IIOMetadata updateMetadata(IIOMetadata meta, ImageWriterParams params) {
        if (JPEG_NATIVE_FORMAT.equals(meta.getNativeMetadataFormatName())) {
            IIOMetadataNode root = (IIOMetadataNode)(meta = ImageIOJPEGImageWriter.addAdobeTransform(meta)).getAsTree(JPEG_NATIVE_FORMAT);
            IIOMetadataNode jv = ImageIOJPEGImageWriter.getChildNode(root, "JPEGvariety");
            if (jv == null) {
                jv = new IIOMetadataNode("JPEGvariety");
                root.appendChild(jv);
            }
            if (params.getResolution() != null) {
                IIOMetadataNode child = ImageIOJPEGImageWriter.getChildNode(jv, "app0JFIF");
                if (child == null) {
                    child = new IIOMetadataNode("app0JFIF");
                    jv.appendChild(child);
                }
                child.setAttribute("majorVersion", null);
                child.setAttribute("minorVersion", null);
                child.setAttribute("resUnits", "1");
                child.setAttribute("Xdensity", params.getResolution().toString());
                child.setAttribute("Ydensity", params.getResolution().toString());
                child.setAttribute("thumbWidth", null);
                child.setAttribute("thumbHeight", null);
            }
            try {
                meta.setFromTree(JPEG_NATIVE_FORMAT, root);
            }
            catch (IIOInvalidTreeException e) {
                throw new RuntimeException("Cannot update image metadata: " + e.getMessage(), e);
            }
        }
        return meta;
    }

    private static IIOMetadata addAdobeTransform(IIOMetadata meta) {
        IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(JPEG_NATIVE_FORMAT);
        IIOMetadataNode markerSequence = ImageIOJPEGImageWriter.getChildNode(root, "markerSequence");
        if (markerSequence == null) {
            throw new RuntimeException("Invalid metadata!");
        }
        IIOMetadataNode adobeTransform = ImageIOJPEGImageWriter.getChildNode(markerSequence, "app14Adobe");
        if (adobeTransform == null) {
            adobeTransform = new IIOMetadataNode("app14Adobe");
            adobeTransform.setAttribute("transform", "1");
            adobeTransform.setAttribute("version", "101");
            adobeTransform.setAttribute("flags0", "0");
            adobeTransform.setAttribute("flags1", "0");
            markerSequence.appendChild(adobeTransform);
        } else {
            adobeTransform.setAttribute("transform", "1");
        }
        try {
            meta.setFromTree(JPEG_NATIVE_FORMAT, root);
        }
        catch (IIOInvalidTreeException e) {
            throw new RuntimeException("Cannot update image metadata: " + e.getMessage(), e);
        }
        return meta;
    }

    @Override
    protected ImageWriteParam getDefaultWriteParam(ImageWriter iiowriter, RenderedImage image, ImageWriterParams params) {
        JPEGImageWriteParam param = new JPEGImageWriteParam(iiowriter.getLocale());
        param.setCompressionMode(2);
        param.setCompressionQuality(params.getJPEGQuality().floatValue());
        if (params.getCompressionMethod() != null && !"JPEG".equals(params.getCompressionMethod())) {
            throw new IllegalArgumentException("No compression method other than JPEG is supported for JPEG output!");
        }
        if (params.getJPEGForceBaseline().booleanValue()) {
            param.setProgressiveMode(0);
        }
        return param;
    }
}

