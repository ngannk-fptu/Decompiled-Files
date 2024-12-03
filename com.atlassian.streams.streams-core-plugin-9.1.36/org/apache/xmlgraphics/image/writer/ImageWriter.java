/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.MultiImageWriter;

public interface ImageWriter {
    public void writeImage(RenderedImage var1, OutputStream var2) throws IOException;

    public void writeImage(RenderedImage var1, OutputStream var2, ImageWriterParams var3) throws IOException;

    public String getMIMEType();

    public boolean isFunctional();

    public boolean supportsMultiImageWriter();

    public MultiImageWriter createMultiImageWriter(OutputStream var1) throws IOException;
}

