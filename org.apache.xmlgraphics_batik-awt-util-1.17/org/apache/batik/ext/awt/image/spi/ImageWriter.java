/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.spi;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;

public interface ImageWriter {
    public void writeImage(RenderedImage var1, OutputStream var2) throws IOException;

    public void writeImage(RenderedImage var1, OutputStream var2, ImageWriterParams var3) throws IOException;

    public String getMIMEType();
}

