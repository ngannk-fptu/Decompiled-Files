/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.ImageWriter
 *  org.apache.batik.ext.awt.image.spi.ImageWriterRegistry
 *  org.apache.batik.util.Base64EncoderStream
 */
package org.apache.batik.svggen;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.DefaultImageHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.util.Base64EncoderStream;
import org.w3c.dom.Element;

public class ImageHandlerBase64Encoder
extends DefaultImageHandler {
    @Override
    public void handleHREF(Image image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (image == null) {
            throw new SVGGraphics2DRuntimeException("image should not be null");
        }
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        if (width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        } else if (image instanceof RenderedImage) {
            this.handleHREF((RenderedImage)((Object)image), imageElement, generatorContext);
        } else {
            BufferedImage buf = new BufferedImage(width, height, 2);
            Graphics2D g = buf.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            this.handleHREF(buf, imageElement, generatorContext);
        }
    }

    @Override
    public void handleHREF(RenderableImage image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (image == null) {
            throw new SVGGraphics2DRuntimeException("image should not be null");
        }
        RenderedImage r = image.createDefaultRendering();
        if (r == null) {
            this.handleEmptyImage(imageElement);
        } else {
            this.handleHREF(r, imageElement, generatorContext);
        }
    }

    protected void handleEmptyImage(Element imageElement) {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "data:image/png;base64,");
        imageElement.setAttributeNS(null, "width", "0");
        imageElement.setAttributeNS(null, "height", "0");
    }

    @Override
    public void handleHREF(RenderedImage image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Base64EncoderStream b64Encoder = new Base64EncoderStream((OutputStream)os);
        try {
            this.encodeImage(image, (OutputStream)b64Encoder);
            b64Encoder.close();
        }
        catch (IOException e) {
            throw new SVGGraphics2DIOException("unexpected exception", e);
        }
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "data:image/png;base64," + os.toString());
    }

    public void encodeImage(RenderedImage buf, OutputStream os) throws SVGGraphics2DIOException {
        try {
            ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/png");
            writer.writeImage(buf, os);
        }
        catch (IOException e) {
            throw new SVGGraphics2DIOException("unexpected exception");
        }
    }

    public BufferedImage buildBufferedImage(Dimension size) {
        return new BufferedImage(size.width, size.height, 2);
    }
}

