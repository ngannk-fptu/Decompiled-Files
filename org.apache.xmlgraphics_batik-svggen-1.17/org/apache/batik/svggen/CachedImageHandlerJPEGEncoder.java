/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.ImageWriter
 *  org.apache.batik.ext.awt.image.spi.ImageWriterParams
 *  org.apache.batik.ext.awt.image.spi.ImageWriterRegistry
 */
package org.apache.batik.svggen;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.DefaultCachedImageHandler;
import org.apache.batik.svggen.ImageCacher;
import org.apache.batik.svggen.SVGGraphics2DIOException;

public class CachedImageHandlerJPEGEncoder
extends DefaultCachedImageHandler {
    public static final String CACHED_JPEG_PREFIX = "jpegImage";
    public static final String CACHED_JPEG_SUFFIX = ".jpg";
    protected String refPrefix = "";

    public CachedImageHandlerJPEGEncoder(String imageDir, String urlRoot) throws SVGGraphics2DIOException {
        this.refPrefix = urlRoot + "/";
        this.setImageCacher(new ImageCacher.External(imageDir, CACHED_JPEG_PREFIX, CACHED_JPEG_SUFFIX));
    }

    @Override
    public void encodeImage(BufferedImage buf, OutputStream os) throws IOException {
        ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/jpeg");
        ImageWriterParams params = new ImageWriterParams();
        params.setJPEGQuality(1.0f, false);
        writer.writeImage((RenderedImage)buf, os, params);
    }

    @Override
    public int getBufferedImageType() {
        return 1;
    }

    @Override
    public String getRefPrefix() {
        return this.refPrefix;
    }
}

