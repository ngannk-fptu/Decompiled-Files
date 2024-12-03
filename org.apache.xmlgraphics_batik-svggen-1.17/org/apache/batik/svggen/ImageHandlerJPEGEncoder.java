/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.ImageWriter
 *  org.apache.batik.ext.awt.image.spi.ImageWriterParams
 *  org.apache.batik.ext.awt.image.spi.ImageWriterRegistry
 */
package org.apache.batik.svggen;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.AbstractImageHandlerEncoder;
import org.apache.batik.svggen.SVGGraphics2DIOException;

public class ImageHandlerJPEGEncoder
extends AbstractImageHandlerEncoder {
    public ImageHandlerJPEGEncoder(String imageDir, String urlRoot) throws SVGGraphics2DIOException {
        super(imageDir, urlRoot);
    }

    @Override
    public final String getSuffix() {
        return ".jpg";
    }

    @Override
    public final String getPrefix() {
        return "jpegImage";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void encodeImage(BufferedImage buf, File imageFile) throws SVGGraphics2DIOException {
        try (FileOutputStream os = new FileOutputStream(imageFile);){
            ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/jpeg");
            ImageWriterParams params = new ImageWriterParams();
            params.setJPEGQuality(1.0f, false);
            writer.writeImage((RenderedImage)buf, (OutputStream)os, params);
        }
        catch (IOException e) {
            throw new SVGGraphics2DIOException("could not write image File " + imageFile.getName());
        }
    }

    @Override
    public BufferedImage buildBufferedImage(Dimension size) {
        return new BufferedImage(size.width, size.height, 1);
    }
}

