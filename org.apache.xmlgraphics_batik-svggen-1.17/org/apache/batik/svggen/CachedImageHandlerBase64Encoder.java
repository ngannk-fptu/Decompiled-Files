/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.ImageWriter
 *  org.apache.batik.ext.awt.image.spi.ImageWriterRegistry
 *  org.apache.batik.util.Base64EncoderStream
 */
package org.apache.batik.svggen;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.DefaultCachedImageHandler;
import org.apache.batik.svggen.ImageCacher;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.util.Base64EncoderStream;
import org.w3c.dom.Element;

public class CachedImageHandlerBase64Encoder
extends DefaultCachedImageHandler {
    public CachedImageHandlerBase64Encoder() {
        this.setImageCacher(new ImageCacher.Embedded());
    }

    @Override
    public Element createElement(SVGGeneratorContext generatorContext) {
        Element imageElement = generatorContext.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "use");
        return imageElement;
    }

    @Override
    public String getRefPrefix() {
        return "";
    }

    @Override
    protected AffineTransform handleTransform(Element imageElement, double x, double y, double srcWidth, double srcHeight, double dstWidth, double dstHeight, SVGGeneratorContext generatorContext) {
        AffineTransform af = new AffineTransform();
        double hRatio = dstWidth / srcWidth;
        double vRatio = dstHeight / srcHeight;
        af.translate(x, y);
        if (hRatio != 1.0 || vRatio != 1.0) {
            af.scale(hRatio, vRatio);
        }
        if (!af.isIdentity()) {
            return af;
        }
        return null;
    }

    @Override
    public void encodeImage(BufferedImage buf, OutputStream os) throws IOException {
        Base64EncoderStream b64Encoder = new Base64EncoderStream(os);
        ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/png");
        writer.writeImage((RenderedImage)buf, (OutputStream)b64Encoder);
        b64Encoder.close();
    }

    @Override
    public int getBufferedImageType() {
        return 2;
    }
}

