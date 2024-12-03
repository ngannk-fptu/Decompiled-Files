/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer;

import java.awt.image.RenderedImage;
import java.io.IOException;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;

public interface MultiImageWriter {
    public void writeImage(RenderedImage var1, ImageWriterParams var2) throws IOException;

    public void close() throws IOException;
}

