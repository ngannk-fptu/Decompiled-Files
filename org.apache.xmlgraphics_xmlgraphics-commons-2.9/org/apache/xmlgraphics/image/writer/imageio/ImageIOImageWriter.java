/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer.imageio;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.apache.xmlgraphics.image.writer.ImageWriter;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.MultiImageWriter;
import org.apache.xmlgraphics.image.writer.ResolutionUnit;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImageIOImageWriter
implements ImageWriter,
IIOWriteWarningListener {
    private static final String DIMENSION = "Dimension";
    private static final String VERTICAL_PIXEL_SIZE = "VerticalPixelSize";
    private static final String HORIZONTAL_PIXEL_SIZE = "HorizontalPixelSize";
    private static final String STANDARD_METADATA_FORMAT = "javax_imageio_1.0";
    private String targetMIME;

    public ImageIOImageWriter(String mime) {
        this.targetMIME = mime;
    }

    @Override
    public void writeImage(RenderedImage image, OutputStream out) throws IOException {
        this.writeImage(image, out, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeImage(RenderedImage image, OutputStream out, ImageWriterParams params) throws IOException {
        javax.imageio.ImageWriter iiowriter = this.getIIOImageWriter();
        iiowriter.addIIOWriteWarningListener(this);
        ImageOutputStream imgout = ImageIO.createImageOutputStream(out);
        try {
            ImageWriteParam iwParam = this.getDefaultWriteParam(iiowriter, image, params);
            IIOMetadata streamMetadata = this.createStreamMetadata(iiowriter, iwParam, params);
            ImageTypeSpecifier type = iwParam.getDestinationType() != null ? iwParam.getDestinationType() : ImageTypeSpecifier.createFromRenderedImage(image);
            IIOMetadata meta = iiowriter.getDefaultImageMetadata(type, iwParam);
            if (params != null && meta != null) {
                meta = this.updateMetadata(image, meta, params);
            }
            iiowriter.setOutput(imgout);
            IIOImage iioimg = new IIOImage(image, null, meta);
            iiowriter.write(streamMetadata, iioimg, iwParam);
        }
        finally {
            imgout.close();
            iiowriter.dispose();
        }
    }

    protected IIOMetadata createStreamMetadata(javax.imageio.ImageWriter writer, ImageWriteParam writeParam, ImageWriterParams params) {
        return null;
    }

    private javax.imageio.ImageWriter getIIOImageWriter() {
        Iterator<javax.imageio.ImageWriter> iter = ImageIO.getImageWritersByMIMEType(this.getMIMEType());
        javax.imageio.ImageWriter iiowriter = null;
        if (iter.hasNext()) {
            iiowriter = iter.next();
        }
        if (iiowriter == null) {
            throw new UnsupportedOperationException("No ImageIO codec for writing " + this.getMIMEType() + " is available!");
        }
        return iiowriter;
    }

    protected ImageWriteParam getDefaultWriteParam(javax.imageio.ImageWriter iiowriter, RenderedImage image, ImageWriterParams params) {
        ImageWriteParam param = iiowriter.getDefaultWriteParam();
        if (params != null && params.getCompressionMethod() != null) {
            param.setCompressionMode(2);
            param.setCompressionType(params.getCompressionMethod());
        }
        return param;
    }

    protected IIOMetadata updateMetadata(RenderedImage image, IIOMetadata meta, ImageWriterParams params) {
        if (meta.isStandardMetadataFormatSupported() && params.getResolution() != null) {
            float multiplier = ResolutionUnit.CENTIMETER == params.getResolutionUnit() ? 10.0f : 25.4f;
            double pixelWidthInMillimeters = (double)multiplier / params.getXResolution().doubleValue();
            double pixelHeightInMillimeters = (double)multiplier / params.getYResolution().doubleValue();
            this.updatePixelSize(meta, pixelWidthInMillimeters, pixelHeightInMillimeters);
            double checkMerged = this.getHorizontalPixelSize(meta);
            if (!ImageIOImageWriter.equals(checkMerged, pixelWidthInMillimeters, 1.0E-5)) {
                double horzValue = 1.0 / pixelWidthInMillimeters;
                double vertValue = 1.0 / pixelHeightInMillimeters;
                this.updatePixelSize(meta, horzValue, vertValue);
            }
        }
        return meta;
    }

    private static boolean equals(double d1, double d2, double maxDelta) {
        return Math.abs(d1 - d2) <= maxDelta;
    }

    private double getHorizontalPixelSize(IIOMetadata meta) {
        IIOMetadataNode horz;
        double result = 0.0;
        IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(STANDARD_METADATA_FORMAT);
        IIOMetadataNode dim = ImageIOImageWriter.getChildNode(root, DIMENSION);
        if (dim != null && (horz = ImageIOImageWriter.getChildNode(dim, HORIZONTAL_PIXEL_SIZE)) != null) {
            result = Double.parseDouble(horz.getAttribute("value"));
        }
        return result;
    }

    private void updatePixelSize(IIOMetadata meta, double horzValue, double vertValue) {
        IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(STANDARD_METADATA_FORMAT);
        IIOMetadataNode dim = ImageIOImageWriter.getChildNode(root, DIMENSION);
        IIOMetadataNode child = ImageIOImageWriter.getChildNode(dim, HORIZONTAL_PIXEL_SIZE);
        if (child == null) {
            child = new IIOMetadataNode(HORIZONTAL_PIXEL_SIZE);
            dim.appendChild(child);
        }
        child.setAttribute("value", Double.toString(horzValue));
        child = ImageIOImageWriter.getChildNode(dim, VERTICAL_PIXEL_SIZE);
        if (child == null) {
            child = new IIOMetadataNode(VERTICAL_PIXEL_SIZE);
            dim.appendChild(child);
        }
        child.setAttribute("value", Double.toString(vertValue));
        try {
            meta.mergeTree(STANDARD_METADATA_FORMAT, root);
        }
        catch (IIOInvalidTreeException e) {
            throw new RuntimeException("Cannot update image metadata: " + e.getMessage());
        }
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

    @Override
    public String getMIMEType() {
        return this.targetMIME;
    }

    @Override
    public boolean isFunctional() {
        Iterator<javax.imageio.ImageWriter> iter = ImageIO.getImageWritersByMIMEType(this.getMIMEType());
        return iter.hasNext();
    }

    @Override
    public void warningOccurred(javax.imageio.ImageWriter source, int imageIndex, String warning) {
        System.err.println("Problem while writing image using ImageI/O: " + warning);
    }

    @Override
    public MultiImageWriter createMultiImageWriter(OutputStream out) throws IOException {
        return new IIOMultiImageWriter(out);
    }

    @Override
    public boolean supportsMultiImageWriter() {
        javax.imageio.ImageWriter iiowriter = this.getIIOImageWriter();
        try {
            boolean bl = iiowriter.canWriteSequence();
            return bl;
        }
        finally {
            iiowriter.dispose();
        }
    }

    private class IIOMultiImageWriter
    implements MultiImageWriter {
        private javax.imageio.ImageWriter iiowriter;
        private ImageOutputStream imageStream;
        private boolean prepared;

        public IIOMultiImageWriter(OutputStream out) throws IOException {
            this.iiowriter = ImageIOImageWriter.this.getIIOImageWriter();
            if (!this.iiowriter.canWriteSequence()) {
                throw new UnsupportedOperationException("This ImageWriter does not support writing multiple images to a single image file.");
            }
            this.iiowriter.addIIOWriteWarningListener(ImageIOImageWriter.this);
            this.imageStream = ImageIO.createImageOutputStream(out);
            this.iiowriter.setOutput(this.imageStream);
        }

        @Override
        public void writeImage(RenderedImage image, ImageWriterParams params) throws IOException {
            if (this.iiowriter == null) {
                throw new IllegalStateException("MultiImageWriter already closed!");
            }
            ImageWriteParam iwParam = ImageIOImageWriter.this.getDefaultWriteParam(this.iiowriter, image, params);
            if (!this.prepared) {
                IIOMetadata streamMetadata = ImageIOImageWriter.this.createStreamMetadata(this.iiowriter, iwParam, params);
                this.iiowriter.prepareWriteSequence(streamMetadata);
                this.prepared = true;
            }
            ImageTypeSpecifier type = iwParam.getDestinationType() != null ? iwParam.getDestinationType() : ImageTypeSpecifier.createFromRenderedImage(image);
            IIOMetadata meta = this.iiowriter.getDefaultImageMetadata(type, iwParam);
            if (params != null && meta != null) {
                meta = ImageIOImageWriter.this.updateMetadata(image, meta, params);
            }
            IIOImage iioimg = new IIOImage(image, null, meta);
            this.iiowriter.writeToSequence(iioimg, iwParam);
        }

        @Override
        public void close() throws IOException {
            this.imageStream.close();
            this.imageStream = null;
            this.iiowriter.dispose();
            this.iiowriter = null;
        }
    }
}

