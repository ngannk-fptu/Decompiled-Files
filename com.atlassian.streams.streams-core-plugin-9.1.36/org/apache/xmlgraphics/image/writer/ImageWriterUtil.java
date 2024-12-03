/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package org.apache.xmlgraphics.image.writer;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.image.writer.ImageWriter;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.ImageWriterRegistry;

public final class ImageWriterUtil {
    private ImageWriterUtil() {
    }

    public static void saveAsPNG(RenderedImage bitmap, File outputFile) throws IOException {
        ImageWriterUtil.saveAsPNG(bitmap, 96, outputFile);
    }

    public static void saveAsPNG(RenderedImage bitmap, int resolution, File outputFile) throws IOException {
        ImageWriterUtil.saveAsFile(bitmap, resolution, outputFile, "image/png");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void saveAsFile(RenderedImage bitmap, int resolution, File outputFile, String mime) throws IOException {
        FileOutputStream out = new FileOutputStream(outputFile);
        try {
            ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor(mime);
            ImageWriterParams params = new ImageWriterParams();
            params.setResolution(resolution);
            writer.writeImage(bitmap, out, params);
        }
        finally {
            IOUtils.closeQuietly((OutputStream)out);
        }
    }
}

