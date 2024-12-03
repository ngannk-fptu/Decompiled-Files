/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.ImageWriter
 *  org.apache.batik.ext.awt.image.spi.ImageWriterRegistry
 */
package org.apache.batik.svggen;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.DefaultCachedImageHandler;
import org.apache.batik.svggen.ImageCacher;
import org.apache.batik.svggen.SVGGraphics2DIOException;

public class CachedImageHandlerPNGEncoder
extends DefaultCachedImageHandler {
    public static final String CACHED_PNG_PREFIX = "pngImage";
    public static final String CACHED_PNG_SUFFIX = ".png";
    protected String refPrefix = "";

    public CachedImageHandlerPNGEncoder(String imageDir, String urlRoot) throws SVGGraphics2DIOException {
        this.refPrefix = urlRoot + "/";
        this.setImageCacher(new ImageCacher.External(imageDir, CACHED_PNG_PREFIX, CACHED_PNG_SUFFIX));
    }

    @Override
    public void encodeImage(BufferedImage buf, OutputStream os) throws IOException {
        ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/png");
        writer.writeImage((RenderedImage)buf, os);
    }

    @Override
    public int getBufferedImageType() {
        return 2;
    }

    @Override
    public String getRefPrefix() {
        return this.refPrefix;
    }
}

