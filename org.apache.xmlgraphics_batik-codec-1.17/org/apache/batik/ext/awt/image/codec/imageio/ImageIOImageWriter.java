/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.ImageWriter
 *  org.apache.batik.ext.awt.image.spi.ImageWriterParams
 */
package org.apache.batik.ext.awt.image.codec.imageio;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImageIOImageWriter
implements org.apache.batik.ext.awt.image.spi.ImageWriter,
IIOWriteWarningListener {
    private String targetMIME;

    public ImageIOImageWriter(String mime) {
        this.targetMIME = mime;
    }

    public void writeImage(RenderedImage image, OutputStream out) throws IOException {
        this.writeImage(image, out, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeImage(RenderedImage image, OutputStream out, ImageWriterParams params) throws IOException {
        block10: {
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByMIMEType(this.getMIMEType());
            ImageWriter iiowriter = null;
            try {
                iiowriter = iter.next();
                if (iiowriter != null) {
                    iiowriter.addIIOWriteWarningListener(this);
                    try (ImageOutputStream imgout = null;){
                        imgout = ImageIO.createImageOutputStream(out);
                        ImageWriteParam iwParam = this.getDefaultWriteParam(iiowriter, image, params);
                        ImageTypeSpecifier type = iwParam.getDestinationType() != null ? iwParam.getDestinationType() : ImageTypeSpecifier.createFromRenderedImage(image);
                        IIOMetadata meta = iiowriter.getDefaultImageMetadata(type, iwParam);
                        if (params != null && meta != null) {
                            meta = this.updateMetadata(meta, params);
                        }
                        iiowriter.setOutput(imgout);
                        IIOImage iioimg = new IIOImage(image, null, meta);
                        iiowriter.write(null, iioimg, iwParam);
                        break block10;
                    }
                }
                throw new UnsupportedOperationException("No ImageIO codec for writing " + this.getMIMEType() + " is available!");
            }
            finally {
                if (iiowriter != null) {
                    iiowriter.dispose();
                }
            }
        }
    }

    protected ImageWriteParam getDefaultWriteParam(ImageWriter iiowriter, RenderedImage image, ImageWriterParams params) {
        ImageWriteParam param = iiowriter.getDefaultWriteParam();
        if (params != null && params.getCompressionMethod() != null) {
            param.setCompressionMode(2);
            param.setCompressionType(params.getCompressionMethod());
        }
        return param;
    }

    protected IIOMetadata updateMetadata(IIOMetadata meta, ImageWriterParams params) {
        String stdmeta = "javax_imageio_1.0";
        if (meta.isStandardMetadataFormatSupported()) {
            IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree("javax_imageio_1.0");
            IIOMetadataNode dim = ImageIOImageWriter.getChildNode(root, "Dimension");
            if (params.getResolution() != null) {
                IIOMetadataNode child = ImageIOImageWriter.getChildNode(dim, "HorizontalPixelSize");
                if (child == null) {
                    child = new IIOMetadataNode("HorizontalPixelSize");
                    dim.appendChild(child);
                }
                child.setAttribute("value", Double.toString(params.getResolution().doubleValue() / 25.4));
                child = ImageIOImageWriter.getChildNode(dim, "VerticalPixelSize");
                if (child == null) {
                    child = new IIOMetadataNode("VerticalPixelSize");
                    dim.appendChild(child);
                }
                child.setAttribute("value", Double.toString(params.getResolution().doubleValue() / 25.4));
            }
            try {
                meta.mergeTree("javax_imageio_1.0", root);
            }
            catch (IIOInvalidTreeException e) {
                throw new RuntimeException("Cannot update image metadata: " + e.getMessage());
            }
        }
        return meta;
    }

    protected static IIOMetadataNode getChildNode(Node n, String name) {
        NodeList nodes = n.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node child = nodes.item(i);
            if (!name.equals(child.getNodeName())) continue;
            return (IIOMetadataNode)child;
        }
        return null;
    }

    public String getMIMEType() {
        return this.targetMIME;
    }

    @Override
    public void warningOccurred(ImageWriter source, int imageIndex, String warning) {
        System.err.println("Problem while writing image using ImageI/O: " + warning);
    }
}

